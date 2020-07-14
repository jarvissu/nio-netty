package com.oppo.usercenter.ojt.nio;

import com.oppo.usercenter.ojt.bio.BioClient;
import com.sun.javafx.scene.text.HitInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {

    private static final int BUFFER_LENGTH = 1024;

    private String host;

    private int port;

    private Selector selector;

    public NioClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        selector = Selector.open();
    }

    public void write(String content) throws IOException {
        if (StringUtils.isBlank(content)) {
            return;
        }

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(this.host, this.port));

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    while (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }

                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    channel.write(ByteBuffer.wrap(content.getBytes()));
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    processReable(channel);
                }

                /*重点：必须删除当前key*/
                iterator.remove();
            }
        }
    }


    private void processReable(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
        int readLength = channel.read(buffer);
        if (readLength <= 0) {
            return;
        }

        /*flip to read*/
        buffer.flip();
        byte[] request = new byte[readLength];
        buffer.get(request);
        System.out.println("[Response]: " + new String(request));

        buffer.flip();
        channel.close();
    }

    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int port = 8080;
        new NioClient(host, port).write("Hello OPPO！");
    }
}
