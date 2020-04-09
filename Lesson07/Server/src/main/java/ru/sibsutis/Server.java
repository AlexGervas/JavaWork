package ru.sibsutis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class MyThread implements Runnable {
    private Socket client;
    private InputStream in;
    private OutputStream out;

    public MyThread(Socket client) throws IOException {
        this.client = client;
        this.in = this.client.getInputStream();
        this.out = client.getOutputStream();
    }

    @Override
    public synchronized void run() {
        try {
            readInputHeaders();
            writeResponse("<html><body><h1>Hello</h1></body></html>");
        } catch (IOException e) {
            System.out.println("Error HTTP");
        } finally {
            try {
                this.client.close();
            } catch (IOException e) {
                System.out.println("Error close client");
            }
        }

    }

    private void readInputHeaders() throws IOException {
        BufferedReader buff = new BufferedReader(new InputStreamReader(this.in));
        while (true) {
            String str = buff.readLine();
            if (str == null || str.trim().length() == 0)
                break;
        }
    }

    private void writeResponse(String str) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Server: YarServer/2020-03-04\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length:" + str.length() + "\r\n" +
                "Connection: close\r\n\r\n";
        String result = response + str;
        out.write(result.getBytes());
        out.flush();
    }
}

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        while (true) {
            Socket client = server.accept();

            Thread socket = new Thread(new MyThread(client));
            socket.start();
        }
    }
}
