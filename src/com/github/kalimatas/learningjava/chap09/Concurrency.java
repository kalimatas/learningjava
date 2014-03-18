package com.github.kalimatas.learningjava.chap09;

public class Concurrency {
    public static void main(String[] args) {
        Animation anim = new Animation("test");
        ExtendThread extendThread = new ExtendThread();
        (new ThreadAdapter()).startRunning();
        (new ThreadAdapter()).startAnotherRunning();
    }
}

class Animation implements Runnable {
    Thread myThread;

    Animation(String name) {
        myThread = new Thread(this);
        myThread.start();
    }

    @Override
    public void run() {
        System.out.println("Inside Animation run");
    }
}

class ExtendThread extends Thread {

    ExtendThread() {
        start();
    }

    @Override
    public void run() {
        System.out.println("Inside ExtendThread run");
    }
}

class ThreadAdapter {
    public void startRunning() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doStuff();
            }
        });
        thread.start();
    }

    public void startAnotherRunning() {
        (new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    doStuff();
                } catch (InterruptedException e) {
                    //
                }
            }
        }).start();
    }

    private void doStuff() {
        System.out.println("Inside ThreadAdapter doStuff thread");
    }
}