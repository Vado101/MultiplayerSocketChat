package com.vadom.socket.chat.common;

import java.util.*;

/**
 * Handlers management.
 */
public class HandlersSelector {
    private final List<Handler> handlers =
            Collections.synchronizedList(new LinkedList<>());
    private final Queue<Handler> handlersToAdd = new LinkedList<>();
    private final Queue<Handler> handlersToRemove = new LinkedList<>();
    private boolean isRun;
    private int id;

    public int getFreeID() {
        return id++;
    }

    public void add(Handler handler) {
        Objects.requireNonNull(handler);

        if (!handlers.contains(handler)) {
            if (isRun) {
                handlersToAdd.add(handler);
            } else {
                handlers.add(handler);
            }
        }
    }

    public void remove(Handler handler) {
        Objects.requireNonNull(handler);

        if (handlers.contains(handler)) {
            if (isRun) {
                handlersToRemove.add(handler);
            } else {
                handlers.remove(handler);
            }
        }
    }

    public void run() {
        isRun = true;

        while (isRun) {
            while (!handlersToAdd.isEmpty()) {
                handlers.add(handlersToAdd.poll());
            }

            while (!handlersToRemove.isEmpty()) {
                handlers.remove(handlersToRemove.poll());
            }

            for (Handler handler : handlers) {
                handler.handle();
            }
        }
    }

    public void stop() {
        isRun = false;
    }

    public void completion() {
        for (Handler handler : handlers) {
            handler.close();
        }

        handlers.clear();
    }
}
