package com.fidespn.service;

import com.fidespn.model.Match;
import com.fidespn.model.MatchEvent;
import com.fidespn.model.Team;
import com.fidespn.model.Player;
import com.fidespn.model.Chat;
import com.fidespn.model.ChatMessage;
import com.fidespn.service.exceptions.MatchNotFoundException;
import com.fidespn.service.exceptions.TeamNotFoundException;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MatchManager {
    private Map<String, Match> matchesById;
    private Map<String, Team> teamsById;
    private Map<String, Chat> chatsByMatchId;
    private static final String MATCHES_FILE = "matches.ser";
    private static final String TEAMS_FILE = "teams.ser";
    private static final String CHATS_FILE = "chats.ser"; // Archivo para chats

    public MatchManager() {
        this.matchesById = new HashMap<>();
        this.teamsById = new HashMap<>();
        this.chatsByMatchId = new HashMap<>();
        loadData(); // Primero cargar datos existentes
        initializeDefaultTeams(); // Luego inicializar equipos por defecto si no hay
    }

    /**
     * Inicializa algunos equipos por defecto para pruebas si no hay equipos cargados.
     */
    private void initializeDefaultTeams() {
        // Solo inicializar si no se cargaron equipos previamente
        if (teamsById.isEmpty()) {
            System.out.println("Inicializando equipos por defecto...");
            try {
                addTeam(new Team("BRA", "Brasil", "Brasil", "url_bra.png"));
                addTeam(new Team("ARG", "Argentina", "Argentina", "url_arg.png"));
                addTeam(new Team("USA", "USA", "Estados Unidos", "url_usa.png"));
                addTeam(new Team("MEX", "México", "México", "url_mex.png"));
                addTeam(new Team("CAN", "Canadá", "Canadá", "url_can.png"));
                addTeam(new Team("COL", "Colombia", "Colombia", "url_col.png"));
                addTeam(new Team("CRC", "Costa Rica", "Costa Rica", "url_crc.png"));
                addTeam(new Team("JPN", "Japón", "Japón", "url_jpn.png"));
                addTeam(new Team("GER", "Alemania", "Alemania", "url_ger.png"));
                addTeam(new Team("FRA", "Francia", "Francia", "url_fra.png"));
                saveData(); // <--- CORRECCIÓN: Llamar a saveData() para persistir los equipos por defecto
            } catch (Exception e) {
                System.err.println("Error al inicializar equipos por defecto: " + e.getMessage());
            }
        }
    }

    /**
     * Agrega un equipo al gestor.
     * @param team El objeto Team a agregar.
     */
    public void addTeam(Team team) {
        teamsById.put(team.getTeamId(), team);
        System.out.println("Equipo agregado: " + team.getName());
    }

    /**
     * Obtiene un equipo por su ID.
     * @param teamId ID del equipo.
     * @return El objeto Team.
     * @throws TeamNotFoundException Si el equipo no es encontrado.
     */
    public Team getTeamById(String teamId) throws TeamNotFoundException {
        Team team = teamsById.get(teamId);
        if (team == null) {
            throw new TeamNotFoundException("Equipo con ID " + teamId + " no encontrado.");
        }
        return team;
    }

    /**
     * Obtiene todos los equipos.
     * @return Una lista de todos los equipos.
     */
    public List<Team> getAllTeams() {
        return new ArrayList<>(teamsById.values());
    }

    /**
     * Crea un nuevo partido.
     * @param date Fecha del partido.
     * @param time Hora del partido.
     * @param homeTeamId ID del equipo local.
     * @param awayTeamId ID del equipo visitante.
     * @param correspondentId ID del corresponsal asignado.
     * @return El objeto Match creado.
     * @throws TeamNotFoundException Si alguno de los equipos no existe.
     */
    public Match createMatch(Date date, String time, String homeTeamId, String awayTeamId, String correspondentId) throws TeamNotFoundException {
        Team homeTeam = getTeamById(homeTeamId);
        Team awayTeam = getTeamById(awayTeamId);

        String matchId = UUID.randomUUID().toString();
        Match newMatch = new Match(matchId, date, time, homeTeam, awayTeam, correspondentId);
        matchesById.put(matchId, newMatch);

        // Crear un chat para el partido
        Chat newChat = new Chat(newMatch.getChatId(), matchId);
        chatsByMatchId.put(matchId, newChat);

        saveData(); // Guardar datos después de crear un partido
        System.out.println("Partido creado: " + homeTeam.getName() + " vs " + awayTeam.getName());
        return newMatch;
    }

    /**
     * Obtiene un partido por su ID.
     * @param matchId ID del partido.
     * @return El objeto Match.
     * @throws MatchNotFoundException Si el partido no es encontrado.
     */
    public Match getMatchById(String matchId) throws MatchNotFoundException {
        Match match = matchesById.get(matchId);
        if (match == null) {
            throw new MatchNotFoundException("Partido con ID " + matchId + " no encontrado.");
        }
        return match;
    }

    /**
     * Obtiene todos los partidos.
     * @return Una lista de todos los partidos.
     */
    public List<Match> getAllMatches() {
        return new ArrayList<>(matchesById.values());
    }

    /**
     * Actualiza el marcador de un partido.
     * @param matchId ID del partido.
     * @param homeScore Nuevo marcador del equipo local.
     * @param awayScore Nuevo marcador del equipo visitante.
     * @throws MatchNotFoundException Si el partido no es encontrado.
     */
    public void updateMatchScore(String matchId, int homeScore, int awayScore) throws MatchNotFoundException {
        Match match = getMatchById(matchId);
        match.setScoreHome(homeScore);
        match.setScoreAway(awayScore);
        saveData();
        System.out.println("Marcador actualizado para " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + ": " + homeScore + "-" + awayScore);
    }

    /**
     * Actualiza el estado de un partido.
     * @param matchId ID del partido.
     * @param status Nuevo estado (ej. "live", "finished").
     * @throws MatchNotFoundException Si el partido no es encontrado.
     */
    public void updateMatchStatus(String matchId, String status) throws MatchNotFoundException {
        Match match = getMatchById(matchId);
        match.setStatus(status);
        saveData();
        System.out.println("Estado de partido " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + " actualizado a: " + status);
    }

    /**
     * Agrega un evento a un partido.
     * @param matchId ID del partido.
     * @param minute Minuto del evento.
     * @param type Tipo de evento.
     * @param description Descripción del evento.
     * @return El objeto MatchEvent creado.
     * @throws MatchNotFoundException Si el partido no es encontrado.
     */
    public MatchEvent addMatchEvent(String matchId, int minute, String type, String description) throws MatchNotFoundException {
        Match match = getMatchById(matchId);
        String eventId = UUID.randomUUID().toString();
        MatchEvent event = new MatchEvent(eventId, matchId, minute, type, description);
        match.addEvent(event);
        saveData();
        System.out.println("Evento agregado al partido " + matchId + ": " + type + " - " + description);
        return event;
    }

    /**
     * Obtiene todos los eventos de un partido.
     * @param matchId ID del partido.
     * @return Lista de MatchEvent.
     * @throws MatchNotFoundException Si el partido no es encontrado.
     */
    public List<MatchEvent> getMatchEvents(String matchId) throws MatchNotFoundException {
        Match match = getMatchById(matchId);
        return new ArrayList<>(match.getEvents()); // Retorna copia
    }

    /**
     * Obtiene el chat de un partido.
     * @param matchId ID del partido.
     * @return El objeto Chat.
     * @throws MatchNotFoundException Si el partido o su chat no son encontrados.
     */
    public Chat getChatForMatch(String matchId) throws MatchNotFoundException {
        Chat chat = chatsByMatchId.get(matchId);
        if (chat == null) {
            throw new MatchNotFoundException("Chat para el partido con ID " + matchId + " no encontrado.");
        }
        return chat;
    }

    /**
     * Agrega un mensaje a un chat de partido.
     * @param matchId ID del partido.
     * @param senderId ID del remitente.
     * @param senderUsername Nombre de usuario del remitente.
     * @param content Contenido del mensaje.
     * @return El objeto ChatMessage creado.
     * @throws MatchNotFoundException Si el partido o su chat no son encontrados.
     */
    public ChatMessage addChatMessage(String matchId, String senderId, String senderUsername, String content) throws MatchNotFoundException {
        Chat chat = getChatForMatch(matchId);
        String messageId = UUID.randomUUID().toString();
        ChatMessage message = new ChatMessage(messageId, chat.getChatId(), senderId, senderUsername, content);
        chat.addMessage(message);
        saveData();
        System.out.println("Mensaje en chat de " + matchId + " de " + senderUsername + ": " + content);
        return message;
    }

    // --- Métodos para Serialización (Persistencia) ---
    private void saveData() {
        try (ObjectOutputStream oosMatches = new ObjectOutputStream(new FileOutputStream(MATCHES_FILE));
             ObjectOutputStream oosTeams = new ObjectOutputStream(new FileOutputStream(TEAMS_FILE));
             ObjectOutputStream oosChats = new ObjectOutputStream(new FileOutputStream(CHATS_FILE))) {
            oosMatches.writeObject(new ArrayList<>(matchesById.values()));
            oosTeams.writeObject(new ArrayList<>(teamsById.values()));
            oosChats.writeObject(new ArrayList<>(chatsByMatchId.values()));

            System.out.println("Datos de partidos, equipos y chats guardados.");
        } catch (IOException e) {
            System.err.println("Error al guardar datos de partidos/equipos/chats: " + e.getMessage());
        }
    }

    // Método privado para guardar solo equipos (si fuera necesario, pero saveData ya lo hace)
    // Se mantiene por si se desea una granularidad de guardado específica en el futuro,
    // pero por ahora, saveData es suficiente.
    private void saveTeams() {
        try (ObjectOutputStream oosTeams = new ObjectOutputStream(new FileOutputStream(TEAMS_FILE))) {
            oosTeams.writeObject(new ArrayList<>(teamsById.values()));
            System.out.println("Equipos guardados en " + TEAMS_FILE);
        } catch (IOException e) {
            System.err.println("Error al guardar equipos: " + e.getMessage());
        }
    }


    private void loadData() {
        File matchesFile = new File(MATCHES_FILE);
        File teamsFile = new File(TEAMS_FILE);
        File chatsFile = new File(CHATS_FILE);

        if (matchesFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(matchesFile))) {
                List<Match> loadedMatches = (List<Match>) ois.readObject();
                this.matchesById.clear();
                for (Match match : loadedMatches) {
                    this.matchesById.put(match.getMatchId(), match);
                }
                System.out.println("Partidos cargados desde " + MATCHES_FILE);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar partidos: " + e.getMessage());
            }
        }

        if (teamsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(teamsFile))) {
                List<Team> loadedTeams = (List<Team>) ois.readObject();
                this.teamsById.clear();
                for (Team team : loadedTeams) {
                    this.teamsById.put(team.getTeamId(), team);
                }
                System.out.println("Equipos cargados desde " + TEAMS_FILE);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar equipos: " + e.getMessage());
            }
        }

        if (chatsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(chatsFile))) {
                List<Chat> loadedChats = (List<Chat>) ois.readObject();
                this.chatsByMatchId.clear();
                for (Chat chat : loadedChats) {
                    this.chatsByMatchId.put(chat.getMatchId(), chat);
                }
                System.out.println("Chats cargados desde " + CHATS_FILE);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar chats: " + e.getMessage());
            }
        }
    }
}