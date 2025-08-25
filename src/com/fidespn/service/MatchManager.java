package com.fidespn.service;

import com.fidespn.model.Match;
import com.fidespn.model.MatchEvent;
import com.fidespn.model.Team;
import com.fidespn.model.Chat;
import com.fidespn.model.ChatMessage;
import com.fidespn.service.exceptions.MatchNotFoundException;
import com.fidespn.service.exceptions.TeamNotFoundException;

// Note: java.io.* import removed - no more .ser file operations
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MatchManager {
    private Map<String, Match> matchesById;
    private Map<String, Team> teamsById;
    private Map<String, Chat> chatsByMatchId;
    // Note: .ser files removed - persistence handled by server via Derby
    private final PropertyChangeSupport eventBus = new PropertyChangeSupport(this);

    public MatchManager() {
        this.matchesById = new HashMap<>();
        this.teamsById = new HashMap<>();
        this.chatsByMatchId = new HashMap<>();
        // Note: No more .ser file loading - data comes from server via socket adapters
        initializeDefaultTeams(); // Initialize default teams locally for demo if needed
    }

    // --- Event bus API ---
    public void addListener(PropertyChangeListener listener) {
        eventBus.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        eventBus.removePropertyChangeListener(listener);
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
                addTeam(new Team("ESP", "España", "España", "url_esp.png"));
                addTeam(new Team("SEN", "Senegal", "Senegal", "url_sen.png"));
                addTeam(new Team("SRB", "Serbia", "Serbia", "url_srb.png"));
                addTeam(new Team("QAT", "Qatar", "Qatar", "url_qat.png"));
                addTeam(new Team("ECU", "Ecuador", "Ecuador", "url_ecu.png"));
                addTeam(new Team("DEN", "Dinamarca", "Dinamarca", "url_den.png"));
                addTeam(new Team("ENG", "Inglaterra", "Inglaterra", "url_eng.png"));
                addTeam(new Team("NED", "Países Bajos", "Países Bajos", "url_ned.png"));
                addTeam(new Team("POR", "Portugal", "Portugal", "url_por.png"));
                addTeam(new Team("ITA", "Italia", "Italia", "url_ita.png"));
                addTeam(new Team("BEL", "Bélgica", "Bélgica", "url_bel.png"));
                // Note: No more .ser saving - persistence handled by server // <--- CORRECCIÓN: Llamar a saveData() para persistir los equipos por defecto
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

        // Note: No more .ser saving - persistence handled by server // Guardar datos después de crear un partido
        System.out.println("Partido creado: " + homeTeam.getName() + " vs " + awayTeam.getName());
        eventBus.firePropertyChange("matchCreated", null, newMatch);
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
        // Note: No more .ser saving - persistence handled by server
        System.out.println("Marcador actualizado para " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + ": " + homeScore + "-" + awayScore);
        eventBus.firePropertyChange("scoreUpdated", null, match);
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
        // Note: No more .ser saving - persistence handled by server
        System.out.println("Estado de partido " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + " actualizado a: " + status);
        eventBus.firePropertyChange("statusUpdated", null, match);
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
        // Note: No more .ser saving - persistence handled by server
        System.out.println("Evento agregado al partido " + matchId + ": " + type + " - " + description);
        eventBus.firePropertyChange("eventAdded", null, event);
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
        // Note: No more .ser saving - persistence handled by server
        System.out.println("Mensaje en chat de " + matchId + " de " + senderUsername + ": " + content);
        return message;
    }

    /**
     * Elimina un partido por su ID y guarda los cambios.
     */
    public void deleteMatchById(String matchId) {
        matchesById.remove(matchId);
        chatsByMatchId.remove(matchId);
        // Note: No more .ser saving - persistence handled by server
        eventBus.firePropertyChange("matchDeleted", matchId, null);
    }

    // Note: saveData() and loadData() removed - persistence now handled by server via Derby
}