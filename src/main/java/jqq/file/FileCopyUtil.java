package jqq.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/** 文件复制工具
 * <br>==========================
 * <br> 创建时间：2020/9/9 下午3:15
 * <br>==========================
 */
public class FileCopyUtil {

    /**
     * 零拷贝复制文件
     * @param from
     * @param to
     * @throws IOException
     */
    public void zeroCopyFile(String from, String to) throws IOException {
        FileChannel fromChannel = new RandomAccessFile(from, "rw").getChannel();
        FileChannel toChannel = new RandomAccessFile(to, "rw").getChannel();

        long position = 0;
        long count = fromChannel.size();

        fromChannel.transferTo(position, count, toChannel);

        fromChannel.close();
        toChannel.close();
    }

    /**
     * 普通复制文件
     * @param from
     * @param to
     * @throws IOException
     */
    public void copyFile(String from, String to) throws IOException {
        FileInputStream input = new FileInputStream(from);
        FileOutputStream output = new FileOutputStream(to);

        byte[] b = new byte[1024];
        int n = 0;
        while ((n = input.read(b)) != -1) {
            output.write(b, 0, n);
        }

        input.close();
        output.close();
    }
}
