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
        int startID = 0;

        Assertions.assertEquals(startID, handlersSelector.getFreeID());

        Handler handler1 = createHandler(startID);
        Handler handler2 = createHandler(++startID);
        handlersSelector.add(handler1);
        handlersSelector.add(handler2);

        Assertions.assertEquals(++startID, handlersSelector.getFreeID());

        Handler handler3 = createHandler(++startID);
        handlersSelector.add(handler3);

        Assertions.assertEquals(--startID, handlersSelector.getFreeID());
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