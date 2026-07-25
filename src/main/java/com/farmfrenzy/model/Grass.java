package com.farmfrenzy.model;

public class Grass {

    private int x;
    private int y;
    private int value;

    public Grass(int x, int y) {
        this(x, y, 40);
    }

    public Grass(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isEaten() {
        return value <= 0;
    }

    public int eat(int amount) {
        if (amount > value) {
            amount = value;
        }
        value = value - amount;
        return amount;
    }
}
