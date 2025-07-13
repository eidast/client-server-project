package com.fidespn.view;

import com.fidespn.model.Match;
import com.fidespn.model.User;
import com.fidespn.model.Administrator; // <--- AGREGADO
import com.fidespn.model.Correspondent; // <--- AGREGADO
import com.fidespn.model.Fanatic;       // <--- AGREGADO
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

public class AdminDashboardFrame extends JFrame {
    private UserManager userManager;
    private MatchManager matchManager;

    private DefaultTableModel matchesTableModel;
    private JTable matchesTable;
    private DefaultTableModel usersTableModel;
    private JTable usersTable;

    public AdminDashboardFrame(UserManager userManager) {
        this.userManager = userManager;
        this.matchManager = new MatchManager();

        setTitle("FidESPN United 2026 - Panel de Administrador");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
        loadDataIntoTables();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Panel de Administrador", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JPanel matchManagementPanel = new JPanel();
        matchManagementPanel.setLayout(new BorderLayout(10, 10));
        matchManagementPanel.setBackground(new Color(255, 255, 255));
        matchManagementPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        matchManagementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        matchManagementPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        JLabel matchTitle = new JLabel("Gestión de Partidos", SwingConstants.LEFT);
        matchTitle.setFont(new Font("Inter", Font.BOLD, 22));
        matchTitle.setForeground(new Color(55, 65, 81));
        matchTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        matchManagementPanel.add(matchTitle, BorderLayout.NORTH);

        JPanel matchButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        matchButtonsPanel.setOpaque(false);
        JButton createMatchBtn = createStyledButton("Crear Nuevo Partido", new Color(34, 197, 94));
        JButton editMatchBtn = createStyledButton("Editar Partido", new Color(234, 179, 8));
        JButton deleteMatchBtn = createStyledButton("Eliminar Partido", new Color(239, 68, 68));
        matchButtonsPanel.add(createMatchBtn);
        matchButtonsPanel.add(editMatchBtn);
        matchButtonsPanel.add(deleteMatchBtn);
        matchManagementPanel.add(matchButtonsPanel, BorderLayout.SOUTH);

        String[] matchColumnNames = {"Partido", "Fecha y Hora", "Corresponsal", "Estado", "Acciones"};
        matchesTableModel = new DefaultTableModel(matchColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        matchesTable = new JTable(matchesTableModel);
        matchesTable.setFont(new Font("Inter", Font.PLAIN, 14));
        matchesTable.setRowHeight(25);
        matchesTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        matchesTable.getTableHeader().setBackground(new Color(229, 231, 235));
        matchesTable.getTableHeader().setForeground(new Color(55, 65, 81));
        matchesTable.setGridColor(new Color(229, 231, 235));
        matchesTable.setSelectionBackground(new Color(209, 213, 219));
        
        JScrollPane matchScrollPane = new JScrollPane(matchesTable);
        matchScrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        matchManagementPanel.add(matchScrollPane, BorderLayout.CENTER);

        contentPanel.add(matchManagementPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel userManagementPanel = new JPanel();
        userManagementPanel.setLayout(new BorderLayout(10, 10));
        userManagementPanel.setBackground(new Color(255, 255, 255));
        userManagementPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        userManagementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userManagementPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        JLabel userTitle = new JLabel("Gestión de Usuarios", SwingConstants.LEFT);
        userTitle.setFont(new Font("Inter", Font.BOLD, 22));
        userTitle.setForeground(new Color(55, 65, 81));
        userTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        userManagementPanel.add(userTitle, BorderLayout.NORTH);

        JPanel userButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userButtonsPanel.setOpaque(false);
        JButton createUserBtn = createStyledButton("Crear Nuevo Usuario", new Color(34, 197, 94));
        JButton editUserBtn = createStyledButton("Editar Usuario", new Color(234, 179, 8));
        JButton deleteUserBtn = createStyledButton("Eliminar Usuario", new Color(239, 68, 68));
        userButtonsPanel.add(createUserBtn);
        userButtonsPanel.add(editUserBtn);
        userButtonsPanel.add(deleteUserBtn);
        userManagementPanel.add(userButtonsPanel, BorderLayout.SOUTH);

        String[] userColumnNames = {"Usuario", "Rol", "Email", "Acciones"};
        usersTableModel = new DefaultTableModel(userColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.setFont(new Font("Inter", Font.PLAIN, 14));
        usersTable.setRowHeight(25);
        usersTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        usersTable.getTableHeader().setBackground(new Color(229, 231, 235));
        usersTable.getTableHeader().setForeground(new Color(55, 65, 81));
        usersTable.setGridColor(new Color(229, 231, 235));
        usersTable.setSelectionBackground(new Color(209, 213, 219));

        JScrollPane userScrollPane = new JScrollPane(usersTable);
        userScrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        userManagementPanel.add(userScrollPane, BorderLayout.CENTER);

        contentPanel.add(userManagementPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

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

    private void loadDataIntoTables() {
        matchesTableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy | hh:mm a");
        List<Match> matches = matchManager.getAllMatches();
        for (Match match : matches) {
            String matchName = match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName();
            String dateTime = sdf.format(match.getDate()) + " (CR)";
            String correspondentName = "N/A";
            try {
                User corr = userManager.getUserById(match.getCorrespondentId());
                correspondentName = corr.getUsername();
            } catch (UserNotFoundException e) {
                System.err.println("Corresponsal no encontrado para el partido " + match.getMatchId() + ": " + e.getMessage());
            }
            String status = match.getStatus().substring(0, 1).toUpperCase() + match.getStatus().substring(1);
            matchesTableModel.addRow(new Object[]{matchName, dateTime, correspondentName, status, ""});
        }

        usersTableModel.setRowCount(0);
        List<User> users = userManager.getAllUsers();
        for (User user : users) {
            String role = "Desconocido";
            // Aquí es donde se necesitaban los imports
            if (user instanceof Administrator) {
                role = "Administrador";
            } else if (user instanceof Correspondent) {
                role = "Corresponsal";
            } else if (user instanceof Fanatic) {
                role = "Fanático";
            }
            usersTableModel.addRow(new Object[]{user.getUsername(), role, user.getEmail(), ""});
        }
    }
}