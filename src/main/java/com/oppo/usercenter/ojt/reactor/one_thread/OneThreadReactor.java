package com.oppo.usercenter.ojt.reactor.one_thread;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class OneThreadReactor {

    private final Selector selector;

    public OneThreadReactor(Selector selector) {
        this.selector = selector;
    }

    public void start() throws IOException {
        while (!Thread.interrupted()){
            System.out.println("hahaha");
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                dispatch(key);
                iterator.remove();
            }
        }
    }

    private void dispatch(SelectionKey key){
        if (Objects.isNull(key)){
            return;
        }

        Object attachment = key.attachment();
        if (Objects.nonNull(attachment) && attachment instanceof Runnable){
            Runnable handler = (Runnable) attachment;
            handler.run();
        }
    }
}
