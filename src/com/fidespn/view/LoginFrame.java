package com.fidespn.view;

import com.fidespn.model.User;
import com.fidespn.model.Administrator;
import com.fidespn.model.Correspondent;
import com.fidespn.model.Fanatic;
import com.fidespn.service.UserManager;
import com.fidespn.service.exceptions.InvalidCredentialsException;
import com.fidespn.service.exceptions.UserNotFoundException;
import com.fidespn.service.MatchManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {
    private UserManager userManager;
    private MatchManager matchManager;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JButton loginButton;
    private JLabel createAccountLabel;
    private JLabel forgotPasswordLabel;

    public LoginFrame(UserManager userManager) {
        this.userManager = userManager;
        this.matchManager = new MatchManager();
        setTitle("FidESPN United 2026 - Iniciar Sesión");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        addListeners();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 242, 245)); // bg-gray-100 / f0f2f5
        panel.setBorder(new EmptyBorder(30, 30, 30, 30)); // Padding alrededor del panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Márgenes entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Rellenar horizontalmente

        // Título de la aplicación
        JLabel titleLabel = new JLabel("FidESPN United 2026", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30)); // Tamaño más grande
        titleLabel.setForeground(new Color(52, 73, 94)); // text-gray-800
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa dos columnas
        gbc.weightx = 1.0; // Permite que el título se expanda
        panel.add(titleLabel, gbc);

        // Subtítulo
        JLabel subtitleLabel = new JLabel("La pasión del fútbol te espera", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 16)); // Tamaño más grande
        subtitleLabel.setForeground(new Color(108, 122, 137)); // text-gray-600
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 8, 20, 8); // Menos margen superior, más inferior
        panel.add(subtitleLabel, gbc);

        // Campo de Usuario
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Ocupa toda la fila para el campo
        gbc.insets = new Insets(10, 10, 5, 10); // Márgenes para el campo
        usernameField = new JTextField(25); // Ancho preferido
        usernameField.setFont(new Font("Inter", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)), // border-gray-300
                BorderFactory.createEmptyBorder(8, 12, 8, 12) // px-4 py-3
        ));
        panel.add(usernameField, gbc);

        // Campo de Contraseña
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 10, 10); // Márgenes
        passwordField = new JPasswordField(25);
        passwordField.setFont(new Font("Inter", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)), // border-gray-300
                BorderFactory.createEmptyBorder(8, 12, 8, 12) // px-4 py-3
        ));
        panel.add(passwordField, gbc);

        // Etiqueta de Mensajes (errores, éxito)
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Inter", Font.BOLD, 13));
        messageLabel.setForeground(Color.RED);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 10, 10);
        panel.add(messageLabel, gbc);

        // Botón de Iniciar Sesión
        loginButton = new JButton("Iniciar Sesión");
        loginButton.setFont(new Font("Inter", Font.BOLD, 18)); // Tamaño de fuente más grande
        loginButton.setBackground(new Color(37, 99, 235)); // bg-blue-600
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setPreferredSize(new Dimension(300, 50)); // Tamaño preferido más grande
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding interno

        gbc.gridy = 5;
        gbc.insets = new Insets(15, 10, 5, 10); // Más margen superior
        gbc.fill = GridBagConstraints.NONE; // No rellenar, usar tamaño preferido
        panel.add(loginButton, gbc);

        // Enlaces de ayuda
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        linksPanel.setOpaque(false);
        createAccountLabel = new JLabel("Crear cuenta");
        createAccountLabel.setForeground(new Color(37, 99, 235));
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel = new JLabel("¿Olvidaste tu contraseña?");
        forgotPasswordLabel.setForeground(new Color(37, 99, 235));
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linksPanel.add(createAccountLabel);
        linksPanel.add(new JLabel("|"));
        linksPanel.add(forgotPasswordLabel);

        gbc.gridy = 6;
        gbc.insets = new Insets(8, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(linksPanel, gbc);

        add(panel);
    }

    private void addListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Abrir registro de cuenta
        createAccountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterFrame(userManager).setVisible(true);
            }
        });

        // Abrir recuperación de contraseña
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ForgotPasswordDialog dialog = new ForgotPasswordDialog(LoginFrame.this, userManager);
                dialog.setVisible(true);
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Por favor, ingrese usuario y contraseña.");
            messageLabel.setForeground(new Color(239, 68, 68)); // text-red-500
            return;
        }

        try {
            User loggedInUser = userManager.loginUser(username, password);
            messageLabel.setText("¡Inicio de sesión exitoso! Redirigiendo...");
            messageLabel.setForeground(new Color(34, 197, 94)); // text-green-500

            this.dispose();

            if (loggedInUser instanceof Administrator) {
                new AdminDashboardFrame(userManager, matchManager).setVisible(true);
            } else if (loggedInUser instanceof Correspondent) {
                new CorrespondentDashboardFrame(userManager, matchManager, loggedInUser).setVisible(true);
            } else if (loggedInUser instanceof Fanatic) {
                new FanaticDashboardFrame(userManager, matchManager, loggedInUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Tipo de usuario no reconocido. Contacte al soporte.", "Error de Inicio de Sesión", JOptionPane.ERROR_MESSAGE);
                new LoginFrame(userManager).setVisible(true);
            }

        } catch (UserNotFoundException | InvalidCredentialsException ex) {
            messageLabel.setText(ex.getMessage());
            messageLabel.setForeground(new Color(239, 68, 68)); // text-red-500
        } catch (Exception ex) {
            messageLabel.setText("Ocurrió un error inesperado: " + ex.getMessage());
            messageLabel.setForeground(new Color(239, 68, 68)); // text-red-500
            ex.printStackTrace();
        }
    }
}