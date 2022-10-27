package com.vadom.socket.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Server implements Runnable {
    private static final int TIME_OUT = 1000;
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        if (serverSocket == null) {
            throw new IllegalArgumentException("serverSocket == null");
        }

        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        System.out.println("----- Start server at address: " +
                serverSocket.getInetAddress().getHostName() + ":" +
                serverSocket.getLocalPort() + " -----\n" +
                "Waiting client at port " +
                serverSocket.getLocalPort() + "...");

        try {
            serverSocket.setSoTimeout(TIME_OUT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Connected client with address " +
                            socket.getRemoteSocketAddress().toString());
                } catch (SocketTimeoutException e) {
                    // TODO: do handle
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SocketException e) {
            System.out.println("no socket access" + e.getMessage());
        }

        System.out.println("----- Disconnect server -----");
    }
}
