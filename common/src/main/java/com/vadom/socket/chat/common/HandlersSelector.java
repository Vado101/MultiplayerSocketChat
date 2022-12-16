package com.vadom.socket.chat.common;

import java.util.*;

/**
 * Handlers management.
 */
public class HandlersSelector {
    private final List<Handler> handlers =
            Collections.synchronizedList(new LinkedList<>());
    private final Queue<Handler> handlerQueue = new LinkedList<>();
    private boolean quitFlag;

    public void add(Handler handler) {
        handlerQueue.add(Objects.requireNonNull(handler));
    }

    public void run() {
        quitFlag = false;

        while (!quitFlag) {
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
        quitFlag = true;
    }
}
