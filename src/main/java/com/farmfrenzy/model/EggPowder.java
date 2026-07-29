package com.farmfrenzy.model;

import com.farmfrenzy.model.base.Product;

public class EggPowder extends Product {

    private static int counter = 1;

    public EggPowder(int x, int y) {
        super("powder" + counter, "Egg Powder", "/images/egg_powder.png", x, y, 30, 2, 15);
        counter++;
    }

    public boolean isExpired(int passedSeconds) {
        return passedSeconds >= lifespan;
    }
}
