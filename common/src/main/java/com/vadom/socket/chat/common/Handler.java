package com.vadom.socket.chat.common;

public abstract class Handler {
    private final int id;
    protected boolean isRun;

    public Handler(int id) {
        this.id = id;
        isRun = true;
    }

    public int getId() {
        return id;
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public abstract void handle();
}
