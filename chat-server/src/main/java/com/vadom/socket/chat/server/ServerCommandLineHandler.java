package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.Command;
import com.vadom.socket.chat.common.CommandLineHandler;
import com.vadom.socket.chat.common.Commands;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ServerCommandLineHandler extends CommandLineHandler {

    private final Server server;

    public ServerCommandLineHandler(int id, Server server) {
        super(id);
        this.server = server;
    }

    @Override
    public void commandProcessing(String fullCommand) {
        Map<Command.LineUp, List<String>> lineUpMap =
                Command.getLineUpCommand(fullCommand);
        Commands command = Commands.EXIT.getCommand(fullCommand);

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
