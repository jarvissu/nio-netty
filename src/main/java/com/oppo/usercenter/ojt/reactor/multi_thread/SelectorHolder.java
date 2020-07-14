package com.oppo.usercenter.ojt.reactor.multi_thread;

import lombok.Data;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Set;

/*
* 由于对于同一个一个Selector，select()和register()操作会竞争同一个锁“publicKeys”，
* 因此当Selector处于select()阻塞状态时，无法register
* 该类为解决多线程下，select和register的资源竞争问题
* */
@Data
public class SelectorHolder {

    private volatile boolean selected = true;

    private final Selector selector;

    public SelectorHolder(Selector selector) {
        this.selector = selector;
    }

    public Set<SelectionKey> select() throws IOException {
        if (!selected){
            return new HashSet<>();
        }

        this.selector.select();
        return this.selector.selectedKeys();
    }

    public synchronized void register(SelectableChannel channel, int ops, Object attachment) throws ClosedChannelException {
        selected = false;

        this.selector.wakeup();
        SelectionKey key = channel.register(this.selector, 0);
        key.attach(attachment);
        key.interestOps(ops);

        selected = true;
    }
}
