package com.fidespn.view;

import com.fidespn.service.UserManager;
import com.fidespn.service.exceptions.DuplicateUsernameException;
import com.fidespn.client.adapters.SocketUserClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Registration screen for new users (defaults to Fanatic role).
 */
public class RegisterFrame extends JFrame {
    private final UserManager userManager;
    private boolean useServer = false; // Toggle for backend usage
    private SocketUserClient socketUserClient;

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;

    public RegisterFrame(UserManager userManager) {
        this.userManager = userManager;
        setTitle("FidESPN United 2026 - Crear Cuenta");
        setSize(480, 460);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel title = new JLabel("Crear cuenta", SwingConstants.CENTER);
        title.setFont(new Font("Inter", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        panel.add(title, gbc);

        // Username
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Usuario:"), gbc);
        usernameField = new JTextField(24);
        usernameField.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(24);
        emailField.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Contraseña:"), gbc);
        passwordField = new JPasswordField(24);
        passwordField.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Confirmar Contraseña:"), gbc);
        confirmPasswordField = new JPasswordField(24);
        confirmPasswordField.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Message
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Inter", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        JButton registerBtn = createPrimaryButton("Crear Cuenta");
        JButton cancelBtn = createSecondaryButton("Cancelar");
        btnPanel.add(registerBtn);
        btnPanel.add(cancelBtn);
        gbc.gridy = 6;
        panel.add(btnPanel, gbc);

        // Listeners
        registerBtn.addActionListener(e -> performRegister());
        cancelBtn.addActionListener(e -> this.dispose());

        add(panel);
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Inter", Font.BOLD, 16));
        b.setBackground(new Color(34, 197, 94));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(180, 44));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Inter", Font.BOLD, 14));
        b.setBackground(new Color(107, 114, 128));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    private void performRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        // Basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setError("Todos los campos son obligatorios");
            return;
        }
        if (username.length() < 3) {
            setError("El usuario debe tener al menos 3 caracteres");
            return;
        }
        if (!email.contains("@")) {
            setError("Ingrese un email válido");
            return;
        }
        if (password.length() < 6) {
            setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        if (!password.equals(confirm)) {
            setError("Las contraseñas no coinciden");
            return;
        }

        try {
            if (useServer) {
                if (socketUserClient == null) socketUserClient = new SocketUserClient("127.0.0.1", 5432);
                socketUserClient.register(username, password, email, "fanatic");
            } else {
                userManager.registerUser(username, password, email, "fanatic");
            }
            setSuccess("Cuenta creada. Ahora puedes iniciar sesión.");
            // Close after short delay
            Timer t = new Timer(1200, evt -> {
                this.dispose();
                new LoginFrame(userManager).setVisible(true);
            });
            t.setRepeats(false);
            t.start();
        } catch (DuplicateUsernameException ex) {
            setError("El nombre de usuario ya existe");
        } catch (Exception ex) {
            setError("Error al crear cuenta: " + ex.getMessage());
        }
    }

    private void setError(String msg) {
        messageLabel.setForeground(new Color(239, 68, 68));
        messageLabel.setText(msg);
    }

    private void setSuccess(String msg) {
        messageLabel.setForeground(new Color(34, 197, 94));
        messageLabel.setText(msg);
    }
}


