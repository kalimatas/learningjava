package com.github.kalimatas.learningjava.chap09;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorThreadPool {
    public static void main(String[] args) {
        List<Runnable> runnables = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            final int y = i;
            runnables.add(new Runnable() {
                @Override
                public void run() {
                    System.out.println("runnable #" + y);
                }
            });
        }

        Executor executor = Executors.newFixedThreadPool(10);
        for (Runnable task : runnables) {
            executor.execute(task);
        }
    }
}
