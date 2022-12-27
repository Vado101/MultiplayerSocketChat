package com.vadom.socket.chat.client;

import com.vadom.socket.chat.common.Command;
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
                case EXIT -> client.exit();
                case LOGIN -> client.login();
                case LOGOUT -> client.logout();
                case HELP -> System.out.println(
                        Commands.commandHelp(Commands.LOGIN, Commands.LOGOUT,
                                Commands.EXIT, Commands.HELP));
            }
        } catch (IOException e) {
            System.out.println("Error occurred when processing a command " +
                    command.name() + " from command line. " + e.getMessage());
        }
    }

    @Override
    public void inputDataProcessing(String inputData) {
        try {
            if (client.isLogin()) {
                if (inputData.matches(".*\\S.*")) {
                    client.send(inputData);
                }
            } else {
                System.out.println("You need to login the chat. " +
                        "Entered command \"" +
                        Command.prefix + Commands.LOGIN + "\"");
            }
        } catch (IOException e) {
            System.out.println("Error occurred when sending a message " +
                    "to the server. " + e.getMessage());
        }
    }
}
