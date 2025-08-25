package com.fidespn.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Thin socket client for sending/receiving framed UTF messages.
 */
public class SocketClient implements AutoCloseable {
    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void connect() throws IOException {
        if (socket != null && socket.isConnected()) return;
        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public synchronized String send(String message) throws IOException {
        if (socket == null || !socket.isConnected()) connect();
        out.writeUTF(message);
        out.flush();
        return in.readUTF();
    }

    @Override
    public synchronized void close() throws IOException {
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}


