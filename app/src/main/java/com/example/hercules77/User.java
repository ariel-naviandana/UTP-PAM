package com.example.hercules77;

public class User {
    int id;
    String username, email, status, fotoProfilUrl;

    public User(int id, String username, String email, String status, String fotoProfilUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.status = status;
        this.fotoProfilUrl = fotoProfilUrl;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getFotoProfilUrl() {
        return fotoProfilUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFotoProfilUrl(String fotoProfilUrl) {
        this.fotoProfilUrl = fotoProfilUrl;
    }
}
