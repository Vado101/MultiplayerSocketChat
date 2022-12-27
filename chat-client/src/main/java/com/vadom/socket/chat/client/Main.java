package com.vadom.socket.chat.client;

import com.vadom.socket.chat.common.HandlersSelector;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        HandlersSelector handlersSelector = new HandlersSelector();
        Client client = Client.connect(handlersSelector);

        if (client != null) {
            client.login();

            ClientCommandLineHandler clientCommandHandler =
                    new ClientCommandLineHandler(handlersSelector.getFreeID(),
                            client);

            handlersSelector.add(client);
            handlersSelector.add(clientCommandHandler);
            handlersSelector.run();
        }

        handlersSelector.completion();
    }
}
