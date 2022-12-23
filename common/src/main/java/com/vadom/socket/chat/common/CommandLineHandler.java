package com.vadom.socket.chat.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class CommandLineHandler extends Handler {

    protected final BufferedReader reader;

    public CommandLineHandler(int id) {
        super(id);
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public abstract void commandProcessing(String fullCommand);

    public void inputDataProcessing(String inputData) {}


    @Override
    public void close() {
        try {
            super.close();
            reader.close();
        } catch (IOException e) {
            System.out.println("Error occurred when closing the " +
                    "input stream for command line. " + e.getMessage());
        }
    }

    @Override
    public void handle() {
        try {
            if (reader.ready()) {
                String inputData = reader.readLine();

                if (Command.isCommand(inputData)) {
                    commandProcessing(inputData);
                } else {
                    inputDataProcessing(inputData);
                }
            }
        } catch (IOException e) {
            System.out.println("Error occurred when receiving a input data " +
                    "from command line. " + e.getMessage());
        } catch (IllegalArgumentException e) {
            int index = e.getMessage().lastIndexOf(".");
            System.out.println("Error: non-existent command entered \"" +
                    e.getMessage().substring(index + 1).toLowerCase() + "\"");
        }
    }
}
