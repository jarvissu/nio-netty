package com.oppo.usercenter.ojt.reactor.multi_thread;

import com.oppo.usercenter.ojt.reactor.one_thread.OneThreadHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadAcceptor implements Runnable {

    private AtomicInteger index = new AtomicInteger(0);

    private final SubReactor[] reactors;

    private ServerSocketChannel serverSocketChannel;

    private ExecutorService workExecutor = Executors.newFixedThreadPool(4);

    public MultiThreadAcceptor(SubReactor[] reactors, ServerSocketChannel serverSocketChannel) {
        this.reactors = reactors;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        try {
            SocketChannel channel = serverSocketChannel.accept();
            if (Objects.isNull(channel)) {
                return;
            }

            int i = index.incrementAndGet();
            if (i >= this.reactors.length) {
                index.set(0);
                i = 0;
            }

            SelectorHolder selectorHolder = reactors[i].getSelectorHolder();
            System.out.println("choose: " + selectorHolder);
            System.out.println("channel: " + channel);
            channel.configureBlocking(false);
            selectorHolder.register(channel, SelectionKey.OP_READ, new MultiThreadHandler(selectorHolder, channel));
        } catch (Exception e) {
            System.out.println("Acceptor error...");
            e.printStackTrace();
        }
    }
}
