package com.github.kalimatas.learningjava.chap09;

import java.util.*;

public class Concurrency {
    public static void main(String[] args) {
        Producer producer = new Producer();
        new Thread(producer).start();

        Consumer consumer = new Consumer("first", producer);
        new Thread(consumer).start();

        Consumer consumerSecond = new Consumer("second", producer);
        new Thread(consumerSecond).start();
    }
}

class Consumer implements Runnable {
    private Producer producer;
    private String name;

    Consumer(String name, Producer producer) {
        this.name = name;
        this.producer = producer;
    }

    @Override
    public void run() {
        while (true) {
            String message = producer.getMessage();
            System.out.println(name + " got message: " + message);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                //
            }
        }
    }
}

class Producer implements Runnable {
    static final int MAXQUEUE = 5;
    private List<String> messages = new ArrayList<>();

    @Override
    public void run() {
        while (true) {
            putMessage();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //
            }
        }
    }

    private synchronized void putMessage() {
        while (messages.size() >= MAXQUEUE) {
            try {
                wait();
            } catch (InterruptedException e) {
                //
            }
        }

        messages.add(new java.util.Date().toString());
        notifyAll();
    }

    public synchronized String getMessage() {
        while (messages.size() == 0) {
            try {
                notifyAll();
                wait();
            } catch (InterruptedException e) {
                //
            }
        }

        String message = messages.remove(0);
        notifyAll();
        return message;
    }
}
