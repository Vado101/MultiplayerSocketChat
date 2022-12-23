package com.vadom.socket.chat.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HandlersSelectorTest {

    @Test
    void addTest() {
        HandlersSelector handlersSelector = new HandlersSelector();
        Assertions.assertThrows(NullPointerException.class,
                () -> handlersSelector.add(null));
    }

    @Test
    void removeTest() {
        HandlersSelector handlersSelector = new HandlersSelector();
        Assertions.assertThrows(NullPointerException.class,
                () -> handlersSelector.remove(null));

        Handler handler = createHandler(handlersSelector.getFreeID());
        handlersSelector.add(handler);
        handlersSelector.remove(handler);
    }

    @Test
    void getFreeID() {
        HandlersSelector handlersSelector = new HandlersSelector();
        int countID = 100;

        for (int i = 0; i < countID; ++i) {
            handlersSelector.getFreeID();
        }

        Assertions.assertEquals(countID, handlersSelector.getFreeID());
    }

    private Handler createHandler(int id) {
        return new Handler(id) {
            @Override
            public void handle() {
                System.out.println("handler with ID=" + getId());
            }
        };
    }
}