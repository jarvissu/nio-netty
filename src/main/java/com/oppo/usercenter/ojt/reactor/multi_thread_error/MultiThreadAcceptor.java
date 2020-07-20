package com.oppo.usercenter.ojt.reactor.multi_thread_error;

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

    private final Selector[] selectors;

    private ServerSocketChannel serverSocketChannel;

    private ExecutorService workExecutor = Executors.newFixedThreadPool(4);

    public MultiThreadAcceptor(Selector[] selectors, ServerSocketChannel serverSocketChannel) {
        this.selectors = selectors;
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
            if (i >= this.selectors.length) {
                index.set(0);
                i = 0;
            }

            Selector selector = selectors[i];
            channel.configureBlocking(false);

            selector.wakeup();
            System.out.println("selector is like wakeup.....");
            /*
            *此处由于选择的selector处于select()状态，而select和register会竞争同一个锁<link sun.nio.ch.SelectorImpl.register.publicKeys>
            * 所以导致register方法会阻塞
             */
            SelectionKey key = channel.register(selector,  0);
            System.out.println("channel is registered!");

            key.attach(new MultiThreadHandler(selector, channel));
            key.interestOps(SelectionKey.OP_READ);
        } catch (Exception e) {
            System.out.println("Acceptor error...");
            e.printStackTrace();
        }
    }
}
