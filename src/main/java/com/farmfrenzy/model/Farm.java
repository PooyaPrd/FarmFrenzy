package com.farmfrenzy.model;

import com.farmfrenzy.model.base.Animal;
import com.farmfrenzy.model.base.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Farm {

    private List<Animal> animals;
    private List<Product> droppedProducts;
    private List<Grass> grassList;
    private Random random;

    public Farm() {
        this.animals = Collections.synchronizedList(new ArrayList<>());
        this.droppedProducts = Collections.synchronizedList(new ArrayList<>());
        this.grassList = Collections.synchronizedList(new ArrayList<>());
        this.random = new Random();
    }

    public List<Animal> getAnimals() {
        synchronized (animals) {
            return new ArrayList<>(animals);
        }
    }

    public List<Product> getDroppedProducts() {
        synchronized (droppedProducts) {
            return new ArrayList<>(droppedProducts);
        }
    }

    public List<Grass> getGrassList() {
        synchronized (grassList) {
            return new ArrayList<>(grassList);
        }
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void removeAnimals(List<Animal> removed) {
        animals.removeAll(removed);
    }

    public void addProduct(Product product) {
        droppedProducts.add(product);
    }

    public void addProducts(List<? extends Product> products) {
        synchronized (droppedProducts) {
            droppedProducts.addAll(products);
        }
    }

    public boolean removeProduct(Product product) {
        return droppedProducts.remove(product);
    }

    public void removeProducts(List<Product> removed) {
        droppedProducts.removeAll(removed);
    }

    public void addGrass(Grass grass) {
        grassList.add(grass);
    }

    public boolean removeGrass(Grass grass) {
        return grassList.remove(grass);
    }

    public Product findProductAt(int x, int y) {
        synchronized (droppedProducts) {
            for (Product product : droppedProducts) {
                if (product.getX() == x && product.getY() == y) {
                    return product;
                }
            }
        }
        return null;
    }

    public Grass findGrassAt(int x, int y) {
        synchronized (grassList) {
            for (Grass grass : grassList) {
                if (grass.getX() == x && grass.getY() == y && !grass.isEaten()) {
                    return grass;
                }
            }
        }
        return null;
    }

    public Grass findClosestGrass(int x, int y) {
        Grass closest = null;
        int bestDistance = Integer.MAX_VALUE;
        synchronized (grassList) {
            for (Grass grass : grassList) {
                if (grass.isEaten()) {
                    continue;
                }
                int distance = Math.abs(grass.getX() - x) + Math.abs(grass.getY() - y);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    closest = grass;
                }
            }
        }
        return closest;
    }

    public int countAliveChickens() {
        int count = 0;
        synchronized (animals) {
            for (Animal animal : animals) {
                if (animal instanceof Chicken && !((Chicken) animal).isDead()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int[] randomBorderCell() {
        int x = random.nextInt(Chicken.COLS);
        int y = random.nextInt(Chicken.ROWS);
        if (!Chicken.isBorderCell(x, y)) {
            if (random.nextBoolean()) {
                if (random.nextBoolean()) {
                    x = 0;
                } else {
                    x = Chicken.COLS - 1;
                }
            } else {
                if (random.nextBoolean()) {
                    y = 0;
                } else {
                    y = Chicken.ROWS - 1;
                }
            }
        }
        return new int[]{x, y};
    }
}
