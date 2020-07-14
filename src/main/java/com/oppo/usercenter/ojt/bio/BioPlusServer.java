package com.oppo.usercenter.ojt.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 利用线程池执行
 * */
public class BioPlusServer {

    private static final int BUFFER_LENGTH = 1024;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private int port;

    public BioPlusServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();
            executorService.submit(new SocketHandler(socket));
        }
    }


    public static void main(String[] args) throws IOException {
        new BioPlusServer(8080).start();
    }

    private class SocketHandler implements Runnable {

        private Socket socket;

        public SocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream();) {

                // 模拟服务端任务执行耗时
                Thread.sleep(1000);

                byte[] buffer = new byte[BUFFER_LENGTH];
                while (true) {
                    int readLength = inputStream.read(buffer);
                    if (readLength > 0) {
                        System.out.println("[request]: " + new String(buffer, 0, readLength));
                        outputStream.write(buffer);
                    } else {
                        break;
                    }
                    outputStream.flush();
                }

            } catch (Exception e) {
                System.out.println("Error");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Socket close error!");
                }
            }
        }
    }
}
