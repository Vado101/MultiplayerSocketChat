package com.vadom.socket.chat.client;

import com.vadom.socket.chat.common.Constant;
import com.vadom.socket.chat.common.HandlersSelector;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private final HandlersSelector handlersSelector;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Scanner scanner;
    private String name;

    private Client(Socket socket, HandlersSelector handlersSelector)
            throws IOException {
        this.socket = socket;
        this.handlersSelector = handlersSelector;

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

        scanner = new Scanner(System.in);
    }

    public String getName() {
        return name;
    }

    public static Client connect(HandlersSelector handlersSelector) {
        return connect(Constant.DEFAULT_IP, Constant.DEFAULT_PORT,
                handlersSelector);
    }

    public static Client connect(int port, HandlersSelector handlersSelector) {
        return connect(Constant.DEFAULT_IP, port, handlersSelector);
    }

    public static Client connect(String ip, int port,
                                 HandlersSelector handlersSelector) {
        if (ip == null) {
            throw new IllegalArgumentException("ip == null");
        }

        try {
            Socket socket = new Socket(ip, port);

            return new Client(socket, handlersSelector);
        } catch (IOException e) {
            System.out.println("Failed to connect to the server. " +
                    e.getMessage());
        }

        return null;
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    public void exit() throws IOException {
        disconnect();
        handlersSelector.stop();
    }

    public void send(String request) throws IOException {
        outputStream.writeUTF(request);
    }

    public String response() throws IOException {
        if (inputStream.available() > 0) {
            return inputStream.readUTF();
        }

        return null;
    }
}
