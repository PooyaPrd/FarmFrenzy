package com.farmfrenzy.engine;

import com.farmfrenzy.exception.InsufficientCoinsException;
import com.farmfrenzy.exception.OutofWaterException;
import com.farmfrenzy.exception.WarehouseFullException;
import com.farmfrenzy.model.Chicken;
import com.farmfrenzy.model.Egg;
import com.farmfrenzy.model.EggPowder;
import com.farmfrenzy.model.EggPowderMachine;
import com.farmfrenzy.model.Farm;
import com.farmfrenzy.model.Grass;
import com.farmfrenzy.model.LevelConfig;
import com.farmfrenzy.model.Warehouse;
import com.farmfrenzy.model.WaterWell;
import com.farmfrenzy.model.base.Animal;
import com.farmfrenzy.model.base.Product;
import com.farmfrenzy.model.enums.AnimalState;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    public static final int CHICKEN_COST = 50;
    public static final int FIRST_PLANT_COLUMN = 2;
    public static final int LAST_PLANT_COLUMN = 3;
    public static final int FIRST_PLANT_ROW = 1;
    public static final int LAST_PLANT_ROW = 3;

    private LevelConfig levelConfig;
    private Farm farm;
    private Warehouse warehouse;
    private WaterWell waterWell;
    private EggPowderMachine eggPowderMachine;
    private GameLoop gameLoop;
    private int coins;
    private int levelTime;
    private volatile GameUpdateListener updateListener;

    public GameEngine(int level) {
        this.levelConfig = LevelConfig.forLevel(level);
        this.farm = new Farm();
        this.warehouse = new Warehouse();
        this.waterWell = new WaterWell();
        this.eggPowderMachine = new EggPowderMachine("machine1", 0, 2);
        this.gameLoop = new GameLoop(this);
        this.levelTime = 0;
        setupLevel();
    }

    private void setupLevel() {
        coins = levelConfig.getStartingCoins();
        for (int i = 0; i < levelConfig.getStartingChickens(); i++) {
            int[] cell = farm.randomBorderCell();
            farm.addAnimal(new Chicken(cell[0], cell[1]));
        }
        waterWell.refill();
    }

    public LevelConfig getLevelConfig() {
        return levelConfig;
    }

    public int getLevel() {
        return levelConfig.getLevel();
    }

    public Farm getFarm() {
        return farm;
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

    public synchronized int getCoins() {
        return coins;
    }

    public synchronized void setCoins(int coins) {
        this.coins = coins;
    }

    public synchronized int getLevelTime() {
        return levelTime;
    }

    public boolean isRunning() {
        return gameLoop.isRunning();
    }

    public void setUpdateListener(GameUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public List<Animal> getAnimals() {
        return farm.getAnimals();
    }

    public List<Product> getDroppedProducts() {
        return farm.getDroppedProducts();
    }

    public List<Grass> getGrassList() {
        return farm.getGrassList();
    }

    public Product findProductAt(int x, int y) {
        return farm.findProductAt(x, y);
    }

    public void startGame() {
        gameLoop.start();
    }

    public void stopGame() {
        gameLoop.stop();
    }

    public static boolean isPlantCell(int x, int y) {
        return x >= FIRST_PLANT_COLUMN && x <= LAST_PLANT_COLUMN
                && y >= FIRST_PLANT_ROW && y <= LAST_PLANT_ROW;
    }

    public synchronized void buyChicken() throws InsufficientCoinsException {
        if (coins < CHICKEN_COST) {
            throw new InsufficientCoinsException("You need " + CHICKEN_COST + " coins to buy a chicken");
        }
        coins -= CHICKEN_COST;
        int[] cell = farm.randomBorderCell();
        farm.addAnimal(new Chicken(cell[0], cell[1]));
        notifyUpdate();
    }

    public synchronized boolean plantGrass(int x, int y) throws OutofWaterException {
        if (!isPlantCell(x, y)) {
            return false;
        }
        if (farm.findGrassAt(x, y) != null) {
            return false;
        }
        if (x == eggPowderMachine.getX() && y == eggPowderMachine.getY()) {
            return false;
        }
        if (!waterWell.useWater()) {
            throw new OutofWaterException("The well is empty, refill it first");
        }
        farm.addGrass(new Grass(x, y));
        notifyUpdate();
        return true;
    }

    public synchronized void refillWell() throws InsufficientCoinsException {
        int cost = waterWell.getRefillCost();
        if (coins < cost) {
            throw new InsufficientCoinsException("You need " + cost + " coins to refill the well");
        }
        coins = coins - cost;
        waterWell.refill();
        notifyUpdate();
    }

    public synchronized void collectProduct(Product p) throws WarehouseFullException {
        if (warehouse.isFull() || warehouse.getFreeSpace() < p.getVolume()) {
            throw new WarehouseFullException("The warehouse is full");
        }
        if (!warehouse.addProduct(p)) {
            throw new WarehouseFullException("The warehouse is full");
        }
        farm.removeProduct(p);
        notifyUpdate();
    }

    public synchronized void sellProductFromWarehouse(Product p) {
        if (warehouse.removeProduct(p)) {
            coins += p.getPrice();
            notifyUpdate();
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
        notifyUpdate();
        gameLoop.submit(() -> runMachine());
    }

    public int countAliveChickens() {
        return farm.countAliveChickens();
    }

    public int countEggPowderInWarehouse() {
        int count = 0;
        for (Product product : warehouse.getProducts()) {
            if (product instanceof EggPowder) {
                count++;
            }
        }
        return count;
    }

    void tickAnimals() {
        List<Animal> dead = new ArrayList<>();
        for (Animal animal : farm.getAnimals()) {
            if (animal instanceof Chicken) {
                updateChicken((Chicken) animal, dead);
            } else {
                animal.move();
            }
        }
        farm.removeAnimals(dead);
        notifyUpdate();
    }

    void tickProducts() {
        List<Product> expired = new ArrayList<>();
        for (Product product : farm.getDroppedProducts()) {
            product.setLifespan(product.getLifespan() - 1);
            if (product.getLifespan() <= 0) {
                product.expire();
                expired.add(product);
            }
        }
        farm.removeProducts(expired);
        notifyUpdate();
    }

    void tickTimer() {
        synchronized (this) {
            levelTime++;
        }
        notifyUpdate();
    }

    private void updateChicken(Chicken chicken, List<Animal> dead) {
        chicken.loseHunger();
        if (chicken.isDead()) {
            dead.add(chicken);
            return;
        }
        if (chicken.getState() == AnimalState.HUNGRY) {
            chicken.setTarget(farm.findClosestGrass(chicken.getX(), chicken.getY()));
        }
        chicken.move();
        Grass grass = farm.findGrassAt(chicken.getX(), chicken.getY());
        if (grass != null) {
            chicken.eat(grass);
            farm.removeGrass(grass);
        }
        chicken.reduceEggCooldown();
        chicken.produceProduct();
        Egg egg = chicken.takeEgg();
        if (egg != null) {
            farm.addProduct(egg);
        }
    }

    private void runMachine() {
        GameLoop.sleepSeconds(eggPowderMachine.getProcessingTime());
        eggPowderMachine.process();
        farm.addProducts(eggPowderMachine.takeOutputs());
        notifyUpdate();
    }

    private Egg findEggInWarehouse() {
        for (Product p : warehouse.getProducts()) {
            if (p instanceof Egg) {
                return (Egg) p;
            }
        }
        return null;
    }

    private void notifyUpdate() {
        GameUpdateListener listener = updateListener;
        if (listener != null) {
            listener.onGameUpdated();
        }
    }
}
