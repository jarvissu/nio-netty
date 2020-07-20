package com.oppo.usercenter.ojt.bio;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    private static final int BUFFER_LENGTH = 1024;

    private int port;

    public BioServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            byte[] buffer = new byte[BUFFER_LENGTH];
            while (true) {
                int readLength = inputStream.read(buffer);
                if (readLength > 0) {
                    System.out.println("[request]: " + new String(buffer, 0, readLength));
                    outputStream.write(buffer);
                } else {
                    break;
                }
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
            socket.close();
        }
    }


    public static void main(String[] args) throws Exception {
        new BioServer(8080).start();
    }
}
