package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.Constant;
import com.vadom.socket.chat.common.HandlersSelector;

public class Main {

    public static void main(String[] args) {
        HandlersSelector handlersSelector = new HandlersSelector();
        Server server = Server.start(Constant.DEFAULT_PORT, handlersSelector);

        if (server != null) {
            handlersSelector.add(server);

            ServerCommandHandler serverCommandHandler =
                    new ServerCommandHandler(handlersSelector.getFreeID(), server);
            handlersSelector.add(serverCommandHandler);

            handlersSelector.run();
        }
    }
}
