package nio;

import org.junit.jupiter.api.Test;

import javax.naming.ldap.SortKey;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.*;

public class TestNonBlockingNIO {

    @Test
    public void client1() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(3);
        ExecutorService executorService = new ThreadPoolExecutor(
                2,
                3,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        try {
            for (int i = 0; i < 3; i++) {
                Future future = executorService.submit(()->{
                    try {
                        client();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                });
                System.out.println(future.get());
            }
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            executorService.shutdownNow();
        }
    }

    /**
     * 客户端
     * 可以同时多个客户端对，服务端通道发送数据，服务端表现为，轮询获取选择键（实时新增）
     */
    @Test
    public void client() throws IOException {
        // 获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));

        // 设置成非阻塞
        socketChannel.configureBlocking(false);

        // 分配缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 放入数据（可以改为循环放入数据）
        Scanner scanner = new Scanner(System.in);
//        while (scanner.hasNext()){
//            byteBuffer.put((LocalDateTime.now().toString() + "\n").getBytes());
//            byteBuffer.put(scanner.next().getBytes());
//            // 切换缓冲区读模式
//            byteBuffer.flip();
//
//            // 把缓冲区数据 写入通道
//            socketChannel.write(byteBuffer);
//
//            // 清空缓冲区
//            byteBuffer.clear();
//        }

        byteBuffer.put((LocalDateTime.now().toString() + "\n").getBytes());
        byteBuffer.put(scanner.next().getBytes());

        // 关闭通道
        socketChannel.close();
    }


    /**
     * 服务端
     * @throws IOException
     */
    @Test
    public void server() throws IOException {
        // 获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 设置非阻塞
        serverSocketChannel.configureBlocking(false);

        // 绑定通道端口号
        serverSocketChannel.bind(new InetSocketAddress(8888));

        // 创建选择器
        Selector selector = Selector.open();

        // 把通道注册到选择器，并指定"监听接收事件"
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // selector循环检测通道是否就绪，轮询式的获取选择器上已经"准备就绪的事件"
        while (selector.select() > 0){
            // 有就绪通道，就获取就绪通道集合的迭代器
            // 获取当前选择器中所有的已经注册的选择键（已就绪的监听事件）
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

            // 迭代每一个selectionKey
            while (keyIterator.hasNext()){
                // get a selected selectionKey
                // 8.获取准备"就绪"的事件
                SelectionKey selectionKey = keyIterator.next();
                // judge the key whether be a acceptable key
                // 9.判断具体是什么事件准备就绪
                if (selectionKey.isAcceptable()){
                    // get the key-selected Channel
                    // 10.若"接受就绪"就获取客户端的连接
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    // 11.切换到非阻塞模式
                    socketChannel.configureBlocking(false);

                    // register the channel into the selector
                    // 12.有客户端的"接受就绪"通道，注册到选择器上，注册 监听"读就绪"状态
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if(selectionKey.isReadable()){
                    // 13.获取当前选择器上"读就绪"状态的通道
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    // 14.读取数据
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                    int len = 0;
                    // 读缓冲区，若有数据，打印
                    while ((len = socketChannel.read(byteBuffer)) > 0){
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(), 0, len));
                        byteBuffer.clear();
                    }
                } else if(selectionKey.isWritable()){

                } else if (selectionKey.isValid()){

                }
                // 15.接受完其中一个通道的数据，就取消其选择键，从轮询列表里面移除，否则就会一直有效。
                keyIterator.remove();
            }

        }
    }
}
