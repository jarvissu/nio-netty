package com.oppo.usercenter.ojt.reactor.one_thread;

import com.oppo.usercenter.ojt.SystemConstant;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class OneThreadHandler implements Runnable {


    private final Selector selector;

    private final SocketChannel socketChannel;

    public OneThreadHandler(Selector selector, SocketChannel socketChannel) {
        this.selector = selector;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(SystemConstant.BUFFER_SIZE);
        try {
            while (true) {
                int readLength = socketChannel.read(buffer);
                if (readLength <= 0){
                    break;
                }

                // flip to read-mode
                buffer.flip();
                byte[] request = new byte[readLength];
                buffer.get(request);
                System.out.println("[request]: " + new String(request));

                // rewind to read again
                buffer.rewind();
                socketChannel.write(buffer);

                // flip to write-mode
                buffer.flip();
            }
        } catch (IOException e) {
            System.out.println("Read error!");
        }
    }
}
