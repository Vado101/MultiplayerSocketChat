package com.vadom.socket.chat.client;

import com.vadom.socket.chat.common.HandlersSelector;

public class Main {

    public static void main(String[] args) {

        HandlersSelector handlersSelector = new HandlersSelector();
        Client client = Client.connect(handlersSelector);

        if (client != null) {
            ClientCommandHandler clientCommandHandler =
                    new ClientCommandHandler(handlersSelector.getFreeID(),
                            client);

            handlersSelector.add(clientCommandHandler);
            handlersSelector.run();
        }

        handlersSelector.completion();
    }
}
