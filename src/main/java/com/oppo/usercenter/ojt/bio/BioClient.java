package com.oppo.usercenter.ojt.bio;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BioClient {

    private static final int BUFFER_LENGTH = 1024;

    private String host;

    private int port;

    public BioClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void write(String content) throws IOException, InterruptedException {
        if (StringUtils.isBlank(content)) {
            return;
        }

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(this.host, this.port));

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        for (int i = 0; i < 10; i++) {
            outputStream.write(content.getBytes());
            outputStream.flush();

            byte[] buffer = new byte[BUFFER_LENGTH];
            int readLength = inputStream.read(buffer);
            if (readLength > 0) {
                System.out.println("[response-" + Thread.currentThread().getName() + "] : " + new String(buffer, 0, readLength));
            }

            Thread.sleep(1000);
        }

        inputStream.close();
        outputStream.close();
        socket.close();
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        new BioClient(host, port).write("Hello OPPO！");
    }
}
