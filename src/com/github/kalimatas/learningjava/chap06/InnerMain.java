package com.github.kalimatas.learningjava.chap06;

public class InnerMain {
    public static void main(String[] args) {
        ParentClass parent = new ParentClass("name");
        System.out.println(parent.getName());

        ParentClass.ChildClass child = new ParentClass.ChildClass("new child");
    }
}
