package com.farmfrenzy.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameLoop {

    private static final int THREAD_COUNT = 5;
    private static final int TICK_SECONDS = 1;
    private static final int SHUTDOWN_WAIT_SECONDS = 2;

    private GameEngine engine;
    private ExecutorService executor;
    private volatile boolean running;

    public GameLoop(GameEngine engine) {
        this.engine = engine;
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        executor = Executors.newFixedThreadPool(THREAD_COUNT);
        executor.submit(() -> runAnimalLoop());
        executor.submit(() -> runProductLoop());
        executor.submit(() -> runTimerLoop());
    }

    public void stop() {
        running = false;
        if (executor == null) {
            return;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(SHUTDOWN_WAIT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void submit(Runnable task) {
        ExecutorService current = executor;
        if (current == null || current.isShutdown()) {
            return;
        }
        current.submit(task);
    }

    public static void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runAnimalLoop() {
        while (running) {
            engine.tickAnimals();
            waitOneTick();
        }
    }

    private void runProductLoop() {
        while (running) {
            engine.tickProducts();
            waitOneTick();
        }
    }

    private void runTimerLoop() {
        while (running) {
            waitOneTick();
            if (running) {
                engine.tickTimer();
            }
        }
    }

    private void waitOneTick() {
        try {
            Thread.sleep(TICK_SECONDS * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }
}
