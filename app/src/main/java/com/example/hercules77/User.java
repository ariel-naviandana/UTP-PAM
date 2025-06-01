package com.example.hercules77;

public class User {
    private int id;
    private String documentId;
    private String username;
    private String email;
    private String status;
    private String fotoProfilUrl;

    public User(String documentId, int id, String username, String email, String status, String fotoProfilUrl) {
        this.documentId = documentId;
        this.id = id;
        this.username = username;
        this.email = email;
        this.status = status;
        this.fotoProfilUrl = fotoProfilUrl;
    }

    public String getDocumentId() {
        return documentId;
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

    public void setStatus(String status) {
        this.status = status;
    }
}
