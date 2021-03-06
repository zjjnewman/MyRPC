package rpc09_nio.provider;

import com.caucho.hessian.io.Hessian2StreamingInput;
import com.caucho.hessian.io.Hessian2StreamingOutput;
import rpc06.Server;

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;

public class Provider {
    static HashMap<String, Class<?>> cache = new HashMap<>();
    static {
        cache.put("rpc.common.UserService", UserServiceImpl.class);
        cache.put("rpc.common.ProductService", ProductServiceImpl.class);
    }

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8888));
        // 接受连接过来的socket，用输入流接收数据
        process(serverSocketChannel);
    }

    private static void process(ServerSocketChannel serverSocketChannel) throws Exception {

        Selector selector = Selector.open();
        // 指定监听事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        int size = selector.selectedKeys().size();
        System.out.println("selector.selectedKeys().size(): "+selector.selectedKeys().size());

        while (selector.select() > 0){

            if(selector.selectedKeys().size() != size){
                size = selector.selectedKeys().size();
                System.out.println("selector.selectedKeys().size(): "+size);
            }

            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

            while (keyIterator.hasNext()){

                SelectionKey selectionKey = keyIterator.next();

                if(selectionKey.isAcceptable()){

                    SocketChannel socketChannel = serverSocketChannel.accept();

                    socketChannel.configureBlocking(false);

                    socketChannel.register(selector, SelectionKey.OP_READ);

                } else if(selectionKey.isReadable()){

                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    // 这个阶段是获取对象的字节流准备阶段
                    ByteBuffer byteBuffer = ByteBuffer.allocate(4);

                    // 这一步还是阻塞的，所以要阻塞查看是不是有数据来缓冲区
                    while(socketChannel.read(byteBuffer) > 0){
                        byteBuffer.flip();
                        int receivedLen = byteBuffer.getInt();
                        System.out.println("provider receivedLen: " + receivedLen);
                        byteBuffer = ByteBuffer.allocate(receivedLen);
                        // 以下是吧对象读入缓冲区
                        socketChannel.read(byteBuffer);
                        byteBuffer.flip();

                        byte[] data = byteBuffer.array();

                        ByteArrayInputStream bis = new ByteArrayInputStream(data);
                        Hessian2StreamingInput hsi = new Hessian2StreamingInput(bis);
                        // 获取元数据
                        // 通过接口名获取实现类
                        String interfaceName = (String) hsi.readObject();
                        String methodName = (String) hsi.readObject();
                        Class<?>[] parameterTypes = (Class<?>[]) hsi.readObject();
                        Object[] parameters = (Object[]) hsi.readObject();

                        System.out.println("interfaceName: " + interfaceName);

                        // 通过接口名获取实现类原信息 强制转换成响应的实现类
                        Class<?> clazz = cache.get(interfaceName);
                        Method method = clazz.getMethod(methodName, parameterTypes);
                        Object o = method.invoke(clazz.newInstance(), parameters);
                        // 把数据通过socket 返回给消费者
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        Hessian2StreamingOutput hso = new Hessian2StreamingOutput(bos);
                        hso.writeObject(o);

                        byte[] sendBytes = bos.toByteArray();
                        int sendLen = sendBytes.length;
                        System.out.println("provider sendLen: "+sendLen);
                        byteBuffer = ByteBuffer.allocate(4 + sendLen);
                        byteBuffer.putInt(sendLen);
                        byteBuffer.put(sendBytes);
                        byteBuffer.flip();
                        socketChannel.write(byteBuffer);
                        System.out.println(o);
                        byteBuffer.clear();
                    }
                }
                // 这里如果不移除会报NullPointerException 异常 具体在107行 socketChannel.configureBlocking(false);
                keyIterator.remove();
            }
        }
    }

    private static void processOPRead(SelectionKey selectionKey){

    }
}
