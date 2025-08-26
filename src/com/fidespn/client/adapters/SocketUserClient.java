package com.fidespn.client.adapters;

import com.fidespn.client.net.SocketClient;

/**
 * Auth adapter over raw socket protocol.
 */
public class SocketUserClient {
    private final SocketClient client;
    private String token;
    private String userId;
    private String role;
    private String username;
    private String email;

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
            userId = parts.length>1?parts[1]:null;
            role = parts.length>2?parts[2]:null;
            this.username = parts.length>3?parts[3]:null;
            email = parts.length>4?parts[4]:null;
            return token;
        }
        throw new IllegalStateException(parseErr(res));
    }

    public void register(String username, String password, String email, String role) throws Exception {
        String res = client.send("REGISTER|" + username + "|" + password + "|" + email + "|" + role);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
    }

    // Admin CRUD
    public java.util.List<String[]> getUsers() throws Exception {
        String res = client.send("GET_USERS|" + token);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
        String payload = res.length()>3 && res.startsWith("OK|") ? res.substring(3) : "";
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        if (payload.isEmpty()) return rows;
        for (String line : payload.split("\n")) rows.add(line.split(",", -1));
        return rows;
    }

    public String createUser(String username, String password, String email, String role) throws Exception {
        String res = client.send("CREATE_USER|" + token + "|" + username + "|" + password + "|" + email + "|" + role);
        if (res.startsWith("OK|")) return res.substring(3);
        if (res.equals("OK")) return "";
        throw new IllegalStateException(parseErr(res));
    }

    public void updateUser(String id, String username, String password, String email, String role) throws Exception {
        String res = client.send("UPDATE_USER|" + token + "|" + id + "|" + username + "|" + password + "|" + email + "|" + role);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
    }

    public void deleteUser(String id) throws Exception {
        String res = client.send("DELETE_USER|" + token + "|" + id);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
    }

    public void resetPassword(String username, String email, String newPassword) throws Exception {
        String res = client.send("RESET_PASSWORD|" + username + "|" + email + "|" + newPassword);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
    }

    public void updateFavorites(String userId, java.util.List<String> teamIds) throws Exception {
        String csv = String.join(",", teamIds == null ? java.util.Collections.emptyList() : teamIds);
        String res = client.send("UPDATE_FAVORITES|" + token + "|" + userId + "|" + csv);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    private String parseErr(String res) {
        if (res.startsWith("ERR|")) return res.substring(4);
        return res;
    }
}


