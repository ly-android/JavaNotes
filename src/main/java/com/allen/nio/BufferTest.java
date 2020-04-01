package com.allen.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * buffer缓冲区测试
 * 1. 分配大小 allocate
 * 2. 读写数据
 * put()
 * get()
 * <p>
 * 3.四个核心属性
 * // Invariants: mark <= position <= limit <= capacity
 * private int mark = -1;  记录当前position,通过reset恢复
 * private int position = 0; 正在操作数据的位置
 * private int limit;     可以操作数据的大小，limit后面的数据不能读写
 * private int capacity;  最大储存的容量
 * <p>
 * 4. 缓冲区
 * 1）非直接缓冲区 allocate  分配在jvm的内存中
 * 2) 直接缓冲区  allocateDirect 分配在操作系统物理内存，提高效率
 */
public class BufferTest {

  public static void main(String[] args) {
    //分配大小
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    System.out.println(buffer.position());
    System.out.println(buffer.limit());
    System.out.println(buffer.capacity());

    System.out.println("---------put 操作----------");

    //put数据
    String str = "abcde";
    buffer.put(str.getBytes(), 0, str.length());

    System.out.println(buffer.position());
    System.out.println(buffer.limit());
    System.out.println(buffer.capacity());

    //切换读模式
    buffer.flip();
    System.out.println("---------flip 操作----------");
    System.out.println(buffer.position());
    System.out.println(buffer.limit());
    System.out.println(buffer.capacity());

    //读数据
    byte[] dst = new byte[buffer.limit()];
    buffer.get(dst);
    System.out.println("---------get 操作----------");
    System.out.println(new String(dst));
    System.out.println(buffer.position());
    System.out.println(buffer.limit());
    System.out.println(buffer.capacity());

    buffer.rewind(); //从头开始读
    System.out.println("---------rewind 操作----------");
    System.out.println(buffer.position());
    System.out.println(buffer.limit());
    System.out.println(buffer.capacity());

    buffer.clear();//清空,缓存区的数据还在,处于被遗忘状态

    System.out.println("---------clear 操作----------");
    System.out.println(buffer.position());
    System.out.println(buffer.limit());
    System.out.println(buffer.capacity());
  }
}
