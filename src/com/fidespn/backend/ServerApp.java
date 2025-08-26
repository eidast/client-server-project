package com.fidespn.backend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal ServerSocket-based server using a simple UTF protocol.
 * Phase 1: LOGIN, REGISTER, RESET_PASSWORD.
 */
public class ServerApp {
    private final int port;
    private final ExecutorService pool;
    private final ConcurrentHashMap<String, String> tokenToUser = new ConcurrentHashMap<>();

    public ServerApp(int port) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors()));
    }

    public void start() throws Exception {
        DerbyUtil.ensureSchema();
        seedIfEmpty();
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket s = server.accept();
                pool.submit(() -> handleClient(s));
            }
        }
    }

    private void seedIfEmpty() throws Exception {
        try (var c = DerbyUtil.getConnection()) {
            boolean hasUsers;
            try (var rs = c.createStatement().executeQuery("SELECT 1 FROM users FETCH FIRST ROW ONLY")) {
                hasUsers = rs.next();
            }
            if (!hasUsers) {
                System.out.println("Seeding Derby with default data...");
                // Users
                try (var ps = c.prepareStatement("INSERT INTO users(id,username,password,email,role) VALUES(?,?,?,?,?)")) {
                    ps.setString(1, java.util.UUID.randomUUID().toString()); ps.setString(2, "admin"); ps.setString(3, "admin123"); ps.setString(4, "admin@fidespn.com"); ps.setString(5, "admin"); ps.executeUpdate();
                }
                try (var ps = c.prepareStatement("INSERT INTO users(id,username,password,email,role) VALUES(?,?,?,?,?)")) {
                    ps.setString(1, java.util.UUID.randomUUID().toString()); ps.setString(2, "corresponsal1"); ps.setString(3, "pass123"); ps.setString(4, "corresponsal1@fidespn.com"); ps.setString(5, "correspondent"); ps.executeUpdate();
                }
                try (var ps = c.prepareStatement("INSERT INTO users(id,username,password,email,role) VALUES(?,?,?,?,?)")) {
                    ps.setString(1, java.util.UUID.randomUUID().toString()); ps.setString(2, "fanatico1"); ps.setString(3, "pass123"); ps.setString(4, "fanatico1@fidespn.com"); ps.setString(5, "fanatic"); ps.executeUpdate();
                }
                try (var ps = c.prepareStatement("INSERT INTO users(id,username,password,email,role) VALUES(?,?,?,?,?)")) {
                    ps.setString(1, java.util.UUID.randomUUID().toString()); ps.setString(2, "fanatico2"); ps.setString(3, "pass123"); ps.setString(4, "fanatico2@fidespn.com"); ps.setString(5, "fanatic"); ps.executeUpdate();
                }
                // Teams (subset)
                try (var ps = c.prepareStatement("INSERT INTO teams(team_id,code,name,country,flag_url) VALUES(?,?,?,?,?)")) {
                    ps.setString(1, "USA"); ps.setString(2, "USA"); ps.setString(3, "USA"); ps.setString(4, "Estados Unidos"); ps.setString(5, "url_usa.png"); ps.executeUpdate();
                }
                try (var ps = c.prepareStatement("INSERT INTO teams(team_id,code,name,country,flag_url) VALUES(?,?,?,?,?)")) {
                    ps.setString(1, "MEX"); ps.setString(2, "MEX"); ps.setString(3, "México"); ps.setString(4, "México"); ps.setString(5, "url_mex.png"); ps.executeUpdate();
                }
                try (var ps = c.prepareStatement("INSERT INTO teams(team_id,code,name,country,flag_url) VALUES(?,?,?,?,?)")) {
                    ps.setString(1, "BRA"); ps.setString(2, "BRA"); ps.setString(3, "Brasil"); ps.setString(4, "Brasil"); ps.setString(5, "url_bra.png"); ps.executeUpdate();
                }
                try (var ps = c.prepareStatement("INSERT INTO teams(team_id,code,name,country,flag_url) VALUES(?,?,?,?,?)")) {
                    ps.setString(1, "ARG"); ps.setString(2, "ARG"); ps.setString(3, "Argentina"); ps.setString(4, "Argentina"); ps.setString(5, "url_arg.png"); ps.executeUpdate();
                }
                // One sample match
                try (var stmt = c.createStatement()) {
                    String matchId = java.util.UUID.randomUUID().toString();
                    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                    stmt.executeUpdate("INSERT INTO matches(match_id,date_ts,time_txt,home_team_id,away_team_id,score_home,score_away,correspondent_id,status) " +
                            "VALUES('" + matchId + "', '" + now + "', '10:00', 'USA','MEX', 0, 0, (SELECT id FROM users WHERE username='corresponsal1'), 'upcoming')");
                }
            }
        }
    }

    private void handleClient(Socket socket) {
        try (Socket s = socket;
             DataInputStream in = new DataInputStream(s.getInputStream());
             DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
            while (true) {
                String msg = in.readUTF();
                String res = process(msg);
                out.writeUTF(res);
                out.flush();
            }
        } catch (IOException e) {
            // Connection closed or error; nothing else to do
        }
    }

    private String process(String msg) {
        try {
            String[] parts = msg.split("\\|", -1);
            String cmd = parts[0];
            switch (cmd) {
                case "PING":
                    return ok("PONG");
                case "LOGIN":
                    return handleLogin(parts);
                case "REGISTER":
                    return handleRegister(parts);
                case "RESET_PASSWORD":
                    return handleReset(parts);
                case "GET_TEAMS":
                    return handleGetTeams(parts);
                case "GET_MATCHES":
                    return handleGetMatches(parts);
                case "GET_EVENTS":
                    return handleGetEvents(parts);
                case "ADD_EVENT":
                    return handleAddEvent(parts);
                case "UPDATE_FAVORITES":
                    return handleUpdateFavorites(parts);
                case "GET_USERS":
                    return handleGetUsers(parts);
                case "CREATE_USER":
                    return handleCreateUser(parts);
                case "UPDATE_USER":
                    return handleUpdateUser(parts);
                case "DELETE_USER":
                    return handleDeleteUser(parts);
                default:
                    return err("UNKNOWN_CMD", "Comando no soportado: " + cmd);
            }
        } catch (Exception ex) {
            return err("SERVER_ERROR", ex.getMessage() == null ? ex.toString() : ex.getMessage());
        }
    }

    private String handleGetTeams(String[] p) throws Exception {
        try (var c = DerbyUtil.getConnection(); var rs = c.createStatement().executeQuery("SELECT team_id,code,name,country FROM teams ORDER BY name")) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                if (sb.length() > 0) sb.append('\n');
                sb.append(rs.getString(1)).append(',').append(rs.getString(2)).append(',').append(rs.getString(3)).append(',').append(rs.getString(4));
            }
            return ok(sb.toString());
        }
    }

    private String handleGetMatches(String[] p) throws Exception {
        try (var c = DerbyUtil.getConnection(); var rs = c.createStatement().executeQuery("SELECT match_id,date_ts,time_txt,home_team_id,away_team_id,score_home,score_away,status,correspondent_id FROM matches ORDER BY date_ts")) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                if (sb.length() > 0) sb.append('\n');
                sb.append(rs.getString(1)).append(',')
                  .append(rs.getTimestamp(2).getTime()).append(',')
                  .append(rs.getString(3)).append(',')
                  .append(rs.getString(4)).append(',')
                  .append(rs.getString(5)).append(',')
                  .append(rs.getInt(6)).append(',')
                  .append(rs.getInt(7)).append(',')
                  .append(rs.getString(8)).append(',')
                  .append(rs.getString(9) == null ? "" : rs.getString(9));
            }
            return ok(sb.toString());
        }
    }

    private String handleGetEvents(String[] p) throws Exception {
        if (p.length < 2) return err("BAD_REQUEST", "GET_EVENTS|matchId");
        String matchId = p[1];
        try (var c = DerbyUtil.getConnection(); var ps = c.prepareStatement("SELECT event_id,event_minute,type,ts,description FROM match_events WHERE match_id=? ORDER BY ts")) {
            ps.setString(1, matchId);
            try (var rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    if (sb.length() > 0) sb.append('\n');
                    String desc = rs.getString(5);
                    String b64 = java.util.Base64.getEncoder().encodeToString((desc == null ? "" : desc).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    sb.append(rs.getString(1)).append(',')
                      .append(rs.getInt(2)).append(',')
                      .append(rs.getString(3)).append(',')
                      .append(rs.getTimestamp(4).getTime()).append(',')
                      .append(b64);
                }
                return ok(sb.toString());
            }
        }
    }

    private String handleAddEvent(String[] p) throws Exception {
        if (p.length < 6) return err("BAD_REQUEST", "ADD_EVENT|token|matchId|minute|type|base64(description)");
        String token = p[1];
        if (!tokenToUser.containsKey(token)) return err("UNAUTHORIZED", "Token inválido");
        String matchId = p[2];
        int minute = Integer.parseInt(p[3]);
        String type = p[4];
        String desc = new String(java.util.Base64.getDecoder().decode(p[5]), java.nio.charset.StandardCharsets.UTF_8);
        String eventId = java.util.UUID.randomUUID().toString();
        try (var c = DerbyUtil.getConnection(); var ps = c.prepareStatement("INSERT INTO match_events(event_id,match_id,event_minute,type,description,ts) VALUES(?,?,?,?,?,CURRENT_TIMESTAMP)")) {
            ps.setString(1, eventId);
            ps.setString(2, matchId);
            ps.setInt(3, minute);
            ps.setString(4, type);
            ps.setString(5, desc);
            ps.executeUpdate();
        }
        return ok(eventId);
    }

    private String handleUpdateFavorites(String[] p) throws Exception {
        // UPDATE_FAVORITES|token|userId|team1,team2,... (empty allowed)
        if (p.length < 4) return err("BAD_REQUEST", "UPDATE_FAVORITES|token|userId|team1,team2,...");
        String token = p[1];
        if (!tokenToUser.containsKey(token)) return err("UNAUTHORIZED", "Token inválido");
        String userIdFromToken = tokenToUser.get(token);
        String userId = p[2];
        if (userId == null || userId.isEmpty()) return err("BAD_REQUEST", "userId requerido");
        if (!userIdFromToken.equals(userId)) return err("FORBIDDEN", "Token no pertenece al usuario");
        String csv = p[3];

        String[] teams = csv == null || csv.isEmpty() ? new String[0] : csv.split(",");
        try (var c = DerbyUtil.getConnection()) {
            c.setAutoCommit(false);
            try (var del = c.prepareStatement("DELETE FROM favorites WHERE user_id=?")) {
                del.setString(1, userId);
                del.executeUpdate();
            }
            if (teams.length > 0) {
                try (var ins = c.prepareStatement("INSERT INTO favorites(user_id,team_id) VALUES(?,?)")) {
                    for (String t : teams) {
                        if (t == null || t.isEmpty()) continue;
                        ins.setString(1, userId);
                        ins.setString(2, t);
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
            }
            c.commit();
        }
        return ok("");
    }

    private String handleLogin(String[] p) throws Exception {
        if (p.length < 3) return err("BAD_REQUEST", "LOGIN|username|password");
        String username = p[1];
        String password = p[2];
        // JDBC lookup
        try (var c = DerbyUtil.getConnection(); var ps = c.prepareStatement("SELECT id,password,email,role FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return err("NOT_FOUND", "Usuario no encontrado");
                String id = rs.getString(1);
                String dbPass = rs.getString(2);
                String email = rs.getString(3);
                String role = rs.getString(4);
                if (!dbPass.equals(password)) return err("INVALID_CREDENTIALS", "Contraseña incorrecta");
                String token = UUID.randomUUID().toString();
                tokenToUser.put(token, id);
                // token,userId,role,username,email
                return ok(token + "," + id + "," + role + "," + username + "," + (email == null ? "" : email));
            }
        }
    }

    private String handleRegister(String[] p) throws Exception {
        if (p.length < 5) return err("BAD_REQUEST", "REGISTER|username|password|email|role");
        String username = p[1];
        String password = p[2];
        String email = p[3];
        String role = p[4];
        String id = UUID.randomUUID().toString();
        try (var c = DerbyUtil.getConnection()) {
            try (var check = c.prepareStatement("SELECT 1 FROM users WHERE username=?")) {
                check.setString(1, username);
                try (var rs = check.executeQuery()) {
                    if (rs.next()) return err("DUPLICATE", "El nombre de usuario ya existe");
                }
            }
            try (var ps = c.prepareStatement("INSERT INTO users(id,username,password,email,role) VALUES(?,?,?,?,?)")) {
                ps.setString(1, id);
                ps.setString(2, username);
                ps.setString(3, password);
                ps.setString(4, email);
                ps.setString(5, role);
                ps.executeUpdate();
                return ok("");
            }
        }
    }

    private String handleReset(String[] p) throws Exception {
        if (p.length < 4) return err("BAD_REQUEST", "RESET_PASSWORD|username|email|newPassword");
        String username = p[1];
        String email = p[2];
        String newPass = p[3];
        try (var c = DerbyUtil.getConnection(); var ps = c.prepareStatement("SELECT id,email FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return err("NOT_FOUND", "Usuario no encontrado");
                String id = rs.getString(1);
                String dbEmail = rs.getString(2);
                if (!dbEmail.equalsIgnoreCase(email)) return err("INVALID_CREDENTIALS", "Email no coincide");
                try (var upd = c.prepareStatement("UPDATE users SET password=? WHERE id=?")) {
                    upd.setString(1, newPass);
                    upd.setString(2, id);
                    upd.executeUpdate();
                }
                return ok("");
            }
        }
    }

    private boolean isAdmin(String token) throws Exception {
        String userId = tokenToUser.get(token);
        if (userId == null) return false;
        try (var c = DerbyUtil.getConnection(); var ps = c.prepareStatement("SELECT role FROM users WHERE id=?")) {
            ps.setString(1, userId);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String role = rs.getString(1);
                return role != null && role.equalsIgnoreCase("admin");
            }
        }
    }

    private String handleGetUsers(String[] p) throws Exception {
        if (p.length < 2) return err("BAD_REQUEST", "GET_USERS|token");
        String token = p[1];
        if (!isAdmin(token)) return err("FORBIDDEN", "Solo admin");
        try (var c = DerbyUtil.getConnection(); var rs = c.createStatement().executeQuery("SELECT id,username,email,role FROM users ORDER BY username")) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                if (sb.length() > 0) sb.append('\n');
                sb.append(rs.getString(1)).append(',')
                  .append(rs.getString(2)).append(',')
                  .append(rs.getString(3)).append(',')
                  .append(rs.getString(4));
            }
            return ok(sb.toString());
        }
    }

    private String handleCreateUser(String[] p) throws Exception {
        if (p.length < 6) return err("BAD_REQUEST", "CREATE_USER|token|username|password|email|role");
        String token = p[1];
        if (!isAdmin(token)) return err("FORBIDDEN", "Solo admin");
        String username = p[2];
        String password = p[3];
        String email = p[4];
        String role = p[5];
        String id = java.util.UUID.randomUUID().toString();
        try (var c = DerbyUtil.getConnection()) {
            try (var check = c.prepareStatement("SELECT 1 FROM users WHERE username=?")) {
                check.setString(1, username);
                try (var rs = check.executeQuery()) { if (rs.next()) return err("DUPLICATE", "El nombre de usuario ya existe"); }
            }
            try (var ps = c.prepareStatement("INSERT INTO users(id,username,password,email,role) VALUES(?,?,?,?,?)")) {
                ps.setString(1, id);
                ps.setString(2, username);
                ps.setString(3, password);
                ps.setString(4, email);
                ps.setString(5, role);
                ps.executeUpdate();
            }
        }
        return ok(id);
    }

    private String handleUpdateUser(String[] p) throws Exception {
        if (p.length < 7) return err("BAD_REQUEST", "UPDATE_USER|token|id|username|password|email|role");
        String token = p[1];
        if (!isAdmin(token)) return err("FORBIDDEN", "Solo admin");
        String id = p[2];
        String username = p[3];
        String password = p[4];
        String email = p[5];
        String role = p[6];
        try (var c = DerbyUtil.getConnection()) {
            // Ensure unique username for other users
            try (var check = c.prepareStatement("SELECT 1 FROM users WHERE username=? AND id<>?")) {
                check.setString(1, username);
                check.setString(2, id);
                try (var rs = check.executeQuery()) { if (rs.next()) return err("DUPLICATE", "El nombre de usuario ya existe"); }
            }
            try (var ps = c.prepareStatement("UPDATE users SET username=?, password=?, email=?, role=? WHERE id=?")) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.setString(4, role);
                ps.setString(5, id);
                int n = ps.executeUpdate();
                if (n == 0) return err("NOT_FOUND", "Usuario no encontrado");
            }
        }
        return ok("");
    }

    private String handleDeleteUser(String[] p) throws Exception {
        if (p.length < 3) return err("BAD_REQUEST", "DELETE_USER|token|id");
        String token = p[1];
        if (!isAdmin(token)) return err("FORBIDDEN", "Solo admin");
        String id = p[2];
        try (var c = DerbyUtil.getConnection(); var ps = c.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setString(1, id);
            int n = ps.executeUpdate();
            if (n == 0) return err("NOT_FOUND", "Usuario no encontrado");
        }
        return ok("");
    }

    private static String ok(String payload) { return payload == null || payload.isEmpty() ? "OK" : "OK|" + payload; }
    private static String err(String code, String msg) { return "ERR|" + code + "|" + (msg == null ? "" : msg); }

    public static void main(String[] args) throws Exception {
        int port = 5432;
        new ServerApp(port).start();
    }
}


