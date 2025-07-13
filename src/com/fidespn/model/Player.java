package com.fidespn.model;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private String playerId;
    private String name;
    private String position;
    private String teamId;

    public Player(String playerId, String name, String position, String teamId) {
        this.playerId = playerId;
        this.name = name;
        this.position = position;
        this.teamId = teamId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return "Player{" +
               "playerId='" + playerId + '\'' +
               ", name='" + name + '\'' +
               ", position='" + position + '\'' +
               ", teamId='" + teamId + '\'' +
               '}';
    }
}