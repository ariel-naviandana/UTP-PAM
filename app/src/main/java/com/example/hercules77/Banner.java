package com.example.hercules77;

public class Banner {
    private String id;
    private String judulBanner;
    private String deskripsi;
    private String linkPromo;
    private String gambarBannerUrl;

    // Empty constructor for Firestore
    public Banner() {}

    public Banner(String id, String judulBanner, String deskripsi, String linkPromo, String gambarBannerUrl) {
        this.id = id;
        this.judulBanner = judulBanner;
        this.deskripsi = deskripsi;
        this.linkPromo = linkPromo;
        this.gambarBannerUrl = gambarBannerUrl;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJudulBanner() { return judulBanner; }
    public void setJudulBanner(String judulBanner) { this.judulBanner = judulBanner; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public String getLinkPromo() { return linkPromo; }
    public void setLinkPromo(String linkPromo) { this.linkPromo = linkPromo; }
    public String getGambarBannerUrl() { return gambarBannerUrl; }
    public void setGambarBannerUrl(String gambarBannerUrl) { this.gambarBannerUrl = gambarBannerUrl; }
}