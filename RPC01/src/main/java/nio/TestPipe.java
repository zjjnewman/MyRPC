package nio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.time.LocalDate;

/**
 * 管道是单向的
 */
public class TestPipe {

    @Test
    public void test1() throws IOException {
        // 获取管道
        Pipe pipe = Pipe.open();

        // 分配缓冲区，放入数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(LocalDate.now().toString().getBytes());

        /**
         * 管道是单向的，所以一个管道有两个 channel：
         *      1.SinkChannel 用于把数据 输入 管道
         *      2.SourceChannel 用于把数据 输出管道
         */
        Pipe.SinkChannel sinkChannel = pipe.sink();

        byteBuffer.flip();
        // 把数据写入管道
        sinkChannel.write(byteBuffer);

        // 把数据从管道读出
        Pipe.SourceChannel sourceChannel = pipe.source();
        byteBuffer.flip();

        int len = sourceChannel.read(byteBuffer);

        System.out.println(new String(byteBuffer.array(),0,len));

        sourceChannel.close();
        sinkChannel.close();
    }
}
