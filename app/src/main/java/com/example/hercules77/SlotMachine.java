package com.example.hercules77;

public class SlotMachine {
    private String id;
    private String namaMesin;
    private String deskripsi;
    private String tipe;
    private String gambarUrl;

    // Empty constructor for Firestore
    public SlotMachine() {}

    public SlotMachine(String id, String namaMesin, String deskripsi, String tipe, String gambarUrl) {
        this.id = id;
        this.namaMesin = namaMesin;
        this.deskripsi = deskripsi;
        this.tipe = tipe;
        this.gambarUrl = gambarUrl;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNamaMesin() { return namaMesin; }
    public void setNamaMesin(String namaMesin) { this.namaMesin = namaMesin; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }
    public String getGambarUrl() { return gambarUrl; }
    public void setGambarUrl(String gambarUrl) { this.gambarUrl = gambarUrl; }
}