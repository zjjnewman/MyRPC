package nio;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一、使用NIO 完成网络通信的三个核心
 *      (注：FileChannel 不支持设置成 阻塞IO)
 *      1.通道（Channel）：负责连接
 *          java.io.channels.Channel 接口：
 *              |--SelectableChannel  Channel接口的抽象实现类
 *                  (selector轮询下面之类，用于网络IO)
 *                  |--SocketChannel   子类
 *                  |--ServerSocketChannel
 *                  |--DatagramChannel
 *
 *                  |--Pipe.SinkChannel
 *                  |--Pipe.SourceChannel
 *
 *      2.缓冲区（Buffer）：负责数据的存取
 *
 *      3.选择器（Selector）：是SelectableChannel 的多路复用器，用于监控 SelectableChannel 的 IO 状况
 *
 */
public class TestBlockingNIO {

    private ByteBuffer byteBuffer;

    @Test
    public void client() throws Exception{
        // 1.获取客户端通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));

        // 要读取本地文件，用FileChannel
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpeg"), StandardOpenOption.READ);

        // 2.分配指定大小缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 3.读取本地文件，并发送到服务端
        while (inChannel.read(byteBuffer) != -1){
            // 读模式
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        // 4.关闭通道
        inChannel.close();
        socketChannel.close();

    }

    @Test
    public void server() throws Exception{
        // 1.获取网络通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        FileChannel outChannel = FileChannel.open(Paths.get("2.jpeg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        // 2.绑定连接端口号
        serverSocketChannel.bind(new InetSocketAddress(9898));

        // 3.获取客户端连接通道
        SocketChannel socketChannel = serverSocketChannel.accept();

        // 4.分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 5.接受客户端的数据并保存到本地，
        while (socketChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            outChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        // 关闭channel
        serverSocketChannel.close();
        socketChannel.close();
        outChannel.close();
    }

}
