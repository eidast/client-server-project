package com.fidespn.model;

import java.util.ArrayList;
import java.util.List;

public class Correspondent extends User {
    private static final long serialVersionUID = 1L;

    public Correspondent(String userId, String username, String password, String email) {
        super(userId, username, password, email);
    }

    @Override
    public String getDashboardGreeting() {
        return "Bienvenido, Corresponsal " + getUsername() + ". Revisa tus partidos asignados para reportar.";
    }

    public void reportGoal(String matchId, String scorer, int minute) {
        System.out.println("Corresponsal " + username + " reporta gol en partido " + matchId + " de " + scorer + " al minuto " + minute);
    }

    public void reportCard(String matchId, String playerName, String cardType, int minute) {
        System.out.println("Corresponsal " + username + " reporta tarjeta " + cardType + " a " + playerName + " en partido " + matchId + " al minuto " + minute);
    }

    @Override
    public String toString() {
        return "Correspondent{" +
               "userId='" + userId + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}