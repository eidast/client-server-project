package com.fidespn.backend;

import java.sql.*;

/**
 * Small helper for Apache Derby embedded setup and schema initialization.
 */
public final class DerbyUtil {
    private static final String JDBC_URL = "jdbc:derby:./data/fidespn;create=true";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    private DerbyUtil() {}

    /**
     * Returns a new JDBC connection to the embedded Derby database.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ignored) {
        }
        return DriverManager.getConnection(JDBC_URL);
    }

    /**
     * Ensures all required tables exist; creates them if missing.
     */
    public static void ensureSchema() throws SQLException {
        try (Connection c = getConnection()) {
            DatabaseMetaData meta = c.getMetaData();
            if (!tableExists(meta, "USERS")) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("CREATE TABLE users (" +
                            "id VARCHAR(36) PRIMARY KEY, " +
                            "username VARCHAR(64) UNIQUE NOT NULL, " +
                            "password VARCHAR(128) NOT NULL, " +
                            "email VARCHAR(128) NOT NULL, " +
                            "role VARCHAR(32) NOT NULL)");
                }
            }
            if (!tableExists(meta, "TEAMS")) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("CREATE TABLE teams (" +
                            "team_id VARCHAR(16) PRIMARY KEY, " +
                            "code VARCHAR(16), " +
                            "name VARCHAR(64), " +
                            "country VARCHAR(64), " +
                            "flag_url VARCHAR(256))");
                }
            }
            if (!tableExists(meta, "MATCHES")) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("CREATE TABLE matches (" +
                            "match_id VARCHAR(36) PRIMARY KEY, " +
                            "date_ts TIMESTAMP, " +
                            "time_txt VARCHAR(16), " +
                            "home_team_id VARCHAR(16), " +
                            "away_team_id VARCHAR(16), " +
                            "score_home INT, " +
                            "score_away INT, " +
                            "correspondent_id VARCHAR(36), " +
                            "status VARCHAR(16))");
                }
            }
            if (!tableExists(meta, "MATCH_EVENTS")) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("CREATE TABLE match_events (" +
                            "event_id VARCHAR(36) PRIMARY KEY, " +
                            "match_id VARCHAR(36) NOT NULL, " +
                            "event_minute INT, " +
                            "type VARCHAR(32), " +
                            "description CLOB, " +
                            "ts TIMESTAMP)");
                }
            }
            if (!tableExists(meta, "CHAT_MESSAGES")) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("CREATE TABLE chat_messages (" +
                            "message_id VARCHAR(36) PRIMARY KEY, " +
                            "match_id VARCHAR(36) NOT NULL, " +
                            "sender_id VARCHAR(36), " +
                            "sender_username VARCHAR(64), " +
                            "content CLOB, " +
                            "ts TIMESTAMP)");
                }
            }
            if (!tableExists(meta, "FAVORITES")) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("CREATE TABLE favorites (" +
                            "user_id VARCHAR(36) NOT NULL, " +
                            "team_id VARCHAR(16) NOT NULL, " +
                            "PRIMARY KEY (user_id, team_id))");
                }
            }
            if (!tableExists(meta, "PLAYERS")) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("CREATE TABLE players (" +
                            "player_id VARCHAR(36) PRIMARY KEY, " +
                            "name VARCHAR(64), " +
                            "position VARCHAR(32), " +
                            "team_id VARCHAR(16))");
                }
            }
            if (!tableExists(meta, "MATCH_LINEUPS")) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("CREATE TABLE match_lineups (" +
                            "match_id VARCHAR(36) NOT NULL, " +
                            "team_id VARCHAR(16) NOT NULL, " +
                            "player_id VARCHAR(36) NOT NULL, " +
                            "PRIMARY KEY (match_id, team_id, player_id))");
                }
            }
        }
    }

    private static boolean tableExists(DatabaseMetaData meta, String table) throws SQLException {
        try (ResultSet rs = meta.getTables(null, null, table, null)) {
            return rs.next();
        }
    }
}


