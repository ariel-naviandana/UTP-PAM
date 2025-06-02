package com.example.hercules77;

import com.google.firebase.Timestamp;

public class History {
    public enum GameType { SLOT, COIN_FLIP }

    private GameType gameType;
    private Timestamp tanggalMenang;
    private boolean isVerified;
    private int jumlahMenang;

    // Constructor kosong diperlukan untuk Firestore
    public History() {}

    public History(GameType gameType, Timestamp tanggalMenang, boolean isVerified, int jumlahMenang) {
        this.gameType = gameType;
        this.tanggalMenang = tanggalMenang;
        this.isVerified = isVerified;
        this.jumlahMenang = jumlahMenang;
    }

    public GameType getGameType() { return gameType; }
    public void setGameType(GameType gameType) { this.gameType = gameType; }

    public Timestamp getTanggalMenang() { return tanggalMenang; }
    public void setTanggalMenang(Timestamp tanggalMenang) { this.tanggalMenang = tanggalMenang; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public int getJumlahMenang() { return jumlahMenang; }
    public void setJumlahMenang(int jumlahMenang) { this.jumlahMenang = jumlahMenang; }
}
