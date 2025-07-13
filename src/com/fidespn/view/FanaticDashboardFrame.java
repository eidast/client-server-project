package com.fidespn.view;

import com.fidespn.service.UserManager;

import javax.swing.*;
import java.awt.*;

public class FanaticDashboardFrame extends JFrame {
    private UserManager userManager;

    public FanaticDashboardFrame(UserManager userManager) {
        this.userManager = userManager;
        setTitle("FidESPN United 2026 - Panel de Fanático");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));

        JLabel welcomeLabel = new JLabel("Hola, Fanático. ¡Disfruta el Mundial United 2026!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 20));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginFrame(userManager).setVisible(true);
        });
        JPanel southPanel = new JPanel();
        southPanel.add(logoutButton);
        panel.add(southPanel, BorderLayout.SOUTH);

        add(panel);
    }
}