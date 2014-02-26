package com.github.kalimatas.learningjava.chap06;

public class ParentClass {
    String name;

    ParentClass(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    static class ChildClass {
        String name;

        ChildClass(String name) {
            this.name = name;
        }
    }
}
