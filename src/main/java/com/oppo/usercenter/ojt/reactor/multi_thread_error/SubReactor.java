package com.oppo.usercenter.ojt.reactor.multi_thread_error;

import lombok.Data;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class SubReactor implements Runnable {

    private final Selector selector;

    private final String reactorName;

    private static AtomicInteger index = new AtomicInteger(0);

    private final ExecutorService executorService;

    public SubReactor(int workThreads) throws IOException {
        this(Selector.open(), "reactor-" + index.getAndIncrement(), Executors.newFixedThreadPool(workThreads));
    }

    public SubReactor(ExecutorService executorService) throws IOException {
        this(Selector.open(), "reactor-" + index.getAndIncrement(), executorService);
    }

    public SubReactor(Selector selector, ExecutorService executorService) {
        this(selector, "reactor-" + index.getAndIncrement(), executorService);
    }

    public SubReactor(Selector selector, String reactorName, ExecutorService executorService) {
        this.selector = selector;
        this.reactorName = reactorName;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    dispatch(key);
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            System.out.println("SubReactor{" + this.reactorName + "} is disconnected...");
        }
    }

    private void dispatch(SelectionKey key) {
        if (Objects.isNull(key)) {
            return;
        }

        Object attachment = key.attachment();
        if (Objects.isNull(attachment)) {
            return;
        }

        if (attachment instanceof MultiThreadAcceptor) {
            System.out.println("Accept");
            Runnable handler = (Runnable) attachment;
            handler.run();
        } else
        if (attachment instanceof Runnable) {
            Runnable handler = (Runnable) attachment;
            executorService.submit(handler);
        }
    }
}
