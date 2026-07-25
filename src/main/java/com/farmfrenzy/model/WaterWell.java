package com.farmfrenzy.model;

public class WaterWell {

    private int currentWater;
    private int maxWater;
    private int refillCost;

    public WaterWell() {
        this.maxWater = 5;
        this.currentWater = 5;
        this.refillCost = 19;
    }

    public synchronized int getCurrentWater() {
        return currentWater;
    }

    public synchronized void setCurrentWater(int currentWater) {
        this.currentWater = currentWater;
    }

    public int getMaxWater() {
        return maxWater;
    }

    public void setMaxWater(int maxWater) {
        this.maxWater = maxWater;
    }

    public int getRefillCost() {
        return refillCost;
    }

    public void setRefillCost(int refillCost) {
        this.refillCost = refillCost;
    }

    public synchronized void refill() {
        currentWater = maxWater;
    }

    public synchronized boolean useWater() {
        if (currentWater <= 0) {
            return false;
        }
        currentWater--;
        return true;
    }

    public synchronized boolean isEmpty() {
        return currentWater <= 0;
    }
}
