package com.farmfrenzy.model;

import com.farmfrenzy.model.base.DomesticAnimal;
import com.farmfrenzy.model.enums.AnimalState;
import com.farmfrenzy.model.enums.ProductType;

import java.util.Random;

public class Chicken extends DomesticAnimal {

    public static final int ROWS = 5;
    public static final int COLS = 6;
    public static final int MAX_HUNGER = 100;
    public static final int HUNGER_STEP = 5;
    public static final int HUNGRY_LIMIT = 50;
    public static final int EGG_COOLDOWN = 8;

    private static int counter = 1;

    private Grass target;
    private Egg lastEgg;
    private boolean hasEaten;
    private int eggCooldown;
    private Random random;

    public Chicken(int x, int y) {
        super("chicken" + counter, "Chicken", 1.0, "/images/chicken.png", x, y, MAX_HUNGER, ProductType.EGG);
        counter++;
        this.hasEaten = false;
        this.eggCooldown = 0;
        this.random = new Random();
    }

    public Grass getTarget() {
        return target;
    }

    public void setTarget(Grass target) {
        this.target = target;
    }

    public boolean hasEaten() {
        return hasEaten;
    }

    public int getEggCooldown() {
        return eggCooldown;
    }

    public Egg getLastEgg() {
        return lastEgg;
    }

    public boolean isDead() {
        return state == AnimalState.DEAD;
    }

    public void loseHunger() {
        decreaseHunger(HUNGER_STEP);
        updateState();
    }

    private void updateState() {
        if (hunger <= 0) {
            changeState(AnimalState.DEAD);
        } else if (hunger <= HUNGRY_LIMIT) {
            changeState(AnimalState.HUNGRY);
        } else {
            changeState(AnimalState.IDLE);
        }
    }

    @Override
    public void move() {
        if (isDead()) {
            return;
        }
        if (state == AnimalState.HUNGRY) {
            if (target != null) {
                stepTowards(target.getX(), target.getY());
            }
            return;
        }
        stepOnBorder();
        changeState(AnimalState.WALKING);
    }

    private void stepTowards(int targetX, int targetY) {
        if (x < targetX) {
            x++;
        } else if (x > targetX) {
            x--;
        } else if (y < targetY) {
            y++;
        } else if (y > targetY) {
            y--;
        }
    }

    private void stepOnBorder() {
        int direction = random.nextInt(4);
        int newX = x;
        int newY = y;
        if (direction == 0) {
            newY--;
        } else if (direction == 1) {
            newY++;
        } else if (direction == 2) {
            newX--;
        } else {
            newX++;
        }
        if (isBorderCell(newX, newY)) {
            x = newX;
            y = newY;
        }
    }

    public static boolean isBorderCell(int x, int y) {
        if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
            return false;
        }
        return x == 0 || x == COLS - 1 || y == 0 || y == ROWS - 1;
    }

    public void eat(Grass grass) {
        grass.eat(grass.getValue());
        feed();
        hasEaten = true;
        target = null;
    }

    public void reduceEggCooldown() {
        if (eggCooldown > 0) {
            eggCooldown--;
        }
    }

    public boolean canProduceEgg() {
        return !isDead() && hasEaten && eggCooldown <= 0;
    }

    @Override
    public void produceProduct() {
        if (!canProduceEgg()) {
            return;
        }
        lastEgg = new Egg(x, y);
        hasEaten = false;
        eggCooldown = EGG_COOLDOWN;
        changeState(AnimalState.PRODUCING);
    }

    public Egg takeEgg() {
        Egg egg = lastEgg;
        lastEgg = null;
        return egg;
    }
}
