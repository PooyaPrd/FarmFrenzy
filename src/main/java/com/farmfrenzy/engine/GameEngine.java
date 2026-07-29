package com.farmfrenzy.engine;

import com.farmfrenzy.exception.InsufficientCoinsException;
import com.farmfrenzy.exception.OutofWaterException;
import com.farmfrenzy.exception.WarehouseFullException;
import com.farmfrenzy.model.Chicken;
import com.farmfrenzy.model.Egg;
import com.farmfrenzy.model.EggPowder;
import com.farmfrenzy.model.EggPowderMachine;
import com.farmfrenzy.model.Grass;
import com.farmfrenzy.model.Warehouse;
import com.farmfrenzy.model.WaterWell;
import com.farmfrenzy.model.base.Animal;
import com.farmfrenzy.model.base.Product;
import com.farmfrenzy.model.enums.AnimalState;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameEngine {

    public static final int CHICKEN_COST = 50;

    private ExecutorService executor;
    private int coins;
    private int levelTime;
    private List<Animal> animals;
    private List<Product> droppedProducts;
    private List<Grass> grassList;
    private Warehouse warehouse;
    private WaterWell waterWell;
    private EggPowderMachine eggPowderMachine;
    private volatile boolean isRunning;
    private Random random;
    private Runnable uiUpdate;

    public GameEngine(int startCoins) {
        this.coins = startCoins;
        this.levelTime = 0;
        this.animals = Collections.synchronizedList(new ArrayList<>());
        this.droppedProducts = Collections.synchronizedList(new ArrayList<>());
        this.grassList = Collections.synchronizedList(new ArrayList<>());
        this.warehouse = new Warehouse();
        this.waterWell = new WaterWell();
        this.eggPowderMachine = new EggPowderMachine("machine1", 2, 2);
        this.isRunning = false;
        this.random = new Random();
    }

    public synchronized int getCoins() {
        return coins;
    }

    public synchronized void setCoins(int coins) {
        this.coins = coins;
    }

    public synchronized int getLevelTime() {
        return levelTime;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public WaterWell getWaterWell() {
        return waterWell;
    }

    public EggPowderMachine getEggPowderMachine() {
        return eggPowderMachine;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setUiUpdate(Runnable uiUpdate) {
        this.uiUpdate = uiUpdate;
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

    public void startGame() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> updateAnimals());
        executor.submit(() -> updateProducts());
        executor.submit(() -> updateGameTimer());
    }

    public void stopGame() {
        isRunning = false;
        if (executor == null) {
            return;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void buyChicken() throws InsufficientCoinsException {
        if (coins < CHICKEN_COST) {
            throw new InsufficientCoinsException("You need " + CHICKEN_COST + " coins to buy a chicken");
        }
        coins -= CHICKEN_COST;
        int[] cell = randomBorderCell();
        animals.add(new Chicken(cell[0], cell[1]));
        refreshUi();
    }

    private int[] randomBorderCell() {
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

    public synchronized void plantGrass(int x, int y) throws OutofWaterException {
        if (!waterWell.useWater()) {
            throw new OutofWaterException("The well is empty, refill it first");
        }
        grassList.add(new Grass(x, y));
        refreshUi();
    }

    public synchronized void refillWell() throws InsufficientCoinsException {
        int cost = waterWell.getRefillCost();
        if (coins < cost) {
            throw new InsufficientCoinsException("You need " + cost + " coins to refill the well");
        }
        coins = coins - cost;
        waterWell.refill();
        refreshUi();
    }

    public synchronized void collectProduct(Product p) throws WarehouseFullException {
        if (warehouse.isFull() || warehouse.getFreeSpace() < p.getVolume()) {
            throw new WarehouseFullException("The warehouse is full");
        }
        if (!warehouse.addProduct(p)) {
            throw new WarehouseFullException("The warehouse is full");
        }
        droppedProducts.remove(p);
        refreshUi();
    }

    public synchronized void sellProductFromWarehouse(Product p) {
        if (warehouse.removeProduct(p)) {
            coins += p.getPrice();
            refreshUi();
        }
    }

    public synchronized void startEggPowderMachine() {
        Egg egg = findEggInWarehouse();
        if (egg == null) {
            return;
        }
        if (!eggPowderMachine.addEgg(egg)) {
            return;
        }
        warehouse.removeProduct(egg);
        eggPowderMachine.start();
        refreshUi();
        executor.submit(() -> runMachine());
    }

    private void runMachine() {
        sleepSeconds(eggPowderMachine.getProcessingTime());
        eggPowderMachine.process();
        List<EggPowder> ready = eggPowderMachine.takeOutputs();
        synchronized (droppedProducts) {
            droppedProducts.addAll(ready);
        }
        refreshUi();
    }

    private Egg findEggInWarehouse() {
        List<Product> products = warehouse.getProducts();
        for (Product p : products) {
            if (p instanceof Egg) {
                return (Egg) p;
            }
        }
        return null;
    }

    private void updateAnimals() {
        while (isRunning) {
            synchronized (animals) {
                List<Animal> dead = new ArrayList<>();
                for (Animal animal : animals) {
                    if (animal instanceof Chicken) {
                        updateChicken((Chicken) animal, dead);
                    } else {
                        animal.move();
                    }
                }
                animals.removeAll(dead);
            }
            refreshUi();
            sleepSeconds(1);
        }
    }

    private void updateChicken(Chicken chicken, List<Animal> dead) {
        chicken.loseHunger();
        if (chicken.isDead()) {
            dead.add(chicken);
            return;
        }
        if (chicken.getState() == AnimalState.HUNGRY) {
            chicken.setTarget(findClosestGrass(chicken.getX(), chicken.getY()));
        }
        chicken.move();
        Grass grass = findGrassAt(chicken.getX(), chicken.getY());
        if (grass != null) {
            chicken.eat(grass);
            grassList.remove(grass);
        }
        chicken.reduceEggCooldown();
        chicken.produceProduct();
        Egg egg = chicken.takeEgg();
        if (egg != null) {
            droppedProducts.add(egg);
        }
    }

    private void updateProducts() {
        while (isRunning) {
            synchronized (droppedProducts) {
                List<Product> expired = new ArrayList<>();
                for (Product p : droppedProducts) {
                    p.setLifespan(p.getLifespan() - 1);
                    if (p.getLifespan() <= 0) {
                        p.expire();
                        expired.add(p);
                    }
                }
                droppedProducts.removeAll(expired);
            }
            refreshUi();
            sleepSeconds(1);
        }
    }

    private void updateGameTimer() {
        while (isRunning) {
            sleepSeconds(1);
            synchronized (this) {
                levelTime++;
            }
            refreshUi();
        }
    }

    private Grass findGrassAt(int x, int y) {
        synchronized (grassList) {
            for (Grass grass : grassList) {
                if (grass.getX() == x && grass.getY() == y && !grass.isEaten()) {
                    return grass;
                }
            }
        }
        return null;
    }

    private Grass findClosestGrass(int x, int y) {
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

    private void refreshUi() {
        if (uiUpdate != null) {
            Platform.runLater(uiUpdate);
        }
    }

    private void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            isRunning = false;
        }
    }
}
