package com.fidespn.view;

import com.fidespn.model.Fanatic; // Importar la clase Fanatic
import com.fidespn.model.Match;
import com.fidespn.model.Team;
import com.fidespn.model.User;
import com.fidespn.service.MatchManager;
import com.fidespn.service.UserManager;
import com.fidespn.service.exceptions.TeamNotFoundException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList; // Necesario para crear nuevas listas temporales

public class FanaticDashboardFrame extends JFrame {
    private UserManager userManager;
    private MatchManager matchManager;
    private Fanatic currentFanatic; // El fanático actualmente logueado

    private JPanel favoriteTeamsPanel; // Panel para mostrar los equipos favoritos
    private DefaultTableModel matchesTableModel;
    private JTable matchesTable;

    public FanaticDashboardFrame(UserManager userManager, User currentUser) {
        this.userManager = userManager;
        this.matchManager = new MatchManager(); // Instancia del gestor de partidos
        // Es seguro castear aquí porque LoginFrame ya verifica que sea una instancia de Fanatic
        this.currentFanatic = (Fanatic) currentUser; 

        setTitle("FidESPN United 2026 - Panel de Fanático");
        setSize(900, 700); // Tamaño adecuado para el contenido
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
        loadFavoriteTeams(); // Cargar equipos favoritos
        loadFeaturedMatches(); // Cargar partidos destacados
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245)); // bg-gray-100
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding general

        // --- Header (Título del Panel) ---
        JLabel titleLabel = new JLabel("Panel de Fanático", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setForeground(new Color(52, 73, 94)); // text-gray-800
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0)); // Margen inferior
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Contenido Central (Equipos Favoritos y Partidos) ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Apilar verticalmente
        contentPanel.setOpaque(false);

        // --- Sección de Mis Equipos Favoritos ---
        JPanel favoriteTeamsSectionPanel = new JPanel();
        favoriteTeamsSectionPanel.setLayout(new BorderLayout(10, 10));
        favoriteTeamsSectionPanel.setBackground(new Color(255, 255, 255)); // bg-white
        favoriteTeamsSectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)), // border-gray-200
                BorderFactory.createEmptyBorder(20, 20, 20, 20) // p-6
        ));
        favoriteTeamsSectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        favoriteTeamsSectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180)); // Altura máxima

        JLabel favoriteTeamsTitle = new JLabel("Mis Equipos Favoritos", SwingConstants.LEFT);
        favoriteTeamsTitle.setFont(new Font("Inter", Font.BOLD, 22)); // text-xl font-semibold
        favoriteTeamsTitle.setForeground(new Color(55, 65, 81)); // text-gray-700
        favoriteTeamsTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        favoriteTeamsSectionPanel.add(favoriteTeamsTitle, BorderLayout.NORTH);

        // Panel para las "etiquetas" de los equipos favoritos
        favoriteTeamsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Espaciado entre tags
        favoriteTeamsPanel.setOpaque(false); // Para que el color de fondo del padre se vea
        favoriteTeamsSectionPanel.add(favoriteTeamsPanel, BorderLayout.CENTER);

        // Botón "Gestionar"
        JPanel manageButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        manageButtonPanel.setOpaque(false);
        JButton manageTeamsBtn = createStyledButton("Gestionar", new Color(156, 163, 175)); // bg-gray-200, text-gray-700
        manageTeamsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Funcionalidad de gestión de equipos favoritos no implementada aún.", "Gestionar Equipos", JOptionPane.INFORMATION_MESSAGE);
        });
        manageButtonPanel.add(manageTeamsBtn);
        favoriteTeamsSectionPanel.add(manageButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(favoriteTeamsSectionPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio entre secciones

        // --- Sección de Partidos Destacados / Próximos ---
        JPanel featuredMatchesPanel = new JPanel();
        featuredMatchesPanel.setLayout(new BorderLayout(10, 10));
        featuredMatchesPanel.setBackground(new Color(255, 255, 255)); // bg-white
        featuredMatchesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)), // border-gray-200
                BorderFactory.createEmptyBorder(20, 20, 20, 20) // p-6
        ));
        featuredMatchesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        featuredMatchesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400)); // Altura máxima

        JLabel featuredMatchesTitle = new JLabel("Partidos Destacados / Próximos", SwingConstants.LEFT);
        featuredMatchesTitle.setFont(new Font("Inter", Font.BOLD, 22));
        featuredMatchesTitle.setForeground(new Color(55, 65, 81));
        featuredMatchesTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        featuredMatchesPanel.add(featuredMatchesTitle, BorderLayout.NORTH);

        // Tabla de Partidos
        String[] matchesColumnNames = {"Partido", "Fecha y Hora", "Estado"}; // Simplificado para esta vista
        matchesTableModel = new DefaultTableModel(matchesColumnNames, 0) {
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
        
        JScrollPane matchesScrollPane = new JScrollPane(matchesTable);
        matchesScrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        featuredMatchesPanel.add(matchesScrollPane, BorderLayout.CENTER);

        // Botón "Ver Detalles" para el partido seleccionado
        JPanel viewDetailsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        viewDetailsPanel.setOpaque(false);
        JButton viewDetailsBtn = createStyledButton("Ver Detalles", new Color(34, 197, 94)); // bg-green-500
        viewDetailsBtn.addActionListener(e -> {
            int selectedRow = matchesTable.getSelectedRow();
            if (selectedRow != -1) {
                // Obtener el ID del partido de la fila seleccionada (asumiendo que el primer elemento es el nombre)
                // En un sistema real, guardaríamos el ID del partido en una columna oculta o en el modelo.
                // Por ahora, para el prototipo, podemos obtener el nombre y buscar el partido.
                String matchName = (String) matchesTableModel.getValueAt(selectedRow, 0);
                Match selectedMatch = findMatchByName(matchName); // Método auxiliar para encontrar el partido

                if (selectedMatch != null) {
                    // Abrir la pantalla de Partido en Vivo
                    new LiveMatchFrame(userManager, matchManager, currentFanatic, selectedMatch).setVisible(true);
                    this.dispose(); // Cerrar el dashboard del fanático
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo encontrar el partido seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un partido de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        viewDetailsPanel.add(viewDetailsBtn);
        featuredMatchesPanel.add(viewDetailsPanel, BorderLayout.SOUTH);


        contentPanel.add(featuredMatchesPanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // --- Footer (Botón de Cerrar Sesión) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(240, 242, 245));
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton logoutButton = createStyledButton("Cerrar Sesión", new Color(107, 114, 128)); // bg-gray-500
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginFrame(userManager).setVisible(true);
        });
        footerPanel.add(logoutButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Método auxiliar para crear botones con estilo
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // py-2 px-4
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    // Método para cargar los equipos favoritos del fanático
    private void loadFavoriteTeams() {
        favoriteTeamsPanel.removeAll(); // Limpiar panel antes de añadir

        List<String> favoriteTeamIds = currentFanatic.getFavoriteTeamIds();
        if (favoriteTeamIds.isEmpty()) {
            JLabel noTeamsLabel = new JLabel("No has añadido equipos favoritos aún.");
            noTeamsLabel.setFont(new Font("Inter", Font.PLAIN, 14));
            noTeamsLabel.setForeground(new Color(108, 122, 137));
            favoriteTeamsPanel.add(noTeamsLabel);
        } else {
            for (String teamId : favoriteTeamIds) {
                try {
                    Team team = matchManager.getTeamById(teamId);
                    // Crear un "tag" para el equipo favorito
                    JPanel teamTag = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                    teamTag.setBackground(new Color(254, 243, 199)); // bg-yellow-100 (ejemplo, podría ser dinámico)
                    teamTag.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(253, 230, 138)), // border-yellow-200
                        BorderFactory.createEmptyBorder(5, 10, 5, 10) // px-4 py-2
                    ));
                    teamTag.setOpaque(true); // Asegura que el color de fondo se vea

                    JLabel teamLabel = new JLabel(getFlagEmoji(team.getCountry()) + " " + team.getName());
                    teamLabel.setFont(new Font("Inter", Font.BOLD, 14));
                    teamLabel.setForeground(new Color(180, 83, 9)); // text-yellow-800
                    teamTag.add(teamLabel);
                    favoriteTeamsPanel.add(teamTag);

                } catch (TeamNotFoundException e) {
                    System.err.println("Equipo favorito no encontrado: " + teamId + " - " + e.getMessage());
                    JLabel errorLabel = new JLabel("Equipo desconocido: " + teamId);
                    errorLabel.setFont(new Font("Inter", Font.PLAIN, 14));
                    errorLabel.setForeground(Color.RED);
                    favoriteTeamsPanel.add(errorLabel);
                }
            }
        }
        favoriteTeamsPanel.revalidate(); // Asegura que el layout se actualice
        favoriteTeamsPanel.repaint(); // Redibuja el panel
    }

    // Método para cargar partidos destacados/próximos
    private void loadFeaturedMatches() {
        matchesTableModel.setRowCount(0); // Limpiar tabla
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy | hh:mm a");
        List<Match> allMatches = matchManager.getAllMatches();

        // Ordenar partidos por fecha (los próximos primero)
        allMatches.sort((m1, m2) -> m1.getDate().compareTo(m2.getDate()));

        for (Match match : allMatches) {
            String matchName = match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName();
            String dateTime = sdf.format(match.getDate()) + " (CR)";
            String status = match.getStatus().substring(0, 1).toUpperCase() + match.getStatus().substring(1);
            matchesTableModel.addRow(new Object[]{matchName, dateTime, status});
        }
    }

    // Método auxiliar para obtener un partido por su nombre (para el botón "Ver Detalles")
    private Match findMatchByName(String matchName) {
        for (Match match : matchManager.getAllMatches()) {
            String currentMatchName = match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName();
            if (currentMatchName.equals(matchName)) {
                return match;
            }
        }
        return null;
    }

    // Método auxiliar para obtener emojis de banderas (ejemplo, no exhaustivo)
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
            default: return "🏳️"; // Bandera genérica
        }
    }
}