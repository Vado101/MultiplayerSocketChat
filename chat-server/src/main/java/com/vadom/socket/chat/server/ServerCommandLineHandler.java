package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.CommandLineHandler;
import com.vadom.socket.chat.common.Commands;

import java.io.IOException;

public class ServerCommandLineHandler extends CommandLineHandler {

    private final Server server;

    public ServerCommandLineHandler(int id, Server server) {
        super(id);
        this.server = server;
    }

    @Override
    public void commandProcessing(String fullCommand) {
        Commands command = Commands.EXIT.getCommand(fullCommand);

        try {
            switch (command) {
                case EXIT -> server.stop();
                case HELP -> System.out.println(Commands.commandHelp(
                        Commands.EXIT, Commands.HELP));
            }
        } catch (IOException e) {
            System.out.println("Error occurred when processing a command " +
                    command.name() + " from command line. " + e.getMessage());
        }
    }
}
