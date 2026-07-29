package com.farmfrenzy.model.base;

import com.farmfrenzy.model.enums.AnimalState;
import com.farmfrenzy.model.enums.ProductType;

public abstract class DomesticAnimal extends Animal {

    protected int hunger;
    protected int maxHunger;
    protected ProductType productType;

    public DomesticAnimal(String id, String name, double speed, String imagePath, int x, int y,
                          int maxHunger, ProductType productType) {
        super(id, name, speed, imagePath, x, y);
        this.hunger = maxHunger;
        this.maxHunger = maxHunger;
        this.productType = productType;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getMaxHunger() {
        return maxHunger;
    }

    public void setMaxHunger(int maxHunger) {
        this.maxHunger = maxHunger;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public void feed() {
        this.hunger = maxHunger;
        changeState(AnimalState.EATING);
    }

    public void decreaseHunger(int amount) {
        hunger -= amount;
        if (hunger < 0) {
            hunger = 0;
        }
    }

    public abstract void produceProduct();
}
