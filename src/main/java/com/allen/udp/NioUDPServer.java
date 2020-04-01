package com.allen.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class NioUDPServer {
    public static void main(String[] args) {
        receive();
    }

    public static void receive() {
        DatagramChannel channel = null;
        try {
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(9898));
            // 分配Buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            byte b[];
            while (true) {
                // 清空Buffer
                buffer.clear();
                // 接受客户端发送数据
                SocketAddress socketAddress = channel.receive(buffer);
                if (socketAddress != null) {
                    int position = buffer.position();
                    b = new byte[position];
                    buffer.flip();
                    for (int i = 0; i < position; ++i) {
                        b[i] = buffer.get();
                    }
                    String str = new String(b);
                    System.out.println("receive remote " + socketAddress.toString() + ":" + str);
                    //接收到消息后给发送方回应
                    buffer.flip();
                    channel.send(buffer, socketAddress);
                    System.out.println("send back to client!");
                }
            }
        } catch (Exception e) {
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
