package com.github.kalimatas.learningjava.tmp;

public class Main {
    public static void main(String[] args) {
        Outside out = new Outside();
        out.print();
    }
}

class Outside {
    Inside in;

    Outside() {
        in = new Inside();
        in.value = 42;
        in.key = 44;

        Test t = new Test();
        t.test();
    }

    Outside(int value) {
        this(value, 55);
    }

    Outside(int value, int key) {
        in = new Inside();
        in.value = value;
    }

    void print() {
        System.out.println(in.value);
    }

    private class Inside {
        private int value;
        private int key;
    }
}
