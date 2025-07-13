package com.fidespn.main;

import com.fidespn.model.Administrator;
import com.fidespn.model.Correspondent;
import com.fidespn.model.Fanatic;
import com.fidespn.service.UserManager;
import com.fidespn.service.MatchManager;
import com.fidespn.service.exceptions.DuplicateUsernameException;
import com.fidespn.view.LoginFrame;

import javax.swing.*;
import java.util.Date;

public class MainApp {
    public static void main(String[] args) {
        // Asegurarse de que Swing se ejecute en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UserManager userManager = new UserManager();
                MatchManager matchManager = new MatchManager(); // Instancia del gestor de partidos

                // --- Inicialización de usuarios de prueba (solo si no existen) ---
                // Esto es para que puedas probar el login con diferentes roles
                try {
                    if (userManager.getAllUsers().isEmpty()) {
                        System.out.println("Creando usuarios de prueba...");
                        userManager.registerUser("admin", "admin123", "admin@fidespn.com", "admin");
                        userManager.registerUser("corresponsal1", "pass123", "corresponsal1@fidespn.com", "correspondent");
                        userManager.registerUser("fanatico1", "pass123", "fanatico1@fidespn.com", "fanatic");
                        userManager.registerUser("fanatico2", "pass123", "fanatico2@fidespn.com", "fanatic");

                        // Asignar un equipo favorito al fanatico1 para probar
                        Fanatic f1 = (Fanatic) userManager.getUserByUsername("fanatico1");
                        f1.addFavoriteTeam("BRA"); // ID de equipo de ejemplo
                        userManager.updateUser(f1); // Guardar el cambio

                        // Crear algunos partidos de prueba
                        // Asegúrate de que los IDs de equipo existan en MatchManager (ya los inicializamos)
                        matchManager.createMatch(new Date(), "10:00 AM", "USA", "MEX", "corresponsal1");
                        matchManager.createMatch(new Date(), "01:00 PM", "BRA", "ARG", "corresponsal1");

                    } else {
                        System.out.println("Usuarios de prueba ya existen o fueron cargados.");
                    }
                } catch (DuplicateUsernameException e) {
                    System.out.println("Usuarios de prueba ya registrados: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error al crear usuarios de prueba o al interactuar con managers: " + e.getMessage());
                    e.printStackTrace();
                }
                // --- Fin de inicialización de usuarios de prueba ---

                LoginFrame loginFrame = new LoginFrame(userManager);
                loginFrame.setVisible(true);
            }
        });
    }
}