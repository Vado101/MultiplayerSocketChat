package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.Constant;
import com.vadom.socket.chat.common.HandlersSelector;

public class Main {

    public static void main(String[] args) {
        HandlersSelector handlersSelector = new HandlersSelector();
        Server server = Server.start(Constant.DEFAULT_PORT, handlersSelector);

        if (server != null) {
            ServerCommandHandler serverCommandHandler =
                    new ServerCommandHandler(handlersSelector.getFreeID(),
                            server);

            handlersSelector.add(server);
            handlersSelector.add(serverCommandHandler);
            handlersSelector.run();
        }

        handlersSelector.completion();
    }
}
