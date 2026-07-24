package com.farmfrenzy.model.base;

public abstract class Product {

    protected String id;
    protected String name;
    protected String imagePath;
    protected int x;
    protected int y;
    protected int price;
    protected int volume;
    protected int lifespan;

    public Product(String id, String name, String imagePath, int x, int y,
                   int price, int volume, int lifespan) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.x = x;
        this.y = y;
        this.price = price;
        this.volume = volume;
        this.lifespan = lifespan;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public void spawn() {
    }

    public void collect() {
    }

    public void expire() {
    }
}
