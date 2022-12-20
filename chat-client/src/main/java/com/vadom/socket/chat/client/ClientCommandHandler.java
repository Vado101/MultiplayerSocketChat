package com.vadom.socket.chat.client;

import com.vadom.socket.chat.common.CommandHandler;
import com.vadom.socket.chat.common.Commands;

import java.io.IOException;

public class ClientCommandHandler extends CommandHandler {

    private final Client client;

    public ClientCommandHandler(int id, Client client) {
        super(id);
        this.client = client;
    }

    @Override
    public void processing(Commands command) {
        try {
            switch (command) {
                case EXIT -> client.exit();
            }
        } catch (IOException e) {
            System.out.println("Error occurred when processing a command " +
                    command.name() + " from command line. " + e.getMessage());
        }
    }
}
