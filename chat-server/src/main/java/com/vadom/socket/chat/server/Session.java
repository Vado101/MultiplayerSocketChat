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

    public Session(int id, Socket socket, InteractionServer server)
            throws IOException {
        super(id);
        this.socket = socket;
        this.server = server;

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void send(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Error occurred when sending a message to the " +
                    "client in session with ID = " + getId() + ". " +
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
                        String message = getId() + ": " + inputData;
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
        Map<Command.LineUp, List<String>> lineUpMap =
                Command.getLineUpCommand(fullCommand);
        Commands command = Commands.EXIT.getCommand(fullCommand);

//        try {
            switch (command) {
                case EXIT -> server.removeSession(this);
            }
//        } catch (IOException e) {
//            System.out.println("Error occurred when processing a command " +
//                    command.name() + " from command line. " + e.getMessage());
//        }
    }
}
