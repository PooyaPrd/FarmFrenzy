package com.farmfrenzy.model;

public class PlayerProgress {

    private int userId;
    private int level;
    private int coins;

    public PlayerProgress(int userId, int level, int coins) {
        this.userId = userId;
        this.level = level;
        this.coins = coins;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
