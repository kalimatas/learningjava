package com.github.kalimatas.learningjava.tmp;

public class Test {
    private int priv = 0;
    int no = 0;
    protected int prot = 0;
    public int pub = 0;
    private int noValue;

    public Test() {
        noValue++;
        System.out.println(noValue);
    }

    void method() {
        int hello = 42;
    }

    static void test() {
        System.out.println("test method");
    }

    private static void testPrivate() {
        System.out.println("test private");
    }

    class Inside {
        void hello() {
            Test t = new Test();
            System.out.println(t.noValue);
        }
    }
}

class TestSub extends Test {
    TestSub() {
        Inside in = new Inside();
    }
}
