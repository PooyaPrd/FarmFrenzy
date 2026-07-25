package com.farmfrenzy.model;

import com.farmfrenzy.model.base.DomesticAnimal;
import com.farmfrenzy.model.enums.AnimalState;
import com.farmfrenzy.model.enums.ProductType;

import java.util.Random;

public class Chicken extends DomesticAnimal {

    public static final int ROWS = 5;
    public static final int COLS = 6;

    private static int counter = 1;

    private Grass target;
    private Egg lastEgg;
    private Random random;

    public Chicken(int x, int y) {
        super("chicken" + counter, "Chicken", 1.0, "/images/chicken.png", x, y, 100, ProductType.EGG);
        counter++;
        this.random = new Random();
    }

    public Grass getTarget() {
        return target;
    }

    public void setTarget(Grass target) {
        this.target = target;
    }

    public Egg getLastEgg() {
        return lastEgg;
    }

    @Override
    public void move() {
        if (state == AnimalState.DEAD || state == AnimalState.EATING) {
            return;
        }
        if (target != null && !target.isEaten()) {
            walkTo(target.getX(), target.getY());
            if (x == target.getX() && y == target.getY()) {
                int eaten = target.eat(hunger);
                hunger = hunger - eaten;
                if (hunger < 0) {
                    hunger = 0;
                }
                target = null;
                changeState(AnimalState.EATING);
                return;
            }
        } else {
            walkRandom();
        }
        changeState(AnimalState.WALKING);
    }

    private void walkTo(int targetX, int targetY) {
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

    private void walkRandom() {
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
        if (newX >= 0 && newX < COLS) {
            x = newX;
        }
        if (newY >= 0 && newY < ROWS) {
            y = newY;
        }
    }

    @Override
    public void produceProduct() {
        if (state == AnimalState.DEAD) {
            return;
        }
        lastEgg = new Egg(x, y);
        changeState(AnimalState.PRODUCING);
    }

    public Egg takeEgg() {
        Egg egg = lastEgg;
        lastEgg = null;
        return egg;
    }
}
