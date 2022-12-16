package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.HandlersSelector;

public class Main {

    public static void main(String[] args) {
        HandlersSelector handlersSelector = new HandlersSelector();
        Server server = Server.start(6060, handlersSelector);

        if (server != null) {
            handlersSelector.add(server);
            handlersSelector.run();
        }
    }
}
