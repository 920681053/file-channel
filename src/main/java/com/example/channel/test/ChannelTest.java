package com.example.channel.test;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author YuanJian
 * @data 18-4-27上午9:42
 */
public class ChannelTest {

    /**
     * 1.利用通道完成文件的复制（非直接缓冲区）
     */
    public static void test01(String fromPath, String toPath) {
        long start = System.currentTimeMillis();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            fis = new FileInputStream(fromPath);
            fos = new FileOutputStream(toPath);
            //1.获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();
            //2.分配指定大小的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //3.将通道中的数据缓冲区中
            while (inChannel.read(buffer) != -1) {
                //切换成都数据模式
                buffer.flip();
                //4.将缓冲区中的数据写入通道中
                outChannel.write(buffer);
                buffer.clear();//清空缓冲区
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(outChannel);
            close(inChannel);
            close(fis);
            close(fos);
            long end = System.currentTimeMillis();
            System.out.println("耗费的时间为：" + (end - start));
        }
    }

    /**
     * 2.利用（直接缓冲区）通道完成文件的复制(内存映射文件的方式)
     *
     * @throws IOException
     */
    public static void test02(String strFromPath, String strToPath) throws IOException {
        long start = System.currentTimeMillis();
        Path fromPath = Paths.get(strFromPath);
        Path toPath = Paths.get(strToPath);
        FileChannel inChannel = FileChannel.open(fromPath, StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(toPath, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
        //内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
        //直接对缓冲区进行数据读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);
        inChannel.close();
        outChannel.close();
        long end = System.currentTimeMillis();
        System.out.println("耗费的时间为：" + (end - start));
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}