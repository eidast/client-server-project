package com.fidespn.view;

import com.fidespn.model.Fanatic;
import com.fidespn.model.Team;
import com.fidespn.service.MatchManager;
import com.fidespn.service.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ManageFavoriteTeamsFrame extends JFrame {
    private UserManager userManager;
    private MatchManager matchManager;
    private Fanatic currentFanatic;
    private FanaticDashboardFrame parentFrame; // Referencia al frame padre
    private JPanel teamsPanel;
    private JButton saveButton;
    private JButton cancelButton;

    public ManageFavoriteTeamsFrame(UserManager userManager, MatchManager matchManager, Fanatic currentFanatic, FanaticDashboardFrame parentFrame) {
        this.userManager = userManager;
        this.matchManager = matchManager;
        this.currentFanatic = currentFanatic;
        this.parentFrame = parentFrame;
        
        setTitle("Gestionar Equipos Favoritos - FidESPN United 2026");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        initComponents();
        loadTeams();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Header ---
        JLabel titleLabel = new JLabel("Gestionar Mis Equipos Favoritos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Instrucciones ---
        JLabel instructionsLabel = new JLabel("Selecciona los equipos que quieres agregar a tus favoritos:");
        instructionsLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        instructionsLabel.setForeground(new Color(107, 114, 128));
        instructionsLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(instructionsLabel, BorderLayout.CENTER);

        // --- Panel de equipos con scroll ---
        teamsPanel = new JPanel();
        teamsPanel.setLayout(new BoxLayout(teamsPanel, BoxLayout.Y_AXIS));
        teamsPanel.setBackground(new Color(255, 255, 255));
        teamsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JScrollPane scrollPane = new JScrollPane(teamsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Panel de botones ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(240, 242, 245));
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        cancelButton = createStyledButton("Cancelar", new Color(107, 114, 128));
        cancelButton.addActionListener(e -> {
            this.dispose();
        });

        saveButton = createStyledButton("Guardar Cambios", new Color(34, 197, 94));
        saveButton.addActionListener(e -> saveChanges());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadTeams() {
        teamsPanel.removeAll();
        List<Team> allTeams = matchManager.getAllTeams();
        List<String> currentFavorites = currentFanatic.getFavoriteTeamIds();

        for (Team team : allTeams) {
            JPanel teamPanel = createTeamPanel(team, currentFavorites.contains(team.getTeamId()));
            teamsPanel.add(teamPanel);
            teamsPanel.add(Box.createRigidArea(new Dimension(0, 8))); // Espacio entre equipos
        }

        teamsPanel.revalidate();
        teamsPanel.repaint();
    }

    private JPanel createTeamPanel(Team team, boolean isFavorite) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Checkbox para seleccionar el equipo
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(isFavorite);
        checkBox.setBackground(new Color(255, 255, 255));
        panel.add(checkBox, BorderLayout.WEST);

        // Información del equipo
        JPanel infoPanel = new JPanel(new BorderLayout(5, 0));
        infoPanel.setOpaque(false);

        JLabel teamNameLabel = new JLabel(getFlagEmoji(team.getCountry()) + " " + team.getName());
        teamNameLabel.setFont(new Font("Inter", Font.BOLD, 16));
        teamNameLabel.setForeground(new Color(55, 65, 81));
        infoPanel.add(teamNameLabel, BorderLayout.NORTH);

        JLabel countryLabel = new JLabel(team.getCountry());
        countryLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        countryLabel.setForeground(new Color(107, 114, 128));
        infoPanel.add(countryLabel, BorderLayout.SOUTH);

        panel.add(infoPanel, BorderLayout.CENTER);

        // Guardar referencia al checkbox para poder accederlo después
        panel.putClientProperty("checkbox", checkBox);
        panel.putClientProperty("teamId", team.getTeamId());

        return panel;
    }

    private void saveChanges() {
        // Limpiar lista actual de favoritos
        List<String> currentFavorites = new ArrayList<>(currentFanatic.getFavoriteTeamIds());
        for (String teamId : currentFavorites) {
            currentFanatic.removeFavoriteTeam(teamId);
        }

        // Agregar los equipos seleccionados
        Component[] components = teamsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel teamPanel = (JPanel) comp;
                JCheckBox checkBox = (JCheckBox) teamPanel.getClientProperty("checkbox");
                String teamId = (String) teamPanel.getClientProperty("teamId");
                
                if (checkBox != null && teamId != null && checkBox.isSelected()) {
                    currentFanatic.addFavoriteTeam(teamId);
                }
            }
        }

        // Guardar cambios en el UserManager
        try {
            userManager.updateUser(currentFanatic);
            
            JOptionPane.showMessageDialog(this, 
                "Tus equipos favoritos han sido actualizados correctamente.", 
                "Cambios Guardados", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (com.fidespn.service.exceptions.UserNotFoundException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al actualizar el usuario: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Actualizar la vista del dashboard padre si existe
        if (parentFrame != null) {
            parentFrame.refreshFavoriteTeams();
        }
        
        this.dispose();
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

    // Método auxiliar para obtener emojis de banderas
    private String getFlagEmoji(String country) {
        switch (country.toLowerCase()) {
            case "brasil": return "🇧🇷";
            case "argentina": return "🇦🇷";
            case "estados unidos": return "🇺🇸";
            case "méxico": return "🇲🇽";
            case "canadá": return "🇨🇦";
            case "colombia": return "🇨🇴";
            case "costa rica": return "🇨🇷";
            case "japón": return "🇯🇵";
            case "alemania": return "🇩🇪";
            case "francia": return "🇫🇷";
            case "españa": return "🇪🇸";
            case "senegal": return "🇸🇳";
            case "serbia": return "🇷🇸";
            case "qatar": return "🇶🇦";
            case "ecuador": return "🇪🇨";
            case "dinamarca": return "🇩🇰";
            case "inglaterra": return "🇬🇧";
            case "países bajos": return "🇳🇱";
            case "portugal": return "🇵🇹";
            case "italia": return "🇮🇹";
            case "bélgica": return "🇧🇪";
            default: return "🏳️";
        }
    }
} 