package com.oppo.usercenter.ojt.reactor.multi_thread_error;

import com.oppo.usercenter.ojt.SystemConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadReactor {

    private final Selector[] selectors;

    private ExecutorService bossExecutor;

    private ExecutorService workExecutor;

    private boolean bind = false;

    public MultiThreadReactor() throws IOException {
        this(SystemConstant.DEFAULT_BOSS_THREAD, SystemConstant.DEFAULT_WORK_THREAD);
    }

    public MultiThreadReactor(int bossThread, int workThread) throws IOException {
        bossExecutor = Executors.newFixedThreadPool(bossThread);
        workExecutor = Executors.newFixedThreadPool(workThread);
        selectors = new Selector[bossThread];
        for (int i = 0; i < bossThread; i++) {
            selectors[i] = Selector.open();
        }
    }

    public MultiThreadReactor bind(int port) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));

        // selector[0] 负责注册绑定channel, 监听OP_ACCEPT事件
        SelectionKey key = serverSocketChannel.register(selectors[0], 0);
        key.attach(new MultiThreadAcceptor(selectors, serverSocketChannel));
        key.interestOps(SelectionKey.OP_ACCEPT);

        this.bind = true;
        return this;
    }

    public void start() throws IOException {
        if (!bind) {
            System.out.println("server not bind success...");
            return;
        }

        for (Selector selector : selectors) {
            bossExecutor.submit(new SubReactor(selector, workExecutor));
        }
        System.out.println("server startup...");
    }
}
