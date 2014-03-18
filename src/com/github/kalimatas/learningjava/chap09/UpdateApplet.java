package com.github.kalimatas.learningjava.chap09;

import java.applet.Applet;

class UpdateApplet extends Applet
    implements Runnable
{
    Thread thread;
    boolean running;
    int updateInterval = 1000;

    @Override
    public void run() {
        while (running) {
            repaint();

            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                System.out.println("Interrupted...");
                return;
            }
        }
    }

    @Override
    public void start() {
        System.out.println("Starting...");
        if (!running) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void stop() {
        System.out.println("Stopping...");
        thread.interrupt();
        running = false;
    }
}
