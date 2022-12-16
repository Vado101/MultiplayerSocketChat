package com.vadom.socket.chat.server;

import com.vadom.socket.chat.common.HandlersSelector;
import com.vadom.socket.chat.common.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Handler implements InteractionServer {
    private static final int START_ID = 0;
    private final ServerSocket serverSocket;
    private final HandlersSelector handlersSelector;
    private final List<Session> sessions = new ArrayList<>();
    private int counterID = START_ID;

    private Server(ServerSocket serverSocket,
                   HandlersSelector handlersSelector) {
        super(START_ID);
        this.serverSocket = serverSocket;
        this.handlersSelector = handlersSelector;
    }

    public static Server start(int port, HandlersSelector handlersSelector) {
        if (handlersSelector == null) {
            throw new IllegalArgumentException("eventSelector == null");
        }

        final ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
        } catch (IOException e) {
            System.out.println("Failed to start server. " + e.getMessage());

            return null;
        }

        return new Server(serverSocket, handlersSelector);
    }

    public void stop() throws IOException {
        sendAll("Server shutdown..." , null);

        for (Session session : sessions) {
            session.setRun(false);
        }

        serverSocket.close();
        handlersSelector.stop();
    }

    @Override
    public void handle() {
        // Listening to new connections and creating sessions for them
        try {
            Socket socket = serverSocket.accept();
            Session session =
                    new Session(++counterID, socket, this);
            sessions.add(session);
            handlersSelector.add(session);
        } catch (IOException e) {
            if (!(e instanceof SocketTimeoutException)) {
                System.out.println("An error occurred when listening to " +
                        "a socket on the server. " + e.getMessage());
            }
        }
    }

    @Override
    public void removeSession(Session session) {
        sessions.remove(session);
    }

    @Override
    public void sendAll(String message, Session exceptSession) {
        for (Session session : sessions) {
            if (!session.equals(exceptSession)) {
                session.send(message);
            }
        }
    }
}
