package com.allen.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class NioNonBlockUDPServer {

    public static void main(String[] args) {
        receive();
    }

    public static String getCurrentMiss() {
        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss:sss").format(new Date());
    }

    public static void receive() {
        DatagramChannel channel = null;
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(8383));
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            while (selector.select() > 0) {
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    String recvStr = "";
                    if (key.isReadable()) {
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        SocketAddress socketAddress = channel.receive(buf);
                        buf.flip();
                        recvStr = new String(buf.array(), 0, buf.limit());
                        System.out.println(getCurrentMiss() + " " + recvStr);
                        channel.send(buf, socketAddress);
                        buf.clear();
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
