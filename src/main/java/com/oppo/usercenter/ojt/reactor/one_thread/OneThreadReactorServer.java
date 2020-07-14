package com.oppo.usercenter.ojt.reactor.one_thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class OneThreadReactorServer {

    private static final int BUFFER_LENGTH = 1024;

    private int port;

    private Selector selector;

    private OneThreadReactor reactor;

    private OneThreadReactorServer(int port) throws IOException {
        this.port = port;
        selector = Selector.open();
        reactor = new OneThreadReactor(selector);
    }

    public void init() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        SelectionKey key = serverSocketChannel.register(selector, 0);
        key.attach(new OneThreadAcceptor(selector, serverSocketChannel));
        key.interestOps(SelectionKey.OP_ACCEPT);

        reactor.start();
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new OneThreadReactorServer(port).init();
    }
}
