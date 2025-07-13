package com.fidespn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match implements Serializable {
    private static final long serialVersionUID = 1L;
    private String matchId;
    private Date date;
    private String time;
    private Team homeTeam;
    private Team awayTeam;
    private int scoreHome;
    private int scoreAway;
    private Map<String, List<Player>> lineups;
    private String correspondentId;
    private List<MatchEvent> events;
    private String status;
    private String chatId;

    public Match(String matchId, Date date, String time, Team homeTeam, Team awayTeam, String correspondentId) {
        this.matchId = matchId;
        this.date = date;
        this.time = time;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.scoreHome = 0;
        this.scoreAway = 0;
        this.lineups = new HashMap<>();
        this.events = new ArrayList<>();
        this.correspondentId = correspondentId;
        this.status = "upcoming";
        this.chatId = "chat-" + matchId;
    }

    public String getMatchId() {
        return matchId;
    }

    public Date getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public int getScoreHome() {
        return scoreHome;
    }

    public int getScoreAway() {
        return scoreAway;
    }

    public Map<String, List<Player>> getLineups() {
        return lineups;
    }

    public String getCorrespondentId() {
        return correspondentId;
    }

    public List<MatchEvent> getEvents() {
        return events;
    }

    public String getStatus() {
        return status;
    }

    public String getChatId() {
        return chatId;
    }

    public void setScoreHome(int scoreHome) {
        this.scoreHome = scoreHome;
    }

    public void setScoreAway(int scoreAway) {
        this.scoreAway = scoreAway;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCorrespondentId(String correspondentId) {
        this.correspondentId = correspondentId;
    }

    public void addLineup(String teamId, List<Player> players) {
        this.lineups.put(teamId, players);
    }

    public void addEvent(MatchEvent event) {
        this.events.add(event);
    }
    
    @Override
    public String toString() {
        return "Match{" +
               "matchId='" + matchId + '\'' +
               ", homeTeam=" + homeTeam.getName() +
               ", awayTeam=" + awayTeam.getName() +
               ", score=" + scoreHome + "-" + scoreAway +
               ", status='" + status + '\'' +
               '}';
    }
}