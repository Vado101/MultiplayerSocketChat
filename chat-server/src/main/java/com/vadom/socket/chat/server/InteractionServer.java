package com.vadom.socket.chat.server;

/**
 * Interaction with the server for sessions.
 *
 * Sessions should only be able to access the server with specific requests.
 */
public interface InteractionServer {

    void removeSession(Session session);

    void sendAll(String message, Session session);
}
