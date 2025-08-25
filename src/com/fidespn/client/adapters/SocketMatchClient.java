package com.fidespn.client.adapters;

import com.fidespn.client.net.SocketClient;

/**
 * Placeholder for match operations over socket.
 * Commands to be implemented in next phases (GET_MATCHES, ADD_EVENT, etc.).
 */
public class SocketMatchClient {
    private final SocketClient client;
    private final String token;

    public SocketMatchClient(String host, int port, String token) {
        this.client = new SocketClient(host, port);
        this.token = token;
    }
}


