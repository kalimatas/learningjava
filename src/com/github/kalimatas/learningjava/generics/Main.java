package com.github.kalimatas.learningjava.generics;

public class Main {
    public static void main(String[] args) {
        Holder3<Automobile> h3 = new Holder3<>(new Automobile());
        TwoTuple<String, Integer> twoTuple = new TwoTuple<>("hello", 42);

        LinkedStack<String> stack = new LinkedStack<>();

        for (String s : "hello from string".split(" ")) {
            stack.push(s);
        }

        String str;
        while ((str = stack.pop()) != null) {
            System.out.println(str);
        }
    }
}

class Automobile {}

class Holder3<T> {
    private T a;
    public Holder3(T a) { this.a = a; }
    public void set(T a) { this.a = a; }
    public T get() { return this.a; }
}

class TwoTuple<A, B> {
    public final A first;
    public final B second;

    public TwoTuple(A a, B b) { first = a; second = b; }
    public String toString() { return "(" + first + "," + second + ")"; }
}

class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
    public final C third;

    public ThreeTuple(A a, B b, C c) {
        super(a, b);
        third = c;
    }
    public String toString() { return "(" + first + "," + second + ", " + third + ")"; }
}

class LinkedStack<T> {
    private class Node<U> {
        U item;
        Node<U> next;

        Node() { item = null; next = null; }
        Node(U item, Node<U> next) {
            this.item = item;
            this.next = next;
        }
        boolean end() { return item == null && next == null; }
    }

    private Node<T> top = new Node<>();

    public void push(T item) {
        top = new Node<>(item, top);
    }

    public T pop() {
        T result = top.item;
        if (!top.end()) {
            top = top.next;
        }

        return result;
    }
}
