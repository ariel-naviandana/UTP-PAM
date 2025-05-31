package com.example.hercules77;

public class WinHistory {
    private String id, idUser, tanggalMenang, buktiGambarUrl;
    private int jumlahMenang;

    public WinHistory(String id, String idUser, int jumlahMenang, String tanggalMenang, String buktiGambarUrl) {
        this.id = id;
        this.idUser = idUser;
        this.jumlahMenang = jumlahMenang;
        this.tanggalMenang = tanggalMenang;
        this.buktiGambarUrl = buktiGambarUrl;
    }

    public String getId() { return id; }
    public String getIdUser() { return idUser; }
    public int getJumlahMenang() { return jumlahMenang; }
    public String getTanggalMenang() { return tanggalMenang; }
    public String getBuktiGambarUrl() { return buktiGambarUrl; }
}

