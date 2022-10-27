package com.vadom.socket.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.vadom.socket.chat.common.Constant.DEFAULT_PORT;
import static com.vadom.socket.chat.common.Constant.EXIT_CODE;

public class Main {
    private static final ExecutorService SERVICE =
            Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            SERVICE.execute(new Server(serverSocket));
            shutdownServer();
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }

    private static void shutdownServer() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                if (EXIT_CODE.equals(scanner.nextLine())) {
                    SERVICE.shutdownNow();
                    break;
                }
            }
        }
    }
}