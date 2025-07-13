package com.fidespn.model;

import java.io.Serializable;

public class Team implements Serializable {
    private static final long serialVersionUID = 1L;
    private String teamId;
    private String name;
    private String country;
    private String logoUrl;

    public Team(String teamId, String name, String country, String logoUrl) {
        this.teamId = teamId;
        this.name = name;
        this.country = country;
        this.logoUrl = logoUrl;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @Override
    public String toString() {
        return "Team{" +
               "teamId='" + teamId + '\'' +
               ", name='" + name + '\'' +
               ", country='" + country + '\'' +
               '}';
    }
}