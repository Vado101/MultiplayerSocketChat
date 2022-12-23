package com.vadom.socket.chat.common;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum Commands implements Command<Commands> {
    CONNECT("Connect to the server"),
    LOGIN("Login to the server"),
    LOGOUT("Logout from the server"),
    DISCONNECT("Disconnect from the server"),
    EXIT("End the application"),
    HELP("Description of available application commands");

    private final String description;

    Commands(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Commands getCommand(String command) {
        final String[] parts =
                command.substring(prefix.length()).split(" ");

        return Commands.valueOf(parts[0].toUpperCase());
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static String createCommand(Commands command, KEY[] keys,
                                       String... args) {
        Objects.requireNonNull(command, "must not be command == null");

        StringBuilder fullCommand = new StringBuilder();
        fullCommand.append(Command.prefix)
                .append(command).append(" ");

        if (keys != null) {
            for (KEY key : keys) {
                fullCommand.append(Command.prefixKeys).append(key).append(" ");
            }
        }

        if (args != null) {
            for (String arg : args) {
                fullCommand.append(arg).append(" ");
            }
        }

        fullCommand.deleteCharAt(fullCommand.length() - 1);

        return fullCommand.toString();
    }

    public static boolean containsKey(String fullCommand, KEY... keys) {
        if (keys == null || keys.length == 0) {
            return false;
        }

        Map<Component, List<String>> components =
                Command.getCommandComponents(fullCommand);

        if (!components.containsKey(Component.KEYS)) {
            return false;
        }

        for (Commands.KEY key : keys) {
            if (!components.get(Component.KEYS).contains(key.toString())) {
                return false;
            }
        }

        return true;
    }

    public static String checkingArgumentsForErrorMessage(List<String> args) {
        String message = Command.getMessageFromArgs(args);

        if (message != null &&
                message.contains(Commands.ErrorCode.FAILED.name())) {
            return message;
        }

        return null;
    }

    public static String commandHelp(Commands... commands) {
        if (commands == null || commands.length == 0) {
            return null;
        }

        int length = 10;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Command list:\n");

        for (Commands command : commands) {
            stringBuilder.append("  ").append(command);

            int commandLength = command.toString().length();

            while (commandLength < length) {
                stringBuilder.append(" ");
                ++commandLength;
            }

            stringBuilder.append("\t")
                    .append(command.getDescription()).append("\n");
        }

        return stringBuilder.toString();
    }


    public enum KEY {
        NAME,
        CONFIRM;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    // Error code is written to args
    public enum ErrorCode {
        OK,
        FAILED
    }
}
