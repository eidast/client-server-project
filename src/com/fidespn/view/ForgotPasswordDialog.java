package com.fidespn.view;

import com.fidespn.service.UserManager;
import com.fidespn.service.exceptions.InvalidCredentialsException;
import com.fidespn.service.exceptions.UserNotFoundException;
import com.fidespn.client.adapters.SocketUserClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog to reset password by validating username and email.
 */
public class ForgotPasswordDialog extends JDialog {
    private final UserManager userManager;
    private boolean useServer = true; // Toggle for backend usage
    private SocketUserClient socketUserClient;

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;

    public ForgotPasswordDialog(Frame owner, UserManager userManager) {
        super(owner, "Restablecer contraseña", true);
        this.userManager = userManager;
        setSize(460, 380);
        setLocationRelativeTo(owner);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("Restablecer contraseña", SwingConstants.CENTER);
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setForeground(new Color(52, 73, 94));
        panel.add(title, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        usernameField = new JTextField(24);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(24);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // New password
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Nueva contraseña:"), gbc);
        newPasswordField = new JPasswordField(24);
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);

        // Confirm password
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Confirmar contraseña:"), gbc);
        confirmPasswordField = new JPasswordField(24);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Message
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Inter", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        JButton submitBtn = createPrimaryButton("Restablecer");
        JButton cancelBtn = createSecondaryButton("Cancelar");
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);
        gbc.gridy = 6;
        panel.add(btnPanel, gbc);

        submitBtn.addActionListener(e -> performReset());
        cancelBtn.addActionListener(e -> dispose());

        add(panel);
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Inter", Font.BOLD, 15));
        b.setBackground(new Color(37, 99, 235));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
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

    private void performReset() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String newPass = new String(newPasswordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            setError("Todos los campos son obligatorios");
            return;
        }
        if (!email.contains("@")) {
            setError("Ingrese un email válido");
            return;
        }
        if (newPass.length() < 6) {
            setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        if (!newPass.equals(confirm)) {
            setError("Las contraseñas no coinciden");
            return;
        }

        try {
            if (useServer) {
                if (socketUserClient == null) socketUserClient = new SocketUserClient("127.0.0.1", 5432);
                socketUserClient.resetPassword(username, email, newPass);
            } else {
                userManager.resetPasswordByUsernameAndEmail(username, email, newPass);
            }
            setSuccess("Contraseña actualizada correctamente");
            Timer t = new Timer(1000, evt -> dispose());
            t.setRepeats(false);
            t.start();
        } catch (UserNotFoundException | InvalidCredentialsException ex) {
            setError(ex.getMessage());
        } catch (Exception ex) {
            setError("Error al restablecer: " + ex.getMessage());
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


