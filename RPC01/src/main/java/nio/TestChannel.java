package nio;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.SortedMap;

/**
 * nid 本地IO
 * @author jin
 *
 * 一、通过（channel）：用于源节点与目标节点的连接，在Java NIO中负责缓冲区数据的传输。channel本身不存储数据需要配合缓冲区进行传输。
 *
 * 二、通道的主要实现类：
 *      java.nio.channels.Channel 接口：
 *      |--FileChannel
 *      |--SocketChannel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 *
 * 三、获取 通道对象 的 3 种方式：
 *      1. Java 针对支持通道的类提供了 getChannel() 方法
 *          本地IO:
 *              {@link FileInputStream}
 *              {@link FileOutputStream}
 *              {@link RandomAccessFile}
 *          网络IO:
 *              {@link Socket}
 *              {@link ServerSocket}
 *              {@link DatagramSocket}
 *
 *      2. 在 JDK1.7 中的 NIO.2 针对各个通道提供了静态方法 open()
 *      3. 在 JDK1.7 中的 NIO.2 的Files工具类的 newByteChannel()
 *
 * 四、通道之间的数据传输
 *      transferFrom()
 *      transferto()
 *
 * 五、分散（Scatter）与聚集（Gather）
 *      分散读取（Scattering Reads）：将通道中的数据分散到多个缓冲区中
 *      聚集写入（Gathering Writes）：将多个缓冲区中的数据聚集到通道中
 *
 * 六、字符集：CharSet (说白了，编码解码是 ByteBuffer 与 CharBuffer之间的转换)
 *      编码：字符串 -> 字节数组
 *      解码：字节数组 -> 字符串
 */
public class TestChannel {

    /**
     * nio中提供的 字符集接口，用于编码解码
     */
    @Test
    public void test6() throws Exception {

        Charset gbk = Charset.forName("GBK");

        // 获取编码器
        CharsetEncoder charsetEncoder = gbk.newEncoder();

        // 获取解码器
        CharsetDecoder charsetDecoder = gbk.newDecoder();

        // 获取字符缓冲区，并放入字符串
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("测试");

        // 读上面的缓冲区
        charBuffer.flip();

        // 编码，（通过下面的输入，猜想编码的时候吧limit也copy过来了）
        ByteBuffer encodeByteBuffer = charsetEncoder.encode(charBuffer);

        System.out.println(encodeByteBuffer.position());
        System.out.println(encodeByteBuffer.limit());
        System.out.println(encodeByteBuffer.capacity());
        for (int i = 0; i < encodeByteBuffer.limit(); i++) {
            System.out.println(encodeByteBuffer.get());
        }

        // 读编码缓冲区
        encodeByteBuffer.flip();

        // 解码
        CharBuffer decodeCharBuffer = charsetDecoder.decode(encodeByteBuffer);
        System.out.println(decodeCharBuffer.position());
        System.out.println(decodeCharBuffer.limit());
        System.out.println(decodeCharBuffer.capacity());
        for (int i = 0; i < decodeCharBuffer.limit(); i++) {
            System.out.println(decodeCharBuffer.get());

        }

    }


    // 遍历字符集
    @Test
    public void test5(){
        SortedMap<String, Charset> stringCharsetSortedMap = Charset.availableCharsets();

        for(SortedMap.Entry entry : stringCharsetSortedMap.entrySet()){
            System.out.println(entry.getKey());
        }

    }


    /**
     * 分散和聚集
     */
    @Test
    public void test4() throws Exception {
        RandomAccessFile raf1 = new RandomAccessFile("1.txt", "rw");

        // 1.获取通道
        FileChannel channel1 = raf1.getChannel();

        // 2.分配指定大小的缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);

        // 3.分散读取
        ByteBuffer[] buffs = {buf1, buf2};
        channel1.read(buffs);

        for (ByteBuffer buff : buffs) {
            buff.flip();
        }

        System.out.println(new String(buffs[0].array(), 0, buffs[0].limit()));
        System.out.println("------------------------");
        System.out.println(new String(buffs[1].array(), 0, buffs[1].limit()));

        // 4.聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("2.txt", "rw");
        FileChannel channel2 = raf2.getChannel();

        channel2.write(buffs);

    }


    /**
     * 通道之间的数据传输 (直接缓冲区)
     */
    @Test
    public void test3() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpeg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.jpeg"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        // inChannel.transferTo(0, inChannel.size(), outChannel);
        outChannel.transferFrom(inChannel, 0, inChannel.size());

        inChannel.close();
        outChannel.close();
    }


    /**
     * 2.使用直接缓冲区文件文成文件的复制（直接内存映射文件）
     */
    @Test
    public void test() throws IOException {
        // Paths是nio.file提供的工具类，返回path，参数可变，可拼串
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpeg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.jpeg"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE );

        /**
         *
         * 获取两个 直接内存映射文件，（只有ByteBuffer支持直接内存缓冲区）
         *      在缓冲区中放数据，相当于直接把数据放到物理内存，所以 不需要 通道传输。直接在缓冲区中放就可以
         *  ---------注意和 test1 对比-------------
         */
        MappedByteBuffer inMappedBuf = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());

        // 直接对缓冲区进行数据的读写
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();
    }


    /**
     * 1.利用通道完成复制（非直接缓冲区）
     */
    @Test
    public void test1(){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            fis = new FileInputStream("1.jpeg");
            fos = new FileOutputStream("2.jpeg");

            // 1.获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            // 2.获取指定大小的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // 3.把in通道中的数据写入缓冲区buffer
            while (inChannel.read(buffer) != -1){
                // 把刚写入buffer的数据，从buffer读出，所以切换读模式
                buffer.flip();
                // 4.把buffer中的数据写入out通道
                outChannel.write(buffer);
                // 清空缓冲区，准备下一次循环
                buffer.clear();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
