package com.fidespn.view;

import com.fidespn.model.Match;
import com.fidespn.model.User;
import com.fidespn.model.Correspondent;
import com.fidespn.service.MatchManager;
import com.fidespn.service.UserManager;
import com.fidespn.service.exceptions.MatchNotFoundException;
import com.fidespn.service.exceptions.UserNotFoundException;

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

    public CorrespondentDashboardFrame(UserManager userManager, User currentUser) {
        this.userManager = userManager;
        this.matchManager = new MatchManager();
        this.currentUser = currentUser;

        setTitle("FidESPN United 2026 - Panel de Corresponsal");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
        loadAssignedMatches();
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

        // Panel principal de contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);

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

        // Botones de acción
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        JButton viewMatchBtn = createStyledButton("Ver Detalles", new Color(37, 99, 235));
        JButton editReportBtn = createStyledButton("Editar Reporte", new Color(234, 179, 8));
        JButton submitReportBtn = createStyledButton("Enviar Reporte", new Color(34, 197, 94));
        buttonsPanel.add(viewMatchBtn);
        buttonsPanel.add(editReportBtn);
        buttonsPanel.add(submitReportBtn);
        matchesPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Tabla de partidos asignados
        String[] columnNames = {"Partido", "Fecha y Hora", "Estado", "Progreso del Reporte"};
        assignedMatchesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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

        contentPanel.add(matchesPanel, BorderLayout.CENTER);

        // Panel de estadísticas rápidas
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);

        JPanel totalMatchesCard = createStatCard("Total de Partidos", "0", new Color(37, 99, 235));
        JPanel completedReportsCard = createStatCard("Reportes Completados", "0", new Color(34, 197, 94));
        JPanel pendingReportsCard = createStatCard("Reportes Pendientes", "0", new Color(234, 179, 8));

        statsPanel.add(totalMatchesCard);
        statsPanel.add(completedReportsCard);
        statsPanel.add(pendingReportsCard);

        contentPanel.add(statsPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

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

        add(mainPanel);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(255, 255, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
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
            if (match.getCorrespondentId() == currentUser.getUserId()) {
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

    private void updateStatistics(int total, int completed, int pending) {
        // En una implementación real, actualizaría los labels de las tarjetas de estadísticas
        System.out.println("Estadísticas actualizadas - Total: " + total + ", Completados: " + completed + ", Pendientes: " + pending);
    }
}