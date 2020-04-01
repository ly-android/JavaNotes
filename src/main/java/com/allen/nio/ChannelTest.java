package com.allen.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * FileChannel
 * SocketChannel
 * ServerSocketChannel
 * DatagramChannel
 * <p>
 * 1. 获取通道
 * 1)本地io getChannel()方法
 * FileInputStream/FileOutputStream
 * RandomAccessFile
 * <p>
 * 2)网络io
 * Socekt
 * ServerSocket
 * DatagramSocket
 * <p>
 * 3)nio的open()方法
 * <p>
 * 4)Files工具类 newByteChannel
 */
public class ChannelTest {
  public static void main(String[] args) throws Exception {
    // test1();
    // test2();
    // test3();
    charsetTest();
  }

  //利用通道完成文件复制
  private static void test1() throws Exception {
    //项目根路径下
    FileInputStream fis = new FileInputStream("1.jpg");
    FileOutputStream fos = new FileOutputStream("2.jpg");
    //获取通道
    FileChannel inChannel = fis.getChannel();
    FileChannel outChannel = fos.getChannel();
    //分配缓冲区
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    //将通道写入buffer
    while (inChannel.read(buffer) != -1) {
      buffer.flip();
      outChannel.write(buffer);
      buffer.clear();
    }
    outChannel.close();
    inChannel.close();
  }

  //内存映射文件
  private static void test2() throws Exception {
    FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
    FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);
    //内存映射文件
    MappedByteBuffer inMapedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
    MappedByteBuffer outMapedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

    //直接写入
    outMapedBuf.put(inMapedBuf);
    //或者通过byte[]
    // byte[] data = new byte[inMapedBuf.limit()];
    // inMapedBuf.get(data);
    // outMapedBuf.put(data);

    inChannel.close();
    outChannel.close();
  }

  //通道数据传输
  //transforTo,transforFrom
  private static void test3() throws Exception {
    FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
    FileChannel outChannel = FileChannel.open(Paths.get("4.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);

    // inChannel.transferTo(0, inChannel.size(), outChannel);
    outChannel.transferFrom(outChannel, 0, inChannel.size());
    inChannel.close();
    outChannel.close();
  }

  //字符集
  private static void charsetTest() throws Exception {

    Charset charset = Charset.forName("GBK");
    //编码器
    CharsetEncoder encoder = charset.newEncoder();
    //解码器
    CharsetDecoder decoder = charset.newDecoder();

    CharBuffer cBuf = CharBuffer.allocate(1024);
    cBuf.put("allen学习java");
    cBuf.flip();
    //编码
    ByteBuffer bBuf = encoder.encode(cBuf);
    for (int i = 0; i < 10; i++) {
      System.out.println(bBuf.get());
    }

    //解码
    bBuf.flip();
    CharBuffer buffer = decoder.decode(bBuf);
    System.out.println(buffer.toString());

    //使用utf-8解码
    bBuf.flip();
    System.out.println(Charset.forName("utf-8").decode(bBuf).toString());
  }
}
