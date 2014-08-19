package com.github.kalimatas.learningjava.arraysort;

import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String... args) {
        int[] array = new int[1000000];

        Random rand = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(100) + 1;
        }

        long startTime = System.currentTimeMillis();
        Arrays.sort(array);

        long stopTime = System.currentTimeMillis();
        System.out.println(stopTime - startTime);
    }
}
