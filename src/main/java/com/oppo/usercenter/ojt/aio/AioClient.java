package com.oppo.usercenter.ojt.aio;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class AioClient {

    private String host;

    private int port;

    public AioClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void write(String content) throws IOException, InterruptedException {
        if (StringUtils.isBlank(content)){
            return;
        }

        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress(host, port), null, new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                try {
                    client.write(ByteBuffer.wrap(content.getBytes())).get();
                    System.out.println("send success...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.println("connect failed!");
            }
        });

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        client.read(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("IO success : " + result);
                System.out.println("[response] : " + new String(buffer.array()));
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("read failed....");
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String host = "127.0.0.1";
        int port = 8080;
        new AioClient(host,port).write("Hello OPPO!");
    }
}
