package nio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class TestNonBlockingNIO2 {

    /**
     * 发送端
     */
    @Test
    public void send() throws IOException {
        // 获取udp通道
        DatagramChannel datagramChannel = DatagramChannel.open();

        datagramChannel.configureBlocking(false);

        // 分配缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 把数据写入缓冲区
        byteBuffer.put("数据".getBytes());

        // ______________注意这一步老是忘----------------
        byteBuffer.flip();

        // 通过通道 把缓冲区中的数据发送出去
        datagramChannel.send(byteBuffer, new InetSocketAddress("127.0.0.1", 9898));

        datagramChannel.close();
    }

    /**
     * 接收端
     */
    @Test
    public void receive() throws IOException {

        // 获取通道
        DatagramChannel datagramChannel = DatagramChannel.open();

        datagramChannel.configureBlocking(false);

        // 把通道绑定到端口号
        datagramChannel.bind(new InetSocketAddress(9898));

        // 获取选择器
        Selector selector = Selector.open();

        // 注册通道到selector
        datagramChannel.register(selector, SelectionKey.OP_READ);

        while (selector.select() > 0){

            Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
            while (selectionKeyIterator.hasNext()){
                SelectionKey selectionKey = selectionKeyIterator.next();

                // 因为是udp连接不用 有建立连接的过程，所以不用 acceptable()方法
                if (selectionKey.isReadable()){

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                    // 把数据接收到缓冲区
                    datagramChannel.receive(byteBuffer);

                    // 缓冲区有了数据，就读取出来
                    byteBuffer.flip();
                    System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit() ));
                    byteBuffer.clear();
                }
                selectionKeyIterator.remove();
            }
        }


    }


}
