package com.example.hercules77;

public class History {
    public enum GameType {
        COIN_FLIP,
        SLOT
    }

    private GameType gameType;
    private String timestamp;
    private boolean isWin;
    private int amount;

    public History(GameType gameType, String timestamp, boolean isWin, int amount) {
        this.gameType = gameType;
        this.timestamp = timestamp;
        this.isWin = isWin;
        this.amount = amount;
    }

    public GameType getGameType() {
        return gameType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isWin() {
        return isWin;
    }

    public int getAmount() {
        return amount;
    }
}
