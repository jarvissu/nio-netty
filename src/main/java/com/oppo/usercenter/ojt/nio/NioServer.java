package com.oppo.usercenter.ojt.nio;

import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    private static final int BUFFER_LENGTH = 1024;

    private int port;

    private Selector selector;

    private NioServer(int port) throws IOException {
        this.port = port;
        selector = Selector.open();
    }

    public void start() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
//            System.out.println("select:");
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    processAccetable(channel);
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    processReable(channel);
                }

                /*重点：必须删除当前key*/
                iterator.remove();
            }
        }
    }

    private void processAccetable(ServerSocketChannel channel) throws IOException {
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void processReable(SocketChannel channel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
        while (true) {
            int readLength = channel.read(buffer);
//            System.out.println("readLength=" + readLength);
            if (readLength <= 0){
                break;
            }

            /*flip to read*/
            buffer.flip();
            byte[] request = new byte[readLength];
            buffer.get(request);
            System.out.println("[request]: " + new String(request));


            /*rewind to read again*/
            buffer.rewind();
            channel.write(buffer);

            buffer.flip();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new NioServer(port).start();
    }
}
