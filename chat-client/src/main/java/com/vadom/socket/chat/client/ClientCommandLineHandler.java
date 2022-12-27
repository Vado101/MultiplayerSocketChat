package com.vadom.socket.chat.client;

import com.vadom.socket.chat.common.CommandLineHandler;
import com.vadom.socket.chat.common.Commands;

import java.io.IOException;

public class ClientCommandLineHandler extends CommandLineHandler {

    private final Client client;

    public ClientCommandLineHandler(int id, Client client) {
        super(id);
        this.client = client;
    }

    @Override
    public void commandProcessing(String fullCommand) {
        Commands command = Commands.EXIT.getCommand(fullCommand);

        try {
            switch (command) {
                case EXIT -> {
                    inputDataProcessing(fullCommand);
                    client.exit();
                }
                case LOGIN -> client.login();
                case HELP -> System.out.println(
                        Commands.commandHelp(Commands.values()));
            }
        } catch (IOException e) {
            System.out.println("Error occurred when processing a command " +
                    command.name() + " from command line. " + e.getMessage());
        }
    }

    @Override
    public void inputDataProcessing(String inputData) {
        try {
            client.send(inputData);
        } catch (IOException e) {
            System.out.println("Error occurred when sending a message " +
                    "to the server. " + e.getMessage());
        }
    }
}
