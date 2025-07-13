package com.fidespn.view;

import com.fidespn.model.Fanatic;
import com.fidespn.model.Match;
import com.fidespn.model.MatchEvent;
import com.fidespn.service.MatchManager;
import com.fidespn.service.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class LiveMatchFrame extends JFrame {
    private UserManager userManager;
    private MatchManager matchManager;
    private Fanatic currentFanatic;
    private Match currentMatch;
    
    private JLabel scoreLabel;
    private JTextArea eventsArea;
    private Timer updateTimer;

    public LiveMatchFrame(UserManager userManager, MatchManager matchManager, Fanatic currentFanatic, Match match) {
        this.userManager = userManager;
        this.matchManager = matchManager;
        this.currentFanatic = currentFanatic;
        this.currentMatch = match;

        setTitle("FidESPN United 2026 - Partido en Vivo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
        startLiveUpdates();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Header con información del partido ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 255, 255));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Título del partido
        String matchTitle = currentMatch.getHomeTeam().getName() + " vs " + currentMatch.getAwayTeam().getName();
        JLabel titleLabel = new JLabel(matchTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Marcador
        scoreLabel = new JLabel("0 - 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Inter", Font.BOLD, 36));
        scoreLabel.setForeground(new Color(34, 197, 94));
        headerPanel.add(scoreLabel, BorderLayout.CENTER);

        // Estado y fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy | hh:mm a");
        String statusText = currentMatch.getStatus() + " | " + sdf.format(currentMatch.getDate());
        JLabel statusLabel = new JLabel(statusText, SwingConstants.CENTER);
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(107, 114, 128));
        headerPanel.add(statusLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Panel de eventos del partido ---
        JPanel eventsPanel = new JPanel(new BorderLayout());
        eventsPanel.setBackground(new Color(255, 255, 255));
        eventsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel eventsTitle = new JLabel("Eventos del Partido", SwingConstants.LEFT);
        eventsTitle.setFont(new Font("Inter", Font.BOLD, 18));
        eventsTitle.setForeground(new Color(55, 65, 81));
        eventsTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        eventsPanel.add(eventsTitle, BorderLayout.NORTH);

        // Área de texto para eventos
        eventsArea = new JTextArea();
        eventsArea.setFont(new Font("Inter", Font.PLAIN, 14));
        eventsArea.setEditable(false);
        eventsArea.setLineWrap(true);
        eventsArea.setWrapStyleWord(true);
        eventsArea.setBackground(new Color(248, 250, 252));
        eventsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(eventsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        eventsPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(eventsPanel, BorderLayout.CENTER);

        // --- Panel de botones ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(240, 242, 245));
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton backButton = createStyledButton("Volver al Dashboard", new Color(107, 114, 128));
        backButton.addActionListener(e -> {
            this.dispose();
            new FanaticDashboardFrame(userManager, currentFanatic).setVisible(true);
        });

        JButton refreshButton = createStyledButton("Actualizar", new Color(34, 197, 94));
        refreshButton.addActionListener(e -> updateMatchInfo());

        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        // Cargar información inicial
        updateMatchInfo();
    }

    private void startLiveUpdates() {
        // Timer para actualizar la información cada 30 segundos
        updateTimer = new Timer(30000, e -> updateMatchInfo());
        updateTimer.start();
    }

    private void updateMatchInfo() {
        // Actualizar marcador
        int homeScore = currentMatch.getScoreHome();
        int awayScore = currentMatch.getScoreAway();
        scoreLabel.setText(homeScore + " - " + awayScore);

        // Actualizar eventos
        updateEvents();
    }

    private void updateEvents() {
        StringBuilder eventsText = new StringBuilder();
        List<MatchEvent> events = currentMatch.getEvents();
        
        if (events.isEmpty()) {
            eventsText.append("No hay eventos registrados aún.\n");
            eventsText.append("El partido comenzará pronto...");
        } else {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            for (MatchEvent event : events) {
                eventsText.append("[").append(timeFormat.format(event.getTimestamp())).append("] ");
                eventsText.append(event.getDescription()).append("\n");
            }
        }
        
        eventsArea.setText(eventsText.toString());
        eventsArea.setCaretPosition(0); // Ir al inicio
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    @Override
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        super.dispose();
    }
} 