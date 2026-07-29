package com.farmfrenzy.model;

public class LevelConfig {

    private int level;
    private int startingCoins;
    private int startingChickens;
    private int coinGoal;
    private int chickenGoal;
    private int powderGoal;
    private int goldTime;
    private int silverTime;

    private LevelConfig(int level, int startingCoins, int startingChickens, int coinGoal,
                        int chickenGoal, int powderGoal, int goldTime, int silverTime) {
        this.level = level;
        this.startingCoins = startingCoins;
        this.startingChickens = startingChickens;
        this.coinGoal = coinGoal;
        this.chickenGoal = chickenGoal;
        this.powderGoal = powderGoal;
        this.goldTime = goldTime;
        this.silverTime = silverTime;
    }

    public static LevelConfig forLevel(int level) {
        if (level == 2) {
            return new LevelConfig(2, 20, 2, 0, 3, 5, 150, 210);
        }
        if (level == 1) {
            return new LevelConfig(1, 60, 0, 150, 0, 0, 120, 240);
        }
        return new LevelConfig(level, 60, 0, 400, 0, 0, 120, 240);
    }

    public int getLevel() {
        return level;
    }

    public int getStartingCoins() {
        return startingCoins;
    }

    public int getStartingChickens() {
        return startingChickens;
    }

    public int getCoinGoal() {
        return coinGoal;
    }

    public int getChickenGoal() {
        return chickenGoal;
    }

    public int getPowderGoal() {
        return powderGoal;
    }

    public int getGoldTime() {
        return goldTime;
    }

    public int getSilverTime() {
        return silverTime;
    }

    public boolean isProductionLevel() {
        return chickenGoal > 0 || powderGoal > 0;
    }

    public String describeObjective() {
        if (isProductionLevel()) {
            return "Objective: " + chickenGoal + " chickens and " + powderGoal + " egg powders";
        }
        return "Objective: earn " + coinGoal + " coins";
    }
}
