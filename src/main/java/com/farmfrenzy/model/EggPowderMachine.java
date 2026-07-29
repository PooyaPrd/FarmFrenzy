package com.farmfrenzy.model;

import com.farmfrenzy.model.base.Machine;
import com.farmfrenzy.model.enums.MachineState;

import java.util.ArrayList;
import java.util.List;

public class EggPowderMachine extends Machine {

    private int x;
    private int y;
    private List<Egg> inputs;
    private List<EggPowder> outputs;

    public EggPowderMachine(String id, int x, int y) {
        super(id, "Egg Powder Machine", 5, 1, "/images/egg_powder_machine.jpg");
        this.x = x;
        this.y = y;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public synchronized boolean addEgg(Egg egg) {
        if (inputs.size() >= capacity) {
            return false;
        }
        inputs.add(egg);
        return true;
    }

    public synchronized List<EggPowder> takeOutputs() {
        List<EggPowder> ready = new ArrayList<>(outputs);
        outputs.clear();
        return ready;
    }

    public synchronized int getInputCount() {
        return inputs.size();
    }

    @Override
    public synchronized void process() {
        if (inputs.isEmpty()) {
            changeState(MachineState.IDLE);
            return;
        }
        changeState(MachineState.WORKING);
        inputs.remove(0);
        outputs.add(new EggPowder(x, y));
        changeState(MachineState.IDLE);
    }
}
