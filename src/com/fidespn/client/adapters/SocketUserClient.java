package com.fidespn.client.adapters;

import com.fidespn.client.net.SocketClient;

/**
 * Auth adapter over raw socket protocol.
 */
public class SocketUserClient {
    private final SocketClient client;
    private String token;

    public SocketUserClient(String host, int port) {
        this.client = new SocketClient(host, port);
    }

    public String login(String username, String password) throws Exception {
        String res = client.send("LOGIN|" + username + "|" + password);
        if (res.startsWith("OK|")) {
            String payload = res.substring(3);
            // token,userId,role,username,email
            String[] parts = payload.split(",", -1);
            token = parts[0];
            return token;
        }
        throw new IllegalStateException(parseErr(res));
    }

    public void register(String username, String password, String email, String role) throws Exception {
        String res = client.send("REGISTER|" + username + "|" + password + "|" + email + "|" + role);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
    }

    public void resetPassword(String username, String email, String newPassword) throws Exception {
        String res = client.send("RESET_PASSWORD|" + username + "|" + email + "|" + newPassword);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
    }

    public String getToken() { return token; }

    private String parseErr(String res) {
        if (res.startsWith("ERR|")) return res.substring(4);
        return res;
    }
}


