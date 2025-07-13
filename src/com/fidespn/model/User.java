package com.fidespn.model;

import java.io.Serializable;
import java.util.Date;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String userId;
    protected String username;
    protected String password;
    protected String email;
    protected Date registrationDate;

    public User(String userId, String username, String password, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.registrationDate = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public abstract String getDashboardGreeting();

    @Override
    public String toString() {
        return "User{" +
               "userId='" + userId + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", registrationDate=" + registrationDate +
               '}';
    }
}