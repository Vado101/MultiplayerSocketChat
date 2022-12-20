package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.Command;
import com.vadom.socket.chat.common.Commands;
import com.vadom.socket.chat.common.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerCommandHandler extends Handler {

    private final Server server;
    private final BufferedReader reader;

    public ServerCommandHandler(int id, Server server) {
        super(id);
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void handle() {
        try {
            if (reader.ready()) {
                String inputData = reader.readLine();

                if (Command.isCommand(inputData)) {
                    Commands command = Commands.CONNECT.getCommand(inputData);

                    switch (command) {
                        case EXIT -> server.stop();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error occurred when receiving a command from" +
                    " command line. " + e.getMessage());
        } catch (IllegalArgumentException e) {
            int index = e.getMessage().lastIndexOf(".");
            System.out.println("Error: non-existent command entered \"" +
                    e.getMessage().substring(index + 1).toLowerCase() + "\"");
        }
    }
}
