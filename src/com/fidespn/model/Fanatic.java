package com.fidespn.model;

import java.util.ArrayList;
import java.util.List;

public class Fanatic extends User {
    private static final long serialVersionUID = 1L;
    private List<String> favoriteTeamIds;

    public Fanatic(String userId, String username, String password, String email) {
        super(userId, username, password, email);
        this.favoriteTeamIds = new ArrayList<>();
    }

    public List<String> getFavoriteTeamIds() {
        return favoriteTeamIds;
    }

    public void addFavoriteTeam(String teamId) {
        if (!favoriteTeamIds.contains(teamId)) {
            favoriteTeamIds.add(teamId);
            System.out.println("Fanático " + username + " agregó a " + teamId + " a sus favoritos.");
        }
    }

    public void removeFavoriteTeam(String teamId) {
        favoriteTeamIds.remove(teamId);
        System.out.println("Fanático " + username + " eliminó a " + teamId + " de sus favoritos.");
    }

    @Override
    public String getDashboardGreeting() {
        return "Hola, Fanático " + getUsername() + ". ¡Disfruta el Mundial United 2026!";
    }

    public void viewMatchDetails(String matchId) {
        System.out.println("Fanático " + username + " viendo detalles del partido " + matchId);
    }

    public void joinChat(String matchId) {
        System.out.println("Fanático " + username + " uniéndose al chat del partido " + matchId);
    }
    
    @Override
    public String toString() {
        return "Fanatic{" +
               "userId='" + userId + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", favoriteTeamIds=" + favoriteTeamIds +
               '}';
    }
}