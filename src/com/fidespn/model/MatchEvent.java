package com.fidespn.model;

import java.io.Serializable;
import java.util.Date;

public class MatchEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String eventId;
    private String matchId;
    private int minute;
    private String type;
    private String description;
    private Date timestamp;

    public MatchEvent(String eventId, String matchId, int minute, String type, String description) {
        this.eventId = eventId;
        this.matchId = matchId;
        this.minute = minute;
        this.type = type;
        this.description = description;
        this.timestamp = new Date();
    }

    public String getEventId() {
        return eventId;
    }

    public String getMatchId() {
        return matchId;
    }

    public int getMinute() {
        return minute;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "MatchEvent{" +
               "eventId='" + eventId + '\'' +
               ", matchId='" + matchId + '\'' +
               ", minute=" + minute +
               ", type='" + type + '\'' +
               ", description='" + description + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}