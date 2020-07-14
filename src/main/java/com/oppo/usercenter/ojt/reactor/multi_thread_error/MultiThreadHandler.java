package com.oppo.usercenter.ojt.reactor.multi_thread_error;

import com.oppo.usercenter.ojt.SystemConstant;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class MultiThreadHandler implements Runnable {


    private final Selector selector;

    private final SocketChannel socketChannel;

    public MultiThreadHandler(Selector selector, SocketChannel socketChannel) throws ClosedChannelException {
        this.selector = selector;
        this.socketChannel = socketChannel;


        // 预先不设置注册的selectionKey

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
