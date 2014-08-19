package com.github.kalimatas.learningjava.ttt;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    private int pr = 42;

    public static void main(String[] args) {
        Main m = new Main();
        m.pr = 44;
        //System.out.println(m.pr);

        Map<Integer, String> codes = new TreeMap<>();
        codes.put(200, "Ok");
        codes.put(300, "Not Ok");
        codes.put(400, "Very bad");

        for (Integer key : codes.keySet()) {
            System.out.printf("%d ", key);
        }
        System.out.println();

        codes.values().remove("Not Ok");

        for (String value : codes.values()) {
            System.out.printf("%s ", value);
        }
        System.out.println();

        for (Map.Entry<Integer, String> entry : codes.entrySet()) {
            System.out.printf("%d: %s ", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }
}
