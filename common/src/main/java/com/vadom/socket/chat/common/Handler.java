package com.vadom.socket.chat.common;

import java.io.Closeable;

public abstract class Handler implements Closeable {
    private final int id;
    protected boolean isRun;

    public Handler(int id) {
        this.id = id;
        this.isRun = true;
    }

    public int getId() {
        return id;
    }

    public boolean isRun() {
        return isRun;
    }

    @Override
    public void close() {
        isRun = false;
    }

    public abstract void handle();
}
