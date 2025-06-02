package com.example.hercules77;

public class WinHistory {
    private String id;
    private String userId;
    private long amount;
    private String timestamp;       // simpan tanggal sebagai String hasil format dari Timestamp
    private String imageUrl;
    private String status;
    private boolean isVerified;

    public WinHistory() { }

    public WinHistory(String id, String userId, long amount, String timestamp, String imageUrl, String status, boolean isVerified) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.status = status;
        this.isVerified = isVerified;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}
