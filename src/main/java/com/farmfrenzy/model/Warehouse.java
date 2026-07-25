package com.farmfrenzy.model;

import com.farmfrenzy.model.base.Product;

import java.util.ArrayList;
import java.util.List;

public class Warehouse {

    private int capacity;
    private int currentVolume;
    private List<Product> products;

    public Warehouse() {
        this.capacity = 50;
        this.currentVolume = 0;
        this.products = new ArrayList<>();
    }

    public synchronized int getCapacity() {
        return capacity;
    }

    public synchronized void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public synchronized int getCurrentVolume() {
        return currentVolume;
    }

    public synchronized List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public synchronized boolean addProduct(Product p) {
        if (p == null) {
            return false;
        }
        if (currentVolume + p.getVolume() > capacity) {
            return false;
        }
        products.add(p);
        currentVolume = currentVolume + p.getVolume();
        return true;
    }

    public synchronized boolean removeProduct(Product p) {
        boolean removed = products.remove(p);
        if (removed) {
            currentVolume = currentVolume - p.getVolume();
        }
        return removed;
    }

    public synchronized boolean isFull() {
        return currentVolume >= capacity;
    }

    public synchronized int getFreeSpace() {
        return capacity - currentVolume;
    }

    public synchronized void clear() {
        products.clear();
        currentVolume = 0;
    }
}
