package com.allen.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * 非阻塞的io
 */
public class NioNonBlockUDPClient {
  public static final String ip = "172.25.48.36";
  public static final int port = 9898;

  public static void main(String[] args) {
    send();
//        new UDPClient().work();
  }

  //connect 方式 使用read,write
  public static class UDPClient {
    DatagramChannel channel;
    Selector selector;


    public void work() {

      try {
        // 开启一个通道
        channel = DatagramChannel.open();

        channel.configureBlocking(false);

        SocketAddress sa = new InetSocketAddress(ip, port);

        channel.connect(sa);
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
        long startTime = System.nanoTime();
        String str = "timeStamp:" + startTime;
        channel.write(Charset.defaultCharset().encode(str));
      } catch (Exception e) {
        e.printStackTrace();
      }

      ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
      while (true) {
        try {
          int n = selector.select();
          if (n > 0) {

            Iterator iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
              SelectionKey key = (SelectionKey) iterator.next();
              iterator.remove();
              if (key.isReadable()) {
                channel = (DatagramChannel) key.channel();
                channel.read(byteBuffer);
                byteBuffer.flip();
                String timeStamp = new String(byteBuffer.array(), 0, byteBuffer.limit()).split("\\:")[1];
                long current = System.nanoTime();
                float rtt = (current - Long.parseLong(timeStamp)) / (1000.0f * 1000);
                System.out.printf("current=%d,pre=%s,rtt=%.2f \n", current, timeStamp, rtt);
                byteBuffer.clear();
                //send to server
                long startTime = System.nanoTime();
                String str = "timeStamp:" + startTime;
                channel.write(Charset.defaultCharset().encode(
                  str));
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }
  }

  //send方式 使用send,receive
  public static void send() {

    DatagramChannel channel = null;
    try {
      channel = DatagramChannel.open();
      channel.configureBlocking(false);

      ByteBuffer buf = ByteBuffer.allocate(1024);
      long startTime = System.nanoTime();
      String str = "timeStamp:" + startTime;
      buf.put(str.getBytes());
      buf.flip();
      channel.send(buf, new InetSocketAddress(ip, port));
      buf.clear();

      Selector selector = Selector.open();
      channel.register(selector, SelectionKey.OP_READ);
      while (selector.select() > 0) {
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
          SelectionKey key = it.next();
          if (key.isReadable()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            SocketAddress socketAddress = channel.receive(buffer);
            buffer.flip();
            String timeStamp = new String(buffer.array(), 0, buffer.limit()).split("\\:")[1];
            long current = System.nanoTime();
            float rtt = (current - Long.parseLong(timeStamp)) / (1000.0f * 1000);
            System.out.printf("current=%d,pre=%s,rtt=%.2f \n", current, timeStamp, rtt);
            buffer.clear();

            //send to server
            ByteBuffer sendByte = ByteBuffer.allocate(100);
            startTime = System.nanoTime();
            str = "timeStamp:" + startTime;
            sendByte.put(str.getBytes());
            sendByte.flip();
            channel.send(sendByte, socketAddress);
            sendByte.clear();
          }
        }
        it.remove();

      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (channel != null) {
          channel.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
