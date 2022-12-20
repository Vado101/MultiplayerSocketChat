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
    private static final int TIME_OUT = 500;
    private final ServerSocket serverSocket;
    private final HandlersSelector handlersSelector;
    private final List<Session> sessions = new ArrayList<>();

    private Server(ServerSocket serverSocket,
                   HandlersSelector handlersSelector) {
        super(handlersSelector.getFreeID());
        this.serverSocket = serverSocket;
        this.handlersSelector = handlersSelector;
    }

    public static Server start(int port, HandlersSelector handlersSelector) {
        if (handlersSelector == null) {
            throw new IllegalArgumentException("eventSelector == null");
        }

        final ServerSocket serverSocket;

        try {
            System.out.println("Start server...");
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(TIME_OUT);
            System.out.println("Server running on port " +
                    serverSocket.getLocalPort());

            return new Server(serverSocket, handlersSelector);
        } catch (IOException e) {
            System.out.println("Failed to start server. " + e.getMessage());
        }

        return null;
    }

    public void stop() throws IOException {
        String quit = "Server shutdown...";
        sendAll(quit, null);

        for (Session session : sessions) {
            session.setRun(false);
        }

        serverSocket.close();
        handlersSelector.stop();

        System.out.println(quit);
    }

    @Override
    public void handle() {
        // Listening to new connections and creating sessions for them
        try {
            Socket socket = serverSocket.accept();
            Session session =
                    new Session(handlersSelector.getFreeID(), socket, this);
            sessions.add(session);
            handlersSelector.add(session);
            System.out.println("New client connected " +
                    socket.getRemoteSocketAddress() +
                    " with ID = " + session.getId());
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
