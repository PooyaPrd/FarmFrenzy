package com.farmfrenzy.model.base;

import com.farmfrenzy.model.enums.AnimalState;

public abstract class Animal {

    protected String id;
    protected String name;
    protected double speed;
    protected AnimalState state;
    protected String imagePath;
    protected int x;
    protected int y;

    public Animal(String id, String name, double speed, String imagePath, int x, int y) {
        this.id = id;
        this.name = name;
        this.speed = speed;
        this.state = AnimalState.IDLE;
        this.imagePath = imagePath;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public AnimalState getState() {
        return state;
    }

    public void setState(AnimalState state) {
        this.state = state;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public abstract void move();

    public void changeState(AnimalState state) {
        this.state = state;
    }
}
