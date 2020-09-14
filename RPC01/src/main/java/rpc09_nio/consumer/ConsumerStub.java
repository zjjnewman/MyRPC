package rpc09_nio.consumer;

import com.caucho.hessian.io.*;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ConsumerStub {
    static Jedis jedis = new Jedis("remotehost0", 6379);

    public static Object getConsumerStub(Class<?> clazz) throws Exception{
        // 创建匿名《调用处理对象》
        InvocationHandler consumerInvocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                //  获取消费者元数据 获取接口名
                String interfaceName = clazz.getName();
                System.out.println("interfaceName: "+interfaceName);
                // 获取方法名
                String methodName = method.getName();
                // 获取参数类型
                Class<?>[] parameterTypes = method.getParameterTypes();

                // 从注册中心获取服务提供者 host port
                String providerInfo = jedis.get(interfaceName);
                System.out.println("providerMeta： "+providerInfo);
                String[] hostAndPort = providerInfo.split(":");

                // 用获取到的host port，连接服务提供者
                SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
                socketChannel.configureBlocking(false);

                // 这一步是 通过流中转成 字节 然后 写入 nio的bytebuffer
                // 告诉服务提供者诉求元数据
                ByteArrayOutputStream bos =new ByteArrayOutputStream();
                Hessian2StreamingOutput hso = new Hessian2StreamingOutput(bos);
                hso.writeObject(interfaceName);
                hso.writeObject(methodName);
                hso.writeObject(parameterTypes);
                hso.writeObject(args);

                byte[] sendBytes = bos.toByteArray();
                int sendLen = sendBytes.length;
                System.out.println("sendLen: " + sendLen);
                ByteBuffer byteBuffer = ByteBuffer.allocate(4 + sendLen);
                byteBuffer.putInt(sendLen);
                byteBuffer.put(sendBytes);
                // 切换成读模式 本质就是处理指针放到开头；
                byteBuffer.flip();
                socketChannel.write(byteBuffer);


                // 接受结果
                byteBuffer = ByteBuffer.allocate(4);
                while (!(socketChannel.read(byteBuffer) > 0)){
                }
                byteBuffer.flip();
                int receiveLen = byteBuffer.getInt();
                System.out.println("consumer receiveLen: " + receiveLen);
                byteBuffer = ByteBuffer.allocate(receiveLen);
                socketChannel.read(byteBuffer);
                byteBuffer.flip();
                byte[] receivedByte = byteBuffer.array();
                ByteArrayInputStream bis = new ByteArrayInputStream(receivedByte);
                Hessian2StreamingInput hsi = new Hessian2StreamingInput(bis);
                Object o = hsi.readObject();

                hso.close();
                hsi.close();
                return o;
            }
        };

        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, consumerInvocationHandler);
    }
}
