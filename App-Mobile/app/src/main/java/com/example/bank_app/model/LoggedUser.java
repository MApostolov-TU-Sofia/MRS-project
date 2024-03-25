package com.example.bank_app.model;

import android.app.Application;

public class LoggedUser extends Application {

    private String username;
    private String token;

    public LoggedUser() {
    }

    public LoggedUser(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void clearUser() {
        this.setUsername(null);
        this.setToken(null);
    }

    public void setUser(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
