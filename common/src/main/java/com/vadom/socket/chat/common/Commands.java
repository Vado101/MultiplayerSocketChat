package com.vadom.socket.chat.common;

public enum Commands implements Command<Commands> {
    CONNECT(""),
    LOGIN(""),
    WORK(""),
    LOGOUT(""),
    DISCONNECT(""),
    EXIT("");

    private final String description;

    Commands(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Commands getCommand(String command) {
        final String[] lineUp = command.substring(prefix.length()).split(" ");

        return Commands.valueOf(lineUp[0].toUpperCase());
    }
}
