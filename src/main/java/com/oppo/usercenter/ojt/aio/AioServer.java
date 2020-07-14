package com.oppo.usercenter.ojt.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AioServer {

    private int port;

    public AioServer(int port) {
        this.port = port;
    }

    public void start() throws IOException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        System.out.println("Server is started...");

        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                System.out.println("Connect success....");
                try {
                    buffer.clear();

                    // 读取数据
                    Future<Integer> readFuture = result.read(buffer);
                    Integer readLength = readFuture.get();
                    byte[] request = new byte[readLength];
                    buffer.flip();
                    buffer.get(request);
                    System.out.println("[request]: " + new String(request));

                    // 回显
                    buffer.flip();
                    result.write(buffer);

                    buffer.flip();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("IO failed...");
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 8080;
        new AioServer(port).start();
    }
}
