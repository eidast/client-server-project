package com.fidespn.client.adapters;

import com.fidespn.client.net.SocketClient;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

    public List<String[]> getTeams() throws Exception {
        String res = client.send("GET_TEAMS");
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
        String payload = res.length() > 3 && res.startsWith("OK|") ? res.substring(3) : "";
        List<String[]> out = new ArrayList<>();
        if (payload.isEmpty()) return out;
        for (String line : payload.split("\n")) {
            out.add(line.split(",", -1));
        }
        return out;
    }

    public List<String[]> getMatches() throws Exception {
        String res = client.send("GET_MATCHES");
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
        String payload = res.length() > 3 && res.startsWith("OK|") ? res.substring(3) : "";
        List<String[]> out = new ArrayList<>();
        if (payload.isEmpty()) return out;
        for (String line : payload.split("\n")) {
            out.add(line.split(",", -1));
        }
        return out;
    }

    public List<String[]> getEvents(String matchId) throws Exception {
        String res = client.send("GET_EVENTS|" + matchId);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
        String payload = res.length() > 3 && res.startsWith("OK|") ? res.substring(3) : "";
        List<String[]> out = new ArrayList<>();
        if (payload.isEmpty()) return out;
        for (String line : payload.split("\n")) {
            out.add(line.split(",", -1));
        }
        return out;
    }

    public String addEvent(String matchId, int minute, String type, String description) throws Exception {
        String b64 = Base64.getEncoder().encodeToString(description.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String res = client.send("ADD_EVENT|" + token + "|" + matchId + "|" + minute + "|" + type + "|" + b64);
        if (res.startsWith("OK|")) return res.substring(3);
        if (res.equals("OK")) return "";
        throw new IllegalStateException(parseErr(res));
    }

    public String createMatch(long dateMillis, String time, String homeId, String awayId, String correspondentUsername) throws Exception {
        String res = client.send("CREATE_MATCH|" + token + "|" + dateMillis + "|" + time + "|" + homeId + "|" + awayId + "|" + correspondentUsername);
        if (res.startsWith("OK|")) return res.substring(3);
        if (res.equals("OK")) return "";
        throw new IllegalStateException(parseErr(res));
    }

    public void deleteMatch(String matchId) throws Exception {
        String res = client.send("DELETE_MATCH|" + token + "|" + matchId);
        if (!res.startsWith("OK")) throw new IllegalStateException(parseErr(res));
    }

    private String parseErr(String res) {
        if (res.startsWith("ERR|")) return res.substring(4);
        return res;
    }
}


