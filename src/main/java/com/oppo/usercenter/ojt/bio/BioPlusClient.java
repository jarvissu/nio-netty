package com.oppo.usercenter.ojt.bio;

import lombok.SneakyThrows;

import java.io.IOException;

public class BioPlusClient {

    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int port = 8080;
        BioClient client = new BioClient(host, port);
        for (int i = 0; i < 200; i++) {
//            client.write("Hello OPPO！");
            new ConnectThread(client, "Thread-" + i).start();
        }
    }

    private static class ConnectThread extends Thread {
        private BioClient client;

        public ConnectThread(BioClient client, String threadName) {
            super(threadName);
            this.client = client;
        }

        @Override
        public void run() {
            try {
                client.write("Hello OPPO!");
            } catch (Exception e) {
                System.out.println("Connect Error!");
            }
        }
    }
}
