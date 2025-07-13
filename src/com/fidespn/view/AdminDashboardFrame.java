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
import com.fidespn.service.exceptions.DuplicateUsernameException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

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
        
        // Listener para crear partido
        createMatchBtn.addActionListener(e -> showCreateMatchDialog());
        
        // Listener para editar partido
        editMatchBtn.addActionListener(e -> {
            int selectedRow = matchesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un partido de la tabla para editar.", "Partido no seleccionado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // El ID del partido no está en la tabla, así que lo buscamos por los datos de la fila
            String matchDesc = (String) matchesTable.getValueAt(selectedRow, 0); // "EquipoA vs EquipoB"
            String dateTime = (String) matchesTable.getValueAt(selectedRow, 1); // "fecha y hora"
            // Buscamos el partido por nombre y fecha
            com.fidespn.model.Match matchToEdit = null;
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy | hh:mm a");
            for (var m : matchManager.getAllMatches()) {
                String desc = m.getHomeTeam().getName() + " vs " + m.getAwayTeam().getName();
                String dt = sdf.format(m.getDate()) + " (CR)";
                if (desc.equals(matchDesc) && dt.equals(dateTime)) {
                    matchToEdit = m;
                    break;
                }
            }
            if (matchToEdit == null) {
                JOptionPane.showMessageDialog(this, "No se pudo encontrar el partido seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showEditMatchDialog(matchToEdit);
        });
        
        // Listener para eliminar partido
        deleteMatchBtn.addActionListener(e -> {
            int selectedRow = matchesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un partido de la tabla para eliminar.", "Partido no seleccionado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String matchDesc = (String) matchesTable.getValueAt(selectedRow, 0); // "EquipoA vs EquipoB"
            String dateTime = (String) matchesTable.getValueAt(selectedRow, 1); // "fecha y hora"
            com.fidespn.model.Match matchToDelete = null;
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy | hh:mm a");
            for (var m : matchManager.getAllMatches()) {
                String desc = m.getHomeTeam().getName() + " vs " + m.getAwayTeam().getName();
                String dt = sdf.format(m.getDate()) + " (CR)";
                if (desc.equals(matchDesc) && dt.equals(dateTime)) {
                    matchToDelete = m;
                    break;
                }
            }
            if (matchToDelete == null) {
                JOptionPane.showMessageDialog(this, "No se pudo encontrar el partido seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar el partido '" + matchDesc + "'?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    matchManager.deleteMatchById(matchToDelete.getMatchId());
                    loadDataIntoTables();
                    JOptionPane.showMessageDialog(this, "Partido eliminado exitosamente.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar partido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
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
        
        // Agregar listener para el botón crear usuario
        createUserBtn.addActionListener(e -> showCreateUserDialog());
        
        // Agregar listener para el botón editar usuario
        editUserBtn.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor, seleccione un usuario de la tabla para editar.", 
                    "Usuario no seleccionado", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = (String) usersTable.getValueAt(selectedRow, 0);
            showEditUserDialog(username);
        });
        
        // Agregar listener para el botón eliminar usuario
        deleteUserBtn.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor, seleccione un usuario de la tabla para eliminar.", 
                    "Usuario no seleccionado", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = (String) usersTable.getValueAt(selectedRow, 0);
            String role = (String) usersTable.getValueAt(selectedRow, 1);
            showDeleteUserConfirmation(username, role);
        });
        
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

    private void showCreateUserDialog() {
        JDialog dialog = new JDialog(this, "Crear Nuevo Usuario", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Crear Nuevo Usuario", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Campo Username
        JLabel usernameLabel = new JLabel("Nombre de Usuario:");
        usernameLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Campo Password
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Campo Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Campo Tipo de Usuario
        JLabel userTypeLabel = new JLabel("Tipo de Usuario:");
        userTypeLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(userTypeLabel, gbc);

        String[] userTypes = {"Fanático", "Corresponsal", "Administrador"};
        JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);
        userTypeCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(userTypeCombo, gbc);

        // Etiqueta para mensajes
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Inter", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(messageLabel, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton createButton = new JButton("Crear Usuario");
        createButton.setFont(new Font("Inter", Font.BOLD, 14));
        createButton.setBackground(new Color(34, 197, 94));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setOpaque(true);
        createButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Inter", Font.BOLD, 14));
        cancelButton.setBackground(new Color(107, 114, 128));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 6;
        mainPanel.add(buttonPanel, gbc);

        // Listeners
        createButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();
            String selectedUserType = (String) userTypeCombo.getSelectedItem();

            // Validaciones
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                messageLabel.setText("Todos los campos son obligatorios");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }

            if (username.length() < 3) {
                messageLabel.setText("El nombre de usuario debe tener al menos 3 caracteres");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }

            if (password.length() < 6) {
                messageLabel.setText("La contraseña debe tener al menos 6 caracteres");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }

            if (!email.contains("@")) {
                messageLabel.setText("Ingrese un email válido");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }

            try {
                // Convertir el tipo de usuario seleccionado al formato esperado por el UserManager
                String userTypeForManager = "";
                switch (selectedUserType) {
                    case "Fanático":
                        userTypeForManager = "fanatic";
                        break;
                    case "Corresponsal":
                        userTypeForManager = "correspondent";
                        break;
                    case "Administrador":
                        userTypeForManager = "admin";
                        break;
                }

                User newUser = userManager.registerUser(username, password, email, userTypeForManager);
                
                messageLabel.setText("Usuario creado exitosamente: " + newUser.getUsername());
                messageLabel.setForeground(new Color(34, 197, 94));
                
                // Recargar la tabla de usuarios
                loadDataIntoTables();
                
                // Cerrar el diálogo después de un breve delay
                Timer timer = new Timer(1500, evt -> {
                    dialog.dispose();
                });
                timer.setRepeats(false);
                timer.start();
                
            } catch (DuplicateUsernameException ex) {
                messageLabel.setText("El nombre de usuario ya existe");
                messageLabel.setForeground(new Color(239, 68, 68));
            } catch (Exception ex) {
                messageLabel.setText("Error al crear usuario: " + ex.getMessage());
                messageLabel.setForeground(new Color(239, 68, 68));
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showEditUserDialog(String username) {
        JDialog dialog = new JDialog(this, "Editar Usuario", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Editar Usuario: " + username, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Campo Username
        JLabel usernameLabel = new JLabel("Nombre de Usuario:");
        usernameLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Campo Password
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Campo Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Campo Tipo de Usuario
        JLabel userTypeLabel = new JLabel("Tipo de Usuario:");
        userTypeLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(userTypeLabel, gbc);

        String[] userTypes = {"Fanático", "Corresponsal", "Administrador"};
        JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);
        userTypeCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(userTypeCombo, gbc);

        // Etiqueta para mensajes
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Inter", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(messageLabel, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Guardar Cambios");
        saveButton.setFont(new Font("Inter", Font.BOLD, 14));
        saveButton.setBackground(new Color(34, 197, 94));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setOpaque(true);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Inter", Font.BOLD, 14));
        cancelButton.setBackground(new Color(107, 114, 128));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 6;
        mainPanel.add(buttonPanel, gbc);

                 // Cargar datos del usuario seleccionado
         try {
             User userToEdit = userManager.getUserByUsername(username);
             usernameField.setText(userToEdit.getUsername());
             passwordField.setText(userToEdit.getPassword());
             emailField.setText(userToEdit.getEmail());
             
             // Seleccionar el tipo de usuario correcto en el combo
             if (userToEdit instanceof Fanatic) {
                 userTypeCombo.setSelectedItem("Fanático");
             } else if (userToEdit instanceof Correspondent) {
                 userTypeCombo.setSelectedItem("Corresponsal");
             } else if (userToEdit instanceof Administrator) {
                 userTypeCombo.setSelectedItem("Administrador");
             }
         } catch (UserNotFoundException ex) {
             messageLabel.setText("Error: Usuario no encontrado");
             messageLabel.setForeground(new Color(239, 68, 68));
         }

         // Listeners
         saveButton.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword());
            String newEmail = emailField.getText().trim();
            String selectedUserType = (String) userTypeCombo.getSelectedItem();

            // Validaciones
            if (newUsername.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty()) {
                messageLabel.setText("Todos los campos son obligatorios");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }

            if (newUsername.length() < 3) {
                messageLabel.setText("El nombre de usuario debe tener al menos 3 caracteres");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }

            if (newPassword.length() < 6) {
                messageLabel.setText("La contraseña debe tener al menos 6 caracteres");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }

            if (!newEmail.contains("@")) {
                messageLabel.setText("Ingrese un email válido");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }

            try {
                // Convertir el tipo de usuario seleccionado al formato esperado por el UserManager
                String userTypeForManager = "";
                switch (selectedUserType) {
                    case "Fanático":
                        userTypeForManager = "fanatic";
                        break;
                    case "Corresponsal":
                        userTypeForManager = "correspondent";
                        break;
                    case "Administrador":
                        userTypeForManager = "admin";
                        break;
                }

                                 User updatedUser = userManager.updateUserByUsername(username, newUsername, newPassword, newEmail, userTypeForManager);
                
                messageLabel.setText("Usuario actualizado exitosamente: " + updatedUser.getUsername());
                messageLabel.setForeground(new Color(34, 197, 94));
                
                // Recargar la tabla de usuarios
                loadDataIntoTables();
                
                // Cerrar el diálogo después de un breve delay
                Timer timer = new Timer(1500, evt -> {
                    dialog.dispose();
                });
                timer.setRepeats(false);
                timer.start();
                
            } catch (DuplicateUsernameException ex) {
                messageLabel.setText("El nombre de usuario ya existe");
                messageLabel.setForeground(new Color(239, 68, 68));
            } catch (UserNotFoundException ex) {
                messageLabel.setText("Usuario no encontrado: " + username);
                messageLabel.setForeground(new Color(239, 68, 68));
            } catch (Exception ex) {
                messageLabel.setText("Error al actualizar usuario: " + ex.getMessage());
                messageLabel.setForeground(new Color(239, 68, 68));
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showDeleteUserConfirmation(String username, String role) {

         JDialog dialog = new JDialog(this, "Confirmar Eliminación", true);
         dialog.setLayout(new BorderLayout());
         dialog.setSize(420, 210);
         dialog.setLocationRelativeTo(this);
         dialog.setResizable(false);

         JPanel mainPanel = new JPanel();
         mainPanel.setLayout(new GridBagLayout());
         mainPanel.setBackground(new Color(240, 242, 245));
         mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

         GridBagConstraints gbc = new GridBagConstraints();
         gbc.insets = new Insets(10, 10, 10, 10);
         gbc.fill = GridBagConstraints.HORIZONTAL;

         // Título con HTML para salto de línea automático
         JLabel titleLabel = new JLabel("<html><div style='text-align:center;'>¿Está seguro de que desea eliminar al usuario <b>" + username + "</b>?</div></html>", SwingConstants.CENTER);
         titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
         titleLabel.setForeground(new Color(52, 73, 94));
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.gridwidth = 2;
         mainPanel.add(titleLabel, gbc);

        // Botones de confirmación
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton confirmButton = new JButton("Sí, Eliminar");
        confirmButton.setFont(new Font("Inter", Font.BOLD, 14));
        confirmButton.setBackground(new Color(239, 68, 68));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setOpaque(true);
        confirmButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Inter", Font.BOLD, 14));
        cancelButton.setBackground(new Color(107, 114, 128));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 1;
        mainPanel.add(buttonPanel, gbc);

        // Listeners
        confirmButton.addActionListener(e -> {
            try {
                userManager.deleteUserByUsername(username);
                JOptionPane.showMessageDialog(this, 
                    "Usuario " + username + " eliminado exitosamente.", 
                    "Eliminación Exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadDataIntoTables();
                dialog.dispose();
            } catch (UserNotFoundException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar usuario: " + ex.getMessage(), 
                    "Error de Eliminación", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error inesperado al eliminar usuario: " + ex.getMessage(), 
                    "Error de Eliminación", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
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

    // --- MODAL CREAR PARTIDO ---
    private void showCreateMatchDialog() {
        JDialog dialog = new JDialog(this, "Crear Nuevo Partido", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(650, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Crear Nuevo Partido", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 22));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        mainPanel.add(titleLabel, gbc);

        // Fecha (JSpinner)
        JLabel dateLabel = new JLabel("Fecha:");
        dateLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        mainPanel.add(dateLabel, gbc);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.util.Date today = calendar.getTime();
        java.util.Date minDate = matchManager.getAllMatches().stream()
                .map(Match::getDate)
                .min(Date::compareTo)
                .orElse(today); // Fallback to today if no matches exist
        javax.swing.SpinnerDateModel dateModel = new javax.swing.SpinnerDateModel(minDate, minDate, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        dateSpinner.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(dateSpinner, gbc);

        // Hora (ComboBox)
        JLabel timeLabel = new JLabel("Hora:");
        timeLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 2;
        mainPanel.add(timeLabel, gbc);

        JComboBox<String> hourCombo = new JComboBox<>();
        for (int h = 0; h < 24; h++) hourCombo.addItem(String.format("%02d", h));
        hourCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 3;
        mainPanel.add(hourCombo, gbc);

        JLabel minLabel = new JLabel("Minuto:");
        minLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridy = 2;
        gbc.gridx = 2;
        mainPanel.add(minLabel, gbc);

        JComboBox<String> minCombo = new JComboBox<>();
        for (int m = 0; m < 60; m += 5) minCombo.addItem(String.format("%02d", m));
        minCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 3;
        mainPanel.add(minCombo, gbc);

        // Equipo Local
        JLabel homeLabel = new JLabel("Equipo Local:");
        homeLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(homeLabel, gbc);

        JComboBox<String> homeCombo = new JComboBox<>();
        homeCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(homeCombo, gbc);

        // Equipo Visitante
        JLabel awayLabel = new JLabel("Equipo Visitante:");
        awayLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(awayLabel, gbc);

        JComboBox<String> awayCombo = new JComboBox<>();
        awayCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(awayCombo, gbc);

        // Corresponsal
        JLabel corrLabel = new JLabel("Corresponsal:");
        corrLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(corrLabel, gbc);

        JComboBox<String> corrCombo = new JComboBox<>();
        corrCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(corrCombo, gbc);

        // Mensaje de error
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Inter", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        mainPanel.add(messageLabel, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        JButton createButton = new JButton("Crear Partido");
        createButton.setFont(new Font("Inter", Font.BOLD, 14));
        createButton.setBackground(new Color(34, 197, 94));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setOpaque(true);
        createButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Inter", Font.BOLD, 14));
        cancelButton.setBackground(new Color(107, 114, 128));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        gbc.gridy = 6;
        mainPanel.add(buttonPanel, gbc);

        // Poblar combos de equipos y corresponsales
        homeCombo.removeAllItems();
        awayCombo.removeAllItems();
        for (var team : matchManager.getAllTeams()) {
            homeCombo.addItem(team.getTeamId() + " - " + team.getName());
            awayCombo.addItem(team.getTeamId() + " - " + team.getName());
        }
        corrCombo.removeAllItems();
        java.util.Map<String, String> corrMap = new java.util.HashMap<>();
        for (var user : userManager.getAllUsers()) {
            if (user instanceof com.fidespn.model.Correspondent) {
                corrCombo.addItem(user.getUsername());
                corrMap.put(user.getUsername(), user.getUserId());
            }
        }

        // Listeners
        createButton.addActionListener(e -> {
            java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
            java.util.Calendar now = java.util.Calendar.getInstance();
            now.set(java.util.Calendar.HOUR_OF_DAY, 0);
            now.set(java.util.Calendar.MINUTE, 0);
            now.set(java.util.Calendar.SECOND, 0);
            now.set(java.util.Calendar.MILLISECOND, 0);
            if (selectedDate.before(now.getTime())) {
                messageLabel.setText("La fecha no puede estar en el pasado");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }
            String hour = (String) hourCombo.getSelectedItem();
            String min = (String) minCombo.getSelectedItem();
            String timeStr = hour + ":" + min;
            String homeSel = (String) homeCombo.getSelectedItem();
            String awaySel = (String) awayCombo.getSelectedItem();
            String corrSel = (String) corrCombo.getSelectedItem();

            if (homeSel == null || awaySel == null || corrSel == null) {
                messageLabel.setText("Todos los campos son obligatorios");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }
            if (homeSel.equals(awaySel)) {
                messageLabel.setText("El equipo local y visitante deben ser diferentes");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }
            // Validar que no sea el mismo equipo
            String homeId = homeSel.split(" - ")[0];
            String awayId = awaySel.split(" - ")[0];
            if (homeId.equals(awayId)) {
                messageLabel.setText("No se puede crear un partido del mismo equipo contra sí mismo");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }
            String corrId = corrMap.get(corrSel);
            try {
                matchManager.createMatch(selectedDate, timeStr, homeId, awayId, corrId);
                messageLabel.setText("Partido creado exitosamente");
                messageLabel.setForeground(new Color(34, 197, 94));
                loadDataIntoTables();
                new javax.swing.Timer(1200, evt -> dialog.dispose()).start();
            } catch (com.fidespn.service.exceptions.TeamNotFoundException ex) {
                messageLabel.setText("Equipo no encontrado");
                messageLabel.setForeground(new Color(239, 68, 68));
            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
                messageLabel.setForeground(new Color(239, 68, 68));
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // --- MODAL EDITAR PARTIDO ---
    private void showEditMatchDialog(com.fidespn.model.Match match) {
        JDialog dialog = new JDialog(this, "Editar Partido", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(650, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Editar Partido", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 22));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        mainPanel.add(titleLabel, gbc);

        // Fecha (JSpinner)
        JLabel dateLabel = new JLabel("Fecha:");
        dateLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        mainPanel.add(dateLabel, gbc);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.util.Date today = calendar.getTime();
        java.util.Date minDate = match.getDate().before(today) ? match.getDate() : today;
        javax.swing.SpinnerDateModel dateModel = new javax.swing.SpinnerDateModel(match.getDate(), minDate, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        dateSpinner.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(dateSpinner, gbc);

        // Hora (ComboBox)
        JLabel timeLabel = new JLabel("Hora:");
        timeLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 2;
        mainPanel.add(timeLabel, gbc);

        JComboBox<String> hourCombo = new JComboBox<>();
        for (int h = 0; h < 24; h++) hourCombo.addItem(String.format("%02d", h));
        hourCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 3;
        mainPanel.add(hourCombo, gbc);

        JLabel minLabel = new JLabel("Minuto:");
        minLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridy = 2;
        gbc.gridx = 2;
        mainPanel.add(minLabel, gbc);

        JComboBox<String> minCombo = new JComboBox<>();
        for (int m = 0; m < 60; m += 5) minCombo.addItem(String.format("%02d", m));
        minCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 3;
        mainPanel.add(minCombo, gbc);

        // Equipo Local
        JLabel homeLabel = new JLabel("Equipo Local:");
        homeLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(homeLabel, gbc);

        JComboBox<String> homeCombo = new JComboBox<>();
        homeCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(homeCombo, gbc);

        // Equipo Visitante
        JLabel awayLabel = new JLabel("Equipo Visitante:");
        awayLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(awayLabel, gbc);

        JComboBox<String> awayCombo = new JComboBox<>();
        awayCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(awayCombo, gbc);

        // Corresponsal
        JLabel corrLabel = new JLabel("Corresponsal:");
        corrLabel.setFont(new Font("Inter", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(corrLabel, gbc);

        JComboBox<String> corrCombo = new JComboBox<>();
        corrCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(corrCombo, gbc);

        // Mensaje de error
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Inter", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        mainPanel.add(messageLabel, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        JButton saveButton = new JButton("Guardar Cambios");
        saveButton.setFont(new Font("Inter", Font.BOLD, 14));
        saveButton.setBackground(new Color(34, 197, 94));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setOpaque(true);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Inter", Font.BOLD, 14));
        cancelButton.setBackground(new Color(107, 114, 128));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridy = 6;
        mainPanel.add(buttonPanel, gbc);

        // Poblar combos y setear valores actuales
        homeCombo.removeAllItems();
        awayCombo.removeAllItems();
        String homeValue = null, awayValue = null;
        for (var team : matchManager.getAllTeams()) {
            String val = team.getTeamId() + " - " + team.getName();
            homeCombo.addItem(val);
            awayCombo.addItem(val);
            if (team.getTeamId().equals(match.getHomeTeam().getTeamId())) homeValue = val;
            if (team.getTeamId().equals(match.getAwayTeam().getTeamId())) awayValue = val;
        }
        homeCombo.setSelectedItem(homeValue);
        awayCombo.setSelectedItem(awayValue);

        corrCombo.removeAllItems();
        java.util.Map<String, String> corrMap = new java.util.HashMap<>();
        String corrValue = null;
        for (var user : userManager.getAllUsers()) {
            if (user instanceof com.fidespn.model.Correspondent) {
                corrCombo.addItem(user.getUsername());
                corrMap.put(user.getUsername(), user.getUserId());
                if (user.getUserId().equals(match.getCorrespondentId())) corrValue = user.getUsername();
            }
        }
        corrCombo.setSelectedItem(corrValue);

        // Setear hora y minuto actuales
        String[] hm = match.getTime().split(":");
        hourCombo.setSelectedItem(hm[0]);
        minCombo.setSelectedItem(hm[1]);

        // Listeners
        saveButton.addActionListener(e -> {
            java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
            java.util.Calendar now = java.util.Calendar.getInstance();
            now.set(java.util.Calendar.HOUR_OF_DAY, 0);
            now.set(java.util.Calendar.MINUTE, 0);
            now.set(java.util.Calendar.SECOND, 0);
            now.set(java.util.Calendar.MILLISECOND, 0);
            if (selectedDate.before(now.getTime())) {
                messageLabel.setText("La fecha no puede estar en el pasado");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }
            String hour = (String) hourCombo.getSelectedItem();
            String min = (String) minCombo.getSelectedItem();
            String timeStr = hour + ":" + min;
            String homeSel = (String) homeCombo.getSelectedItem();
            String awaySel = (String) awayCombo.getSelectedItem();
            String corrSel = (String) corrCombo.getSelectedItem();
            if (homeSel == null || awaySel == null || corrSel == null) {
                messageLabel.setText("Todos los campos son obligatorios");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }
            String homeId = homeSel.split(" - ")[0];
            String awayId = awaySel.split(" - ")[0];
            if (homeId.equals(awayId)) {
                messageLabel.setText("No se puede asignar el mismo equipo como local y visitante");
                messageLabel.setForeground(new Color(239, 68, 68));
                return;
            }
            String corrId = corrMap.get(corrSel);
            try {
                // Actualizar datos del partido
                match.setStatus("upcoming");
                match.setCorrespondentId(corrId);
                match.setScoreHome(0);
                match.setScoreAway(0);
                match.setStatus("upcoming");
                java.lang.reflect.Field dateField = match.getClass().getDeclaredField("date");
                dateField.setAccessible(true);
                dateField.set(match, selectedDate);
                java.lang.reflect.Field timeField = match.getClass().getDeclaredField("time");
                timeField.setAccessible(true);
                timeField.set(match, timeStr);
                java.lang.reflect.Field homeTeamField = match.getClass().getDeclaredField("homeTeam");
                homeTeamField.setAccessible(true);
                homeTeamField.set(match, matchManager.getTeamById(homeId));
                java.lang.reflect.Field awayTeamField = match.getClass().getDeclaredField("awayTeam");
                awayTeamField.setAccessible(true);
                awayTeamField.set(match, matchManager.getTeamById(awayId));
                // Guardar cambios
                matchManager.getAllMatches(); // Para asegurar persistencia
                matchManager.updateMatchStatus(match.getMatchId(), "upcoming");
                loadDataIntoTables();
                messageLabel.setText("Partido actualizado exitosamente");
                messageLabel.setForeground(new Color(34, 197, 94));
                new javax.swing.Timer(1200, evt -> dialog.dispose()).start();
            } catch (Exception ex) {
                messageLabel.setText("Error al actualizar: " + ex.getMessage());
                messageLabel.setForeground(new Color(239, 68, 68));
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}