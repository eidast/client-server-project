package com.fidespn.model;

public class Administrator extends User {
    private static final long serialVersionUID = 1L;
    private int adminLevel;

    public Administrator(String userId, String username, String password, String email, int adminLevel) {
        super(userId, username, password, email);
        this.adminLevel = adminLevel;
    }

    public int getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    @Override
    public String getDashboardGreeting() {
        return "Bienvenido, Administrador " + getUsername() + ". Tienes control total del sistema.";
    }

    public void createMatch() {
        System.out.println("Administrador " + username + " creando un nuevo partido.");
    }

    public void manageUsers() {
        System.out.println("Administrador " + username + " gestionando usuarios.");
    }
    
    @Override
    public String toString() {
        return "Administrator{" +
               "userId='" + userId + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", adminLevel=" + adminLevel +
               '}';
    }
}