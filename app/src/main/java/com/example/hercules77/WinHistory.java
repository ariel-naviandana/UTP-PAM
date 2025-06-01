package com.example.hercules77;

public class WinHistory {
    private String id;
    private String idUser;
    private int jumlahMenang;
    private String tanggalMenang;
    private String buktiGambarUrl;
    private String status;
    boolean isVerified;

    public WinHistory() {
        // Required by Firebase
    }

    public WinHistory(String id, String idUser, int jumlahMenang, String tanggalMenang, String buktiGambarUrl, String status, boolean isVerified) {
        this.id = id;
        this.idUser = idUser;
        this.jumlahMenang = jumlahMenang;
        this.tanggalMenang = tanggalMenang;
        this.buktiGambarUrl = buktiGambarUrl;
        this.status = status;
        this.isVerified = isVerified;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public int getJumlahMenang() { return jumlahMenang; }
    public void setJumlahMenang(int jumlahMenang) { this.jumlahMenang = jumlahMenang; }

    public String getTanggalMenang() { return tanggalMenang; }
    public void setTanggalMenang(String tanggalMenang) { this.tanggalMenang = tanggalMenang; }

    public String getBuktiGambarUrl() { return buktiGambarUrl; }
    public void setBuktiGambarUrl(String buktiGambarUrl) { this.buktiGambarUrl = buktiGambarUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }
}
