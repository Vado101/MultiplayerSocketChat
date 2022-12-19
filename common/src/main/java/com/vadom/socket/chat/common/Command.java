package com.vadom.socket.chat.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command consists of: prefix <command> [--<keys>] [<args>]
 */
public interface Command<T> {

    T getCommand(String command);


    enum LineUp {
        COMMAND,
        KEYS,
        ARGS
    }

    // What will be written before the command and identify the entry
    // as a command
    String prefix = "cht ";
    String prefixKeys = "--";

    static boolean isCommand(String command) {
        if (command == null) {
            return false;
        }

        return command.indexOf(prefix) == 0;
    }

    static Map<LineUp, List<String>> getLineUpCommand(String command) {
        Map<LineUp, List<String>> lineUpCommand = new HashMap<>();

        final String[] lineUp = command.substring(prefix.length()).split(" ");

        List<String> commandName = new ArrayList<>();
        commandName.add(lineUp[0]);
        lineUpCommand.put(LineUp.COMMAND, commandName);

        for (int i = 1; i < lineUp.length; ++i) {
            if (lineUp[i].indexOf(prefixKeys) == 0) {
                setPartCommand(lineUpCommand, LineUp.KEYS,
                        lineUp[i].substring(prefixKeys.length()));
            } else {
                setPartCommand(lineUpCommand, LineUp.ARGS, lineUp[i]);
            }
        }

        return Map.copyOf(lineUpCommand);
    }

    private static void setPartCommand(Map<LineUp, List<String>> partsCommand,
                                LineUp lineUp, String part) {
        if (partsCommand.containsKey(lineUp)) {
            partsCommand.get(lineUp).add(part);
        } else {
            List<String> list = new ArrayList<>();
            list.add(part);
            partsCommand.put(lineUp, list);
        }
    }
}
