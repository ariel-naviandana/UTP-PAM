package com.example.hercules77;

public class Tips {
    private String id;
    private String judul;
    private String konten;
    private String kategori;
    private String gambarIlustrasiUrl;

    public Tips() {}

    public Tips(String id, String judul, String konten, String kategori, String gambarIlustrasiUrl) {
        this.id = id;
        this.judul = judul;
        this.konten = konten;
        this.kategori = kategori;
        this.gambarIlustrasiUrl = gambarIlustrasiUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getKonten() { return konten; }
    public void setKonten(String konten) { this.konten = konten; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getGambarIlustrasiUrl() { return gambarIlustrasiUrl; }
    public void setGambarIlustrasiUrl(String gambarIlustrasiUrl) { this.gambarIlustrasiUrl = gambarIlustrasiUrl; }
}
