package com.farmfrenzy.model.base;

import com.farmfrenzy.model.enums.MachineState;

public abstract class Machine {

    protected String id;
    protected String name;
    protected MachineState state;
    protected int processingTime;
    protected int capacity;
    protected String imagePath;

    public Machine(String id, String name, int processingTime, int capacity, String imagePath) {
        this.id = id;
        this.name = name;
        this.state = MachineState.IDLE;
        this.processingTime = processingTime;
        this.capacity = capacity;
        this.imagePath = imagePath;
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

    public MachineState getState() {
        return state;
    }

    public void setState(MachineState state) {
        this.state = state;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void start() {
        changeState(MachineState.WORKING);
    }

    public void stop() {
        changeState(MachineState.IDLE);
    }

    public void changeState(MachineState state) {
        this.state = state;
    }

    public abstract void process();
}
