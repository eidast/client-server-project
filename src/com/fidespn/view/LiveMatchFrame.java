package com.fidespn.view;

import com.fidespn.model.Fanatic;
import com.fidespn.model.Match;
import com.fidespn.model.MatchEvent;
import com.fidespn.service.MatchManager;
import com.fidespn.service.UserManager;
import com.fidespn.service.StatisticsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LiveMatchFrame extends JFrame implements PropertyChangeListener {
    private UserManager userManager;
    private MatchManager matchManager;
    private Fanatic currentFanatic;
    private Match currentMatch;
    
    private JLabel scoreLabel;
    private JTextArea eventsArea;
    // Removed polling timer in favor of event-driven updates
    private JLabel statsLabel;

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
        // Subscribe to realtime updates
        this.matchManager.addListener(this);
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

        // Stats summary
        statsLabel = new JLabel(" ", SwingConstants.LEFT);
        statsLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        statsLabel.setForeground(new Color(55, 65, 81));
        statsLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        eventsPanel.add(statsLabel, BorderLayout.SOUTH);

        mainPanel.add(eventsPanel, BorderLayout.CENTER);

        // --- Panel de botones ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(240, 242, 245));
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton backButton = createStyledButton("Volver al Dashboard", new Color(107, 114, 128));
        backButton.addActionListener(e -> {
            this.dispose();
            new FanaticDashboardFrame(userManager, matchManager, currentFanatic).setVisible(true);
        });

        JButton refreshButton = createStyledButton("Actualizar", new Color(34, 197, 94));
        refreshButton.addActionListener(e -> updateMatchInfo());

        JButton exportPdfBtn = createStyledButton("Exportar PDF", new Color(37, 99, 235));
        exportPdfBtn.addActionListener(e -> {
            try {
                javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                chooser.setSelectedFile(new java.io.File("reporte-" + currentMatch.getHomeTeam().getName() + "-" + currentMatch.getAwayTeam().getName() + ".pdf"));
                int result = chooser.showSaveDialog(this);
                if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                    java.io.File file = chooser.getSelectedFile();
                    new com.fidespn.service.ReportService().exportMatchReport(currentMatch, file);
                    JOptionPane.showMessageDialog(this, "PDF exportado en: " + file.getAbsolutePath(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportPdfBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        // Cargar información inicial
        updateMatchInfo();
    }

    // Eliminated polling; updates will be event-driven

    private void updateMatchInfo() {
        // Actualizar marcador
        int homeScore = currentMatch.getScoreHome();
        int awayScore = currentMatch.getScoreAway();
        scoreLabel.setText(homeScore + " - " + awayScore);

        // Actualizar eventos
        updateEvents();
        updateStats();
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

    private void updateStats() {
        StatisticsService service = new StatisticsService();
        StatisticsService.MatchStats stats = service.computeMatchStats(currentMatch);
        statsLabel.setText("Eventos: " + stats.totalEvents +
                " | Goles: " + stats.goals +
                " | Amarillas: " + stats.yellowCards +
                " | Rojas: " + stats.redCards +
                " | Cambios: " + stats.substitutions);
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
        // Unsubscribe from events
        this.matchManager.removeListener(this);
        super.dispose();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if (name == null) return;
        switch (name) {
            case "scoreUpdated":
            case "statusUpdated":
                if (evt.getNewValue() instanceof Match) {
                    Match m = (Match) evt.getNewValue();
                    if (m.getMatchId().equals(currentMatch.getMatchId())) {
                        this.currentMatch = m;
                        SwingUtilities.invokeLater(this::updateMatchInfo);
                        try {
                            TrayNotifier.show("Marcador actualizado", m.getHomeTeam().getName() + " " + m.getScoreHome() + " - " + m.getScoreAway() + " " + m.getAwayTeam().getName());
                        } catch (Exception ignored) {}
                    }
                }
                break;
            case "eventAdded":
                if (evt.getNewValue() instanceof MatchEvent) {
                    MatchEvent me = (MatchEvent) evt.getNewValue();
                    if (me.getMatchId().equals(currentMatch.getMatchId())) {
                        SwingUtilities.invokeLater(this::updateEvents);
                        try {
                            TrayNotifier.show("Nuevo evento", me.getType() + ": " + me.getDescription());
                        } catch (Exception ignored) {}
                    }
                }
                break;
            default:
                break;
        }
    }
} 