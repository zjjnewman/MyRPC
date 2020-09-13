package nio;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * NIO 本地 IO
 * NIO https://www.bilibili.com/video/BV14W411u7ro?p=2
 *
 * 一、缓冲区（buffer）：在Java NIO中负责数据的存取。缓冲区就是数组用于存储不同数据类型的数组
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * 上述缓冲区管理方式几乎一致，通过 allocate()获取缓冲区
 *
 *
 * 二、缓冲区存取数据的两个核心方法
 *      put()：存入数据到缓冲区
 *      get()：读取缓冲区的数据
 *
 * 四、缓冲区中的四个核心属性
 *      capacity：容量，表示缓冲区中最大存储数据的容量。一旦生命不能改变
 *      limit：界限，表示缓冲区中可以操作数据的大小。（limit指针后的数据，不能读写）
 *      position：位置，表示缓冲区中正在操作数据的位置。
 *      mark：标记，表示记录当前position的位置，可以通过reset()恢复到mark的位置
 *
 *      0 <= mark <= position <= limit <= capacity
 *
 * 五、直接缓冲区与非直接缓冲区：
 *      非直接缓冲区：通过 allocate() 方法分配缓冲区，将缓冲区建立在JVM内存
 *      直接缓冲区：通过 allocateDirect() 方法分配缓冲区，将缓冲区建立在物理内存中。只有ByteBuffer支持
 *      1.适用场景，适合 数据长时间在内存中操作
 *      2.直接内存缓冲区：1.开辟空间较为耗费资源。
 *                      2.数据写进去之后就不归 程序管了，什么时候写回磁盘由操作系统控制。
 *                      3.垃圾回收机制 才可以使 jvm指向直接内存的指针断开
 *
 */
public class TestBuffer {

    @Test
    public void test3(){
        // 分配直接缓冲区----还有一种方式直接内存文件（通道）
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        System.out.println(buffer.isDirect());
    }

    @Test
    public void test2(){
        String str = "abcde";
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(str.getBytes());
        buffer.flip();
        byte[] dst = new byte[buffer.limit()];

        // 读两个buffer查看position
        buffer.get(dst, 0, 2);
        System.out.println(new String(dst, 0, 2));
        System.out.println(buffer.position());

        // mark()
        buffer.mark();

        // 再读两个buffer查看position
        buffer.get(dst, 2, 2);
        System.out.println(new String(dst, 2, 2));
        System.out.println(buffer.position());

        // 调用reset() 恢复到mark()，查看position
        buffer.reset();
        System.out.println(buffer.position());

        // 判断缓冲区中是否还有剩余的数据
        if(buffer.hasRemaining()){
            // 获取缓冲区中可以操作的数量
            System.out.println(buffer.remaining());
        }

    }

    @Test
    public void test1(){
        String str = "abcde";

        // 1. 分配指定大小啊缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        System.out.println("------------allocate()-------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 2.用put() 存入数据到缓冲区中
        buffer.put(str.getBytes());
        System.out.println("------------put()-------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 3.flip()，切换读取数据模式（实质就是 limit指针指向position，position指向buf开始）
        buffer.flip();
        System.out.println("------------flip()-------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 4.get() 读取缓冲区数据
        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst);
        System.out.println(new String(dst, 0, dst.length));
        System.out.println("------------get()-------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 5.rewind() 可重复读数据
        buffer.rewind();
        System.out.println("------------rewind()-------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 6.clear()清空缓冲区，执行后数据还在，只不过处于被遗忘状态（指针复位）
        buffer.clear();
        System.out.println("------------clear()-------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        System.out.println((char) buffer.get());

        // 7.

    }
}
