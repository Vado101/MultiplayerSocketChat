package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.Handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.Socket;

public class Session extends Handler {
    private final Socket socket;
    private final InteractionServer server;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Session(int id, Socket socket, InteractionServer server)
            throws IOException {
        super(id);
        this.socket = socket;
        this.server = server;

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void send(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Error occurred when sending a message to the " +
                    "client in session with ID = " + getId() + e.getMessage());

            if (!(e instanceof UTFDataFormatException)) {
                server.removeSession(this);
                setRun(false);
            }
        }
    }

    @Override
    public void setRun(boolean run) {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error occurred when closing the session " +
                    "with ID = " + getId() + ". " + e.getMessage());
        }

        super.setRun(run);
    }

    @Override
    public void handle() {
        try {
            if (inputStream.available() != 0) {
                String message = getId() + ": " + inputStream.readUTF();
                server.sendAll(message, this);
            }
        } catch (IOException e) {
            System.out.println("Error occurred when receiving a message from " +
                    "client in session with ID = " + getId() + e.getMessage());

            if (!(e instanceof UTFDataFormatException)) {
                server.removeSession(this);
                setRun(false);
            }
        }
    }
}
