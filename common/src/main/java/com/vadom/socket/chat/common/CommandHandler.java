package com.vadom.socket.chat.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class CommandHandler extends Handler {

    protected BufferedReader reader;

    public CommandHandler(int id) {
        super(id);
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public abstract void processing(Commands command);

    @Override
    public void setRun(boolean run) {
        if (isRun != run) {
            isRun = run;

            if (isRun) {
                reader = new BufferedReader(new InputStreamReader(System.in));
            } else {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error occurred when closing the " +
                            "input stream for command line. " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void handle() {
        try {
            if (isRun && reader.ready()) {
                String inputData = reader.readLine();

                if (Command.isCommand(inputData)) {
                    Commands command = Commands.EXIT.getCommand(inputData);
                    processing(command);
                }
            }
        } catch (IOException e) {
            System.out.println("Error occurred when receiving a command from" +
                    " command line. " + e.getMessage());
        } catch (IllegalArgumentException e) {
            int index = e.getMessage().lastIndexOf(".");
            System.out.println("Error: non-existent command entered \"" +
                    e.getMessage().substring(index + 1).toLowerCase() + "\"");
        }
    }
}
