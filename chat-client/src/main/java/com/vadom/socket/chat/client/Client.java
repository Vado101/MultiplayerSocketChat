package com.vadom.socket.chat.client;

import com.vadom.socket.chat.common.*;

import java.io.*;
import java.net.ProtocolException;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Client extends Handler {
    private final Socket socket;
    private final HandlersSelector handlersSelector;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Scanner scanner;
    private String name;
    private boolean isLogin;

    private Client(Socket socket, HandlersSelector handlersSelector)
            throws IOException {
        super(handlersSelector.getFreeID());
        this.socket = socket;
        this.handlersSelector = handlersSelector;

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

        scanner = new Scanner(System.in);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLogin() {
        return isLogin;
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
        send(Commands.createCommand(Commands.EXIT, null));
        disconnect();
        handlersSelector.stop();
    }

    public void send(String request) throws IOException {
        if (request != null) {
            outputStream.writeUTF(request);
        }
    }

    public String getResponse() throws IOException {
        if (inputStream.available() > 0) {
            return inputStream.readUTF();
        }

        return null;
    }

    /**
     * Implemented login-protocol:
     * 1. request: {@code Command.prefix} login
     * 2. response: {@code Command.prefix} login --name Entered your name
     * 3. request: {@code Command.prefix} login --name username
     * 4. response: {@code Command.prefix} login --confirm OK
     */
    public void login() throws IOException {
        String loginCommand = Commands
                .createCommand(Commands.LOGIN, null);

        Map<Command.Component, List<String>> components =
                getResponse(loginCommand, Commands.LOGIN, Commands.KEY.NAME);
        List<String> args = components.get(Command.Component.ARGS);

        if (args != null) {
            System.out.println(Command.getMessageFromArgs(args));
        } else {
            System.out.println("Entered you name:");
        }

        // Getting username from command line
        String name = scanner.next();
        loginCommand = Commands.createCommand(Commands.LOGIN,
                new Commands.KEY[]{Commands.KEY.NAME}, name);
        components = getResponse(loginCommand, Commands.LOGIN,
                Commands.KEY.CONFIRM);
        args = components.get(Command.Component.ARGS);

        if (args != null && args.contains(Commands.ErrorCode.OK.name())) {
            setName(name);
            System.out.println(
                    Command.getMessageFromArgs(args.subList(1, args.size())));
            isLogin = true;
        } else {
            System.out.println("Login is failed");
        }
    }

    /**
     * Implemented login-protocol:
     * 1. request: {@code Command.prefix} logout
     * 2. response: {@code Command.prefix} logout --confirm OK You has left the chat
     */
    public void logout() throws IOException {
        String logoutCommand = Commands
                .createCommand(Commands.LOGOUT, null);

        Map<Command.Component, List<String>> components =
                getResponse(logoutCommand, Commands.LOGOUT,
                        Commands.KEY.CONFIRM);
        List<String> args = components.get(Command.Component.ARGS);

        if (args != null && args.contains(Commands.ErrorCode.OK.name())) {
            System.out.println(
                    Command.getMessageFromArgs(args.subList(1, args.size())));
            isLogin = false;
        } else {
            System.out.println("Logout is failed");
        }
    }

    @Override
    public void handle() {
        try {
            String response = getResponse();
            if (response != null) {
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Error occurred when receiving message " +
                    "from chat. " + e.getMessage());
        }
    }


    private Map<Command.Component, List<String>> getResponse(
            String fullCommand,
            Commands responseCommand,
            Commands.KEY... responseKeys) throws IOException {
        send(fullCommand);
        String response = waitingResponse(5000);

        return checkResponse(response, responseCommand, responseKeys);
    }

    /**
     * Waiting for the server response with delay.
     * Waits for a server response for the specified number of milliseconds.
     *
     * @param delay
     *            Delay in milliseconds.
     *
     * @return Server response as a string or {@code null} if the timeout has
     *         expired.
     * @throws IOException the stream has been closed and the contained input
     *         stream does not support reading after close, or another
     *         I/O error occurs.
     */
    private String waitingResponse(int delay) throws IOException {
        String response;
        long interval;
        Instant finish;
        Instant start = Instant.now();

        do {
            response = getResponse();
            finish = Instant.now();
            interval = Duration.between(start, finish).toMillis();
        } while (response == null && interval < delay);

        return response;
    }

    private Map<Command.Component, List<String>> checkResponse(
            String response,
            Commands responseCommand,
            Commands.KEY... responseKeys) throws ProtocolException {
        if (response == null) {
            throw new ProtocolException("Server not responding");
        }

        if (!Command.isCommand(response)) {
            throw new ProtocolException("Received an unknown command from " +
                    "the server: " + response);
        }

        if (Commands.LOGIN.getCommand(response) != responseCommand) {
            throw new ProtocolException("Received an invalid command from " +
                    "the server: " + response);
        }

        if (responseKeys != null &&
                !Commands.containsKey(response, responseKeys)) {
            throw new ProtocolException("Received command from " +
                    "server does not contain expected keys: " + response);
        }

        Map<Command.Component, List<String>> components =
                Command.getCommandComponents(response);
        String error = Commands.checkingArgumentsForErrorMessage(
                components.get(Command.Component.ARGS));

        if (error != null) {
            throw new ProtocolException(error);
        }

        return components;
    }
}
