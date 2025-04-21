package com.example.hercules77;

public class HomeItem {
    private String title;
    private int iconResId;
    private String description;

    public HomeItem(String title, int iconResId, String description) {
        this.title = title;
        this.iconResId = iconResId;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getDescription() {
        return description;
    }
}
