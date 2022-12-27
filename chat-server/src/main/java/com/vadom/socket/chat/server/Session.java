package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.Command;
import com.vadom.socket.chat.common.Commands;
import com.vadom.socket.chat.common.Handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class Session extends Handler {
    private final Socket socket;
    private final InteractionServer server;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private boolean login;
    private String username;

    public Session(int id, Socket socket, InteractionServer server)
            throws IOException {
        super(id);
        this.socket = socket;
        this.server = server;

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public boolean isLogin() {
        return login;
    }

    public String getUsername() {
        return username;
    }

    public void send(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Error occurred when sending a message to the" +
                    " client in session with ID = " + getId() + ". " +
                    e.getMessage());

            if (!(e instanceof UTFDataFormatException)) {
                close();
            }
        }
    }

    @Override
    public void close() {
        // Session can only be closed
        if (isRun) {
            super.close();

            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error occurred when closing the session " +
                        "with ID = " + getId() + ". " + e.getMessage());
            }
        }
    }

    @Override
    public void handle() {
        try {
            if (isRun) {
                if (inputStream.available() > 0) {
                    String inputData = inputStream.readUTF(); // blocking

                    if (Command.isCommand(inputData)) {
                        commandProcessing(inputData);
                    } else {
                        String message = getUsername() + ": " + inputData;
                        server.sendAll(message, this);
                    }
                }
            } else {
                server.removeSession(this);
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.out.println("Error occurred when receiving a message from" +
                        " client in session with ID = " + getId() + ". " +
                        e.getMessage());
            }

            if (!(e instanceof UTFDataFormatException)) {
                close();
                server.removeSession(this);
            }
        }
    }

    private void commandProcessing(String fullCommand) {
        Commands command = Commands.EXIT.getCommand(fullCommand);
        switch (command) {
            case EXIT -> server.removeSession(this);
            case LOGIN -> login(fullCommand);
            case LOGOUT -> logout();
        }
    }

    /**
     * Implemented login-protocol:
     * 1. request: {@code Command.prefix} login
     * 2. response: {@code Command.prefix} login --name Entered your name
     * 3. request: {@code Command.prefix} login --name username
     * 4. response: {@code Command.prefix} login --confirm OK
     */
    private void login(String fullCommand) {
        Map<Command.Component, List<String>> components =
                Command.getCommandComponents(fullCommand);

        if (!login) {
            if (Commands.containsKey(fullCommand, Commands.KEY.NAME)) {
                // if the user has not entered a name
                if (components.get(Command.Component.ARGS).isEmpty()) {
                    send(Commands.createCommand(
                            Commands.LOGIN,
                            new Commands.KEY[]{Commands.KEY.NAME},
                            "Entered your name, please"));
                } else {
                    username = components.get(Command.Component.ARGS).get(0);

                    // TODO: check username is correct

                    send(Commands.createCommand(
                            Commands.LOGIN,
                            new Commands.KEY[]{Commands.KEY.CONFIRM},
                            Commands.ErrorCode.OK.name() +
                            " Welcome to the chat, you are known as " +
                                    username));
                    server.sendAll(username + " has entered the chat", this);
                    System.out.println("ID = " + getId() +
                            " login name: " + username);
                    login = true;
                }
            } else {
                send(Commands.createCommand(
                        Commands.LOGIN,
                        new Commands.KEY[]{Commands.KEY.NAME},
                        "Entered your name, please"));
            }
        } else {
            send(Commands.createCommand(
                    Commands.LOGIN,
                    new Commands.KEY[]{Commands.KEY.NAME},
                    Commands.ErrorCode.FAILED.name() + ":",
                    " You are already logged in as " + getUsername()));
        }
    }

    /**
     * Implemented login-protocol:
     * 1. request: {@code Command.prefix} logout
     * 2. response: {@code Command.prefix} logout --confirm OK You has left the chat
     */
    public void logout() {
        if (login) {
            send(Commands.createCommand(
                    Commands.LOGOUT,
                    new Commands.KEY[]{Commands.KEY.CONFIRM},
                    Commands.ErrorCode.OK.name() +
                            " You has left the chat"));
            login = false;
            server.sendAll(username + " has left the chat", this);
            System.out.println("ID = " + getId() +
                    " logout name: " + username);
        } else {
            send(Commands.createCommand(
                    Commands.LOGOUT,
                    new Commands.KEY[]{Commands.KEY.CONFIRM},
                    Commands.ErrorCode.FAILED.name() + ":" +
                            " You are not logged into the chat"));
        }
    }
}
