package com.vadom.socket.chat.common;

import java.util.*;

/**
 * Handlers management.
 */
public class HandlersSelector {
    private final List<Handler> handlers =
            Collections.synchronizedList(new LinkedList<>());
    private final Queue<Handler> handlerQueue = new LinkedList<>();
    private boolean isRun;

    public int getFreeID() {
        int id = 0;

        for (int i = 0; i < handlers.size(); ++i) {
            if (handlers.get(i).getId() == id) {
                ++id;
                i = -1;
            }
        }

        return id;
    }

    public void add(Handler handler) {
        Objects.requireNonNull(handler);

        if (!handlers.contains(handler)) {
            if (isRun) {
                handlerQueue.add(handler);
            } else {
                handlers.add(handler);
            }
        }
    }

    public void remove(Handler handler) {
        Objects.requireNonNull(handler);

        if (handlers.contains(handler)) {
            if (isRun) {
                handler.setRun(false);
            } else {
                handlers.remove(handler);
            }
        }
    }

    public void run() {
        isRun = true;

        while (isRun) {
            ListIterator<Handler> iterator = handlers.listIterator();

            while (!handlerQueue.isEmpty()) {
                iterator.add(handlerQueue.poll());
            }

            while (iterator.hasNext()) {
                Handler handler = iterator.next();

                if (handler.isRun()) {
                    handler.handle();
                } else {
                    iterator.remove();
                }
            }
        }
    }

    public void stop() {
        isRun = false;
    }

    public void completion() {
        // disable all handlers
        for (Handler handler : handlers) {
            handler.setRun(false);
        }
    }
}
