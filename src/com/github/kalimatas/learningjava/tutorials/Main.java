package com.github.kalimatas.learningjava.tutorials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String... args) {
        List<Integer> l = new ArrayList<>(Arrays.asList(1, 2, 3));
        final int[] testNew = new int[] {42, 22, 64};
        final int[] testLiteral = {42, 22, 64};

        Arrays.sort(testNew);
        for (int i : testNew) {
            System.out.println(i);
        }

        System.out.println(new Child().key);
    }
}

class Parent {
    int key;

    {
        key = 42;
    }
}

class Child extends Parent {

}
