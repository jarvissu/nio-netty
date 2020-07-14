package com.oppo.usercenter.ojt.reactor.one_thread;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class OneThreadAcceptor implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    public OneThreadAcceptor(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        SocketChannel channel = null;
        try {
            channel = serverSocketChannel.accept();
            channel.configureBlocking(false);

            // 预先不设置注册的selectionKey
            SelectionKey key = channel.register(selector, 0);
            key.attach(new OneThreadHandler(selector, channel));
            key.interestOps(SelectionKey.OP_READ);
        } catch (Exception e) {
            System.out.println("Acceptor error...");
        }
    }
}
