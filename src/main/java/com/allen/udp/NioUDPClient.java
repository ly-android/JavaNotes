package com.allen.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * 普通io
 */
public class NioUDPClient {

    public static void main(String[] args) {
        send();
    }

    public static void send() {
        DatagramChannel channel = null;
        try {
            channel = DatagramChannel.open();
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int i = 0;
            while (true) {
                if (i == 50) {
                    break;
                }
                long startTime = System.nanoTime();
                String str = "timeStamp:" + startTime;
                buf.put(str.getBytes());
                buf.flip();
                channel.send(buf, new InetSocketAddress("localhost", 9898));
                buf.clear();
                i++;
            }
            //接收消息线程
            DatagramChannel finalChannel = channel;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    byte b[];
                    while (true) {
                        buffer.clear();
                        SocketAddress socketAddress = null;
                        try {
                            socketAddress = finalChannel.receive(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (socketAddress != null) {
                            int position = buffer.position();
                            b = new byte[position];
                            buffer.flip();
                            for (int i = 0; i < position; ++i) {
                                b[i] = buffer.get();
                            }
                            try {
                                String data = new String(b);
                                String timeStamp = data.split("\\:")[1];
                                long current = System.nanoTime();
                                float rtt = (current - Long.parseLong(timeStamp)) / (1000f * 1000f);
                                System.out.println("rtt:" + rtt);
//                                System.out.println("rtt:" + rtt * 1.0f / (1000 * 1000));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                if (channel != null) {
//                    channel.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

}
