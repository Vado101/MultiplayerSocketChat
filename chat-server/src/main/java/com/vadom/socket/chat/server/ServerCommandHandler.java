package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.CommandHandler;
import com.vadom.socket.chat.common.Commands;

import java.io.IOException;

public class ServerCommandHandler extends CommandHandler {

    private final Server server;

    public ServerCommandHandler(int id, Server server) {
        super(id);
        this.server = server;
    }

    @Override
    public void processing(Commands command) {
        try {
            switch (command) {
                case EXIT -> server.stop();
            }
        } catch (IOException e) {
            System.out.println("Error occurred when processing a command " +
                    command.name() + " from command line. " + e.getMessage());
        }
    }
}
