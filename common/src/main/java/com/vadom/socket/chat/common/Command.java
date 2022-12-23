package com.vadom.socket.chat.common;

import java.util.*;

/**
 * Command consists of: prefix <command> [--<keys>] [<args>].
 * Args can be used to pass error messages.
 */
public interface Command<T> {

    T getCommand(String command);


    enum Component {
        COMMAND,
        KEYS,
        ARGS
    }

    // This will be written before the command and identify the entry or
    // message as a command
    String prefix = "cht ";
    String prefixKeys = "--";

    static boolean isCommand(String command) {
        if (command == null) {
            return false;
        }

        return command.indexOf(prefix) == 0;
    }


    /**
     * NOTE: The first word after the prefix will be considered a command.
     */
    static Map<Component, List<String>> getCommandComponents(String command) {
        Objects.requireNonNull(command, "must not be command == null");

        if (!isCommand(command)) {
            throw new IllegalArgumentException("Input data isn't command. " +
                    "Command consists of: prefix <command> [--<keys>] [<args>]");
        }

        Map<Component, List<String>> components = new HashMap<>();
        final String[] parts = command.substring(prefix.length()).split(" ");
        List<String> commandName = new ArrayList<>();
        commandName.add(parts[0]);
        components.put(Component.COMMAND, commandName);

        for (int i = 1; i < parts.length; ++i) {
            if (parts[i].indexOf(prefixKeys) == 0) {
                setPartCommand(components, Component.KEYS,
                        parts[i].substring(prefixKeys.length()));
            } else {
                setPartCommand(components, Component.ARGS, parts[i]);
            }
        }

        return Map.copyOf(components);
    }

    static String getMessageFromArgs(List<String> args) {
        if (args == null || args.isEmpty()) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

    private static void setPartCommand(Map<Component, List<String>> components,
                                       Component component, String part) {
        if (components.containsKey(component)) {
            components.get(component).add(part);
        } else {
            List<String> list = new ArrayList<>();
            list.add(part);
            components.put(component, list);
        }
    }
}
