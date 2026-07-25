package com.farmfrenzy.model;

import com.farmfrenzy.model.base.Product;

public class Egg extends Product {

    private static int counter = 1;

    public Egg(int x, int y) {
        super("egg" + counter, "Egg", "/images/egg.png", x, y, 15, 1, 10);
        counter++;
    }

    public boolean isExpired(int passedSeconds) {
        return passedSeconds >= lifespan;
    }
}
