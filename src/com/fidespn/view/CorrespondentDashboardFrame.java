package com.fidespn.view;

import com.fidespn.model.Match;
import com.fidespn.model.User;
import com.fidespn.model.Correspondent;
import com.fidespn.service.MatchManager;
import com.fidespn.service.UserManager;
import com.fidespn.service.exceptions.MatchNotFoundException;
import com.fidespn.service.exceptions.UserNotFoundException;
import com.fidespn.model.MatchEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class CorrespondentDashboardFrame extends JFrame {
    private UserManager userManager;
    private MatchManager matchManager;
    private User currentUser;

    private DefaultTableModel assignedMatchesTableModel;
    private JTable assignedMatchesTable;
    private JTable eventsTable;
    private DefaultTableModel eventsTableModel;
    private Match selectedMatch;
    private MatchEvent selectedEvent;

    private JComboBox<String> matchCombo;
    private JSpinner minuteSpinner;
    private JComboBox<String> typeCombo;
    private JTextArea descArea;

    // Referencias directas a los labels de estadísticas
    private JLabel totalMatchesLabel;
    private JLabel completedReportsLabel;
    private JLabel pendingReportsLabel;

    public CorrespondentDashboardFrame(UserManager userManager, MatchManager matchManager, User currentUser) {
        this.userManager = userManager;
        this.matchManager = matchManager;
        this.currentUser = currentUser;

        setTitle("FidESPN United 2026 - Panel de Corresponsal");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
        loadAssignedMatches();
        loadMatchCombo();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de información del usuario
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BorderLayout());
        userInfoPanel.setBackground(new Color(255, 255, 255));
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel welcomeLabel = new JLabel("Bienvenido, " + currentUser.getUsername(), SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(52, 73, 94));
        userInfoPanel.add(welcomeLabel, BorderLayout.WEST);

        JLabel roleLabel = new JLabel("Corresponsal", SwingConstants.RIGHT);
        roleLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        roleLabel.setForeground(new Color(108, 122, 137));
        userInfoPanel.add(roleLabel, BorderLayout.EAST);

        mainPanel.add(userInfoPanel, BorderLayout.NORTH);

        // Panel principal de contenido (vertical)
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.setOpaque(false);

        // Panel de partidos asignados
        JPanel matchesPanel = new JPanel();
        matchesPanel.setLayout(new BorderLayout(10, 10));
        matchesPanel.setBackground(new Color(255, 255, 255));
        matchesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel matchesTitle = new JLabel("Mis Partidos Asignados", SwingConstants.LEFT);
        matchesTitle.setFont(new Font("Inter", Font.BOLD, 22));
        matchesTitle.setForeground(new Color(55, 65, 81));
        matchesTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        matchesPanel.add(matchesTitle, BorderLayout.NORTH);
        String[] columnNames = {"Partido", "Fecha y Hora", "Estado", "Progreso del Reporte"};
        assignedMatchesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        assignedMatchesTable = new JTable(assignedMatchesTableModel);
        assignedMatchesTable.setFont(new Font("Inter", Font.PLAIN, 14));
        assignedMatchesTable.setRowHeight(30);
        assignedMatchesTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        assignedMatchesTable.getTableHeader().setBackground(new Color(229, 231, 235));
        assignedMatchesTable.getTableHeader().setForeground(new Color(55, 65, 81));
        assignedMatchesTable.setGridColor(new Color(229, 231, 235));
        assignedMatchesTable.setSelectionBackground(new Color(209, 213, 219));
        JScrollPane matchesScrollPane = new JScrollPane(assignedMatchesTable);
        matchesScrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        matchesPanel.add(matchesScrollPane, BorderLayout.CENTER);
        verticalPanel.add(matchesPanel);
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Tabla de eventos del partido seleccionado
        String[] eventColumns = {"Minuto", "Tipo", "Descripción"};
        eventsTableModel = new DefaultTableModel(eventColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        eventsTable = new JTable(eventsTableModel);
        eventsTable.setFont(new Font("Inter", Font.PLAIN, 13));
        eventsTable.setRowHeight(24);
        eventsTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        JScrollPane eventsScrollPane = new JScrollPane(eventsTable);
        eventsScrollPane.setPreferredSize(new Dimension(400, 120));
        JPanel eventsPanel = new JPanel(new BorderLayout());
        eventsPanel.setBackground(new Color(255,255,255));
        eventsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel eventsTitle = new JLabel("Eventos del Partido", SwingConstants.LEFT);
        eventsTitle.setFont(new Font("Inter", Font.BOLD, 18));
        eventsTitle.setForeground(new Color(55, 65, 81));
        eventsPanel.add(eventsTitle, BorderLayout.NORTH);
        eventsPanel.add(eventsScrollPane, BorderLayout.CENTER);
        verticalPanel.add(eventsPanel);
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel de formulario de eventos
        JPanel eventFormPanel = new JPanel();
        eventFormPanel.setLayout(new GridBagLayout());
        eventFormPanel.setBackground(new Color(255, 255, 255));
        eventFormPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        eventFormPanel.add(new JLabel("Partido:"), gbc);
        matchCombo = new JComboBox<>();
        gbc.gridx = 1;
        eventFormPanel.add(matchCombo, gbc);
        gbc.gridx = 2;
        eventFormPanel.add(new JLabel("Minuto:"), gbc);
        minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 130, 1));
        gbc.gridx = 3;
        eventFormPanel.add(minuteSpinner, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        eventFormPanel.add(new JLabel("Tipo de Evento:"), gbc);
        typeCombo = new JComboBox<>(new String[]{"Gol", "Tarjeta Amarilla", "Tarjeta Roja", "Sustitución", "Otro"});
        gbc.gridx = 1;
        eventFormPanel.add(typeCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        eventFormPanel.add(new JLabel("Descripción:"), gbc);
        descArea = new JTextArea(3, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        gbc.gridx = 1; gbc.gridwidth = 3;
        eventFormPanel.add(descScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        JPanel formBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton enviarBtn = createStyledButton("Enviar Reporte", new Color(34, 197, 94));
        JButton editarBtn = createStyledButton("Editar Reporte", new Color(234, 179, 8));
        formBtnPanel.add(enviarBtn);
        formBtnPanel.add(editarBtn);
        eventFormPanel.add(formBtnPanel, gbc);
        verticalPanel.add(eventFormPanel);
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel de estadísticas rápidas
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        totalMatchesLabel = new JLabel("0", SwingConstants.CENTER);
        completedReportsLabel = new JLabel("0", SwingConstants.CENTER);
        pendingReportsLabel = new JLabel("0", SwingConstants.CENTER);
        JPanel totalMatchesCard = createStatCard("Total de Partidos", totalMatchesLabel, new Color(37, 99, 235));
        JPanel completedReportsCard = createStatCard("Reportes Completados", completedReportsLabel, new Color(34, 197, 94));
        JPanel pendingReportsCard = createStatCard("Reportes Pendientes", pendingReportsLabel, new Color(234, 179, 8));
        statsPanel.add(totalMatchesCard);
        statsPanel.add(completedReportsCard);
        statsPanel.add(pendingReportsCard);
        verticalPanel.add(statsPanel);

        mainPanel.add(verticalPanel, BorderLayout.CENTER);

        // Panel de pie de página
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(240, 242, 245));
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton logoutButton = createStyledButton("Cerrar Sesión", new Color(107, 114, 128));
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginFrame(userManager).setVisible(true);
        });
        footerPanel.add(logoutButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);

        // Poblar el combo de partidos asignados
        loadMatchCombo();
        // Listener para seleccionar partido desde el combo
        matchCombo.addActionListener(e -> {
            int idx = matchCombo.getSelectedIndex();
            if (idx != -1) {
                String matchName = (String) matchCombo.getSelectedItem();
                for (Match m : matchManager.getAllMatches()) {
                    String desc = m.getHomeTeam().getName() + " vs " + m.getAwayTeam().getName();
                    if (desc.equals(matchName) && m.getCorrespondentId() != null && m.getCorrespondentId().equals(currentUser.getUserId())) {
                        selectedMatch = m;
                        loadEventsForSelectedMatch();
                        break;
                    }
                }
            } else {
                selectedMatch = null;
                eventsTableModel.setRowCount(0);
            }
        });

        // Listener para Enviar Reporte
        enviarBtn.addActionListener(e -> {
            if (selectedMatch == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un partido para reportar evento.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int minute = (Integer) minuteSpinner.getValue();
                String type = (String) typeCombo.getSelectedItem();
                String desc = descArea.getText().trim();
                if (desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                matchManager.addMatchEvent(selectedMatch.getMatchId(), minute, type, desc);
                loadEventsForSelectedMatch();
                descArea.setText("");
                minuteSpinner.setValue(0);
                JOptionPane.showMessageDialog(this, "Evento reportado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al reportar evento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Listener para Editar Reporte
        editarBtn.addActionListener(e -> {
            if (selectedMatch == null || selectedEvent == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un evento para editar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int newMinute = (Integer) minuteSpinner.getValue();
                String newType = (String) typeCombo.getSelectedItem();
                String newDesc = descArea.getText().trim();
                if (newDesc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                MatchEvent updatedEvent = new MatchEvent(selectedEvent.getEventId(), selectedEvent.getMatchId(), newMinute, newType, newDesc);
                for (int i = 0; i < selectedMatch.getEvents().size(); i++) {
                    if (selectedMatch.getEvents().get(i).getEventId().equals(selectedEvent.getEventId())) {
                        selectedMatch.getEvents().set(i, updatedEvent);
                        break;
                    }
                }
                java.lang.reflect.Method saveData = matchManager.getClass().getDeclaredMethod("saveData");
                saveData.setAccessible(true);
                saveData.invoke(matchManager);
                loadEventsForSelectedMatch();
                descArea.setText("");
                minuteSpinner.setValue(0);
                JOptionPane.showMessageDialog(this, "Evento editado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al editar evento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Al seleccionar un evento, llenar el formulario
        eventsTable.getSelectionModel().addListSelectionListener(e -> {
            int row = eventsTable.getSelectedRow();
            if (row != -1 && selectedMatch != null) {
                int minute = Integer.parseInt(eventsTableModel.getValueAt(row, 0).toString());
                String type = (String) eventsTableModel.getValueAt(row, 1);
                String desc = (String) eventsTableModel.getValueAt(row, 2);
                for (MatchEvent ev : selectedMatch.getEvents()) {
                    if (ev.getMinute() == minute && ev.getType().equals(type) && ev.getDescription().equals(desc)) {
                        selectedEvent = ev;
                        minuteSpinner.setValue(ev.getMinute());
                        typeCombo.setSelectedItem(ev.getType());
                        descArea.setText(ev.getDescription());
                        break;
                    }
                }
            }
        });

        // Listener para seleccionar evento a editar
        eventsTable.getSelectionModel().addListSelectionListener(e -> {
            int row = eventsTable.getSelectedRow();
            if (row != -1 && selectedMatch != null) {
                int minute = Integer.parseInt(eventsTableModel.getValueAt(row, 0).toString());
                String type = (String) eventsTableModel.getValueAt(row, 1);
                String desc = (String) eventsTableModel.getValueAt(row, 2);
                for (MatchEvent ev : selectedMatch.getEvents()) {
                    if (ev.getMinute() == minute && ev.getType().equals(type) && ev.getDescription().equals(desc)) {
                        selectedEvent = ev;
                        break;
                    }
                }
            }
        });
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(255, 255, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        valueLabel.setFont(new Font("Inter", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 122, 137));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(titleLabel);
        return card;
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

    private void loadAssignedMatches() {
        assignedMatchesTableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy | hh:mm a");
        
        List<Match> allMatches = matchManager.getAllMatches();
        int totalMatches = 0;
        int completedReports = 0;
        int pendingReports = 0;

        for (Match match : allMatches) {
            if (match.getCorrespondentId() != null && match.getCorrespondentId().equals(currentUser.getUserId())) {
                totalMatches++;
                
                String matchName = match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName();
                String dateTime = sdf.format(match.getDate()) + " (CR)";
                String status = "Programado";
                String reportProgress = "Pendiente";

                // Simular progreso del reporte (en una implementación real, esto vendría de la base de datos)
                if (match.getDate().before(new java.util.Date())) {
                    status = "En Curso";
                    if (Math.random() > 0.5) {
                        reportProgress = "Completado";
                        completedReports++;
                    } else {
                        reportProgress = "En Progreso";
                        pendingReports++;
                    }
                } else {
                    pendingReports++;
                }

                assignedMatchesTableModel.addRow(new Object[]{matchName, dateTime, status, reportProgress});
            }
        }

        // Actualizar estadísticas
        updateStatistics(totalMatches, completedReports, pendingReports);
    }

    private void loadEventsForSelectedMatch() {
        eventsTableModel.setRowCount(0);
        if (selectedMatch != null) {
            for (MatchEvent ev : selectedMatch.getEvents()) {
                eventsTableModel.addRow(new Object[]{ev.getMinute(), ev.getType(), ev.getDescription()});
            }
        }
    }

    // Nuevo método para poblar el combo de partidos asignados
    private void loadMatchCombo() {
        matchCombo.removeAllItems();
        boolean found = false;
        for (Match m : matchManager.getAllMatches()) {
            if (m.getCorrespondentId() != null && m.getCorrespondentId().equals(currentUser.getUserId())) {
                String desc = m.getHomeTeam().getName() + " vs " + m.getAwayTeam().getName();
                matchCombo.addItem(desc);
                found = true;
            }
        }
        if (!found) {
            selectedMatch = null;
            eventsTableModel.setRowCount(0);
        }
    }

    // Mejora: Actualiza visualmente las tarjetas de estadísticas
    private void updateStatistics(int total, int completed, int pending) {
        if (totalMatchesLabel != null) totalMatchesLabel.setText(String.valueOf(total));
        if (completedReportsLabel != null) completedReportsLabel.setText(String.valueOf(completed));
        if (pendingReportsLabel != null) pendingReportsLabel.setText(String.valueOf(pending));
    }
}