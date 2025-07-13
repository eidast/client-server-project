package com.fidespn.service;

import com.fidespn.model.User;
import com.fidespn.model.Administrator;
import com.fidespn.model.Correspondent;
import com.fidespn.model.Fanatic;
import com.fidespn.service.exceptions.DuplicateUsernameException;
import com.fidespn.service.exceptions.InvalidCredentialsException;
import com.fidespn.service.exceptions.UserNotFoundException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserManager {
    private Map<String, User> usersByUsername;
    private List<User> allUsers;
    private static final String USERS_FILE = "users.ser";

    public UserManager() {
        this.usersByUsername = new HashMap<>();
        this.allUsers = new ArrayList<>();
        loadUsers();
    }

    public User registerUser(String username, String password, String email, String userType) throws DuplicateUsernameException {
        if (usersByUsername.containsKey(username)) {
            throw new DuplicateUsernameException("El nombre de usuario '" + username + "' ya está en uso.");
        }

        String userId = UUID.randomUUID().toString();
        User newUser;

        switch (userType.toLowerCase()) {
            case "fanatic":
                newUser = new Fanatic(userId, username, password, email);
                break;
            case "correspondent":
                newUser = new Correspondent(userId, username, password, email);
                break;
            case "admin":
                newUser = new Administrator(userId, username, password, email, 1);
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuario no válido: " + userType);
        }

        usersByUsername.put(username, newUser);
        allUsers.add(newUser);
        saveUsers();
        System.out.println("Usuario registrado: " + newUser.getUsername() + " como " + userType);
        return newUser;
    }

    public User loginUser(String username, String password) throws UserNotFoundException, InvalidCredentialsException {
        User user = usersByUsername.get(username);
        if (user == null) {
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }
        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Contraseña incorrecta para el usuario: " + username);
        }
        System.out.println("Inicio de sesión exitoso para: " + username);
        return user;
    }

    public User getUserById(String userId) throws UserNotFoundException {
        for (User user : allUsers) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        throw new UserNotFoundException("Usuario con ID " + userId + " no encontrado.");
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        User user = usersByUsername.get(username);
        if (user == null) {
            throw new UserNotFoundException("Usuario con nombre de usuario " + username + " no encontrado.");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers);
    }

    public void updateUser(User user) throws UserNotFoundException {
        boolean found = false;
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUserId().equals(user.getUserId())) {
                allUsers.set(i, user);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new UserNotFoundException("No se pudo actualizar el usuario con ID " + user.getUserId() + ". No encontrado.");
        }
        usersByUsername.put(user.getUsername(), user);
        saveUsers();
        System.out.println("Usuario " + user.getUsername() + " actualizado.");
    }

    public void deleteUser(String userId) throws UserNotFoundException {
        User userToRemove = null;
        for (User user : allUsers) {
            if (user.getUserId().equals(userId)) {
                userToRemove = user;
                break;
            }
        }

        if (userToRemove != null) {
            allUsers.remove(userToRemove);
            usersByUsername.remove(userToRemove.getUsername());
            saveUsers();
            System.out.println("Usuario con ID " + userId + " eliminado.");
        } else {
            throw new UserNotFoundException("Usuario con ID " + userId + " no encontrado para eliminar.");
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(allUsers);
            System.out.println("Usuarios guardados en " + USERS_FILE);
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    this.allUsers = (List<User>) obj;
                    this.usersByUsername.clear();
                    for (User user : allUsers) {
                        this.usersByUsername.put(user.getUsername(), user);
                    }
                    System.out.println("Usuarios cargados desde " + USERS_FILE);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar usuarios: " + e.getMessage());
                this.usersByUsername = new HashMap<>();
                this.allUsers = new ArrayList<>();
            }
        } else {
            System.out.println("Archivo de usuarios no encontrado. Iniciando con usuarios vacíos.");
        }
    }
}