package com.example.hercules77;

import java.util.Date;

public class Event {
    private String id;
    private String judulEvent;
    private String deskripsi;
    private Date tanggalMulai;
    private Date tanggalAkhir;
    private String bannerUrl;

    public Event() {}

    public Event(String id, String judulEvent, String deskripsi, Date tanggalMulai, Date tanggalAkhir, String bannerUrl){
        this.id = id;
        this.judulEvent = judulEvent;
        this.deskripsi = deskripsi;
        this.tanggalMulai = tanggalMulai;
        this.tanggalAkhir = tanggalAkhir;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudulEvent() {
        return judulEvent;
    }

    public void setJudulEvent(String judulEvent) {
        this.judulEvent = judulEvent;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Date getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(Date tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public Date getTanggalAkhir() {
        return tanggalAkhir;
    }

    public void setTanggalAkhir(Date tanggalAkhir) {
        this.tanggalAkhir = tanggalAkhir;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }
}
