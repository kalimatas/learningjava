package com.github.kalimatas.learningjava.chap08;

import java.util.*;

public class Generics
{
    public static void main(String[] args) {
        List<Date> list = new ArrayList<>();
        Date date = new Date();
        list.add(date);

        Date first = list.get(0);
        System.out.println(first);

        List l = new ArrayList();
        l.add("string");

        Object o = new Object();
        Date d = new Date();
        o = d;

        //Date[] dates = new Date[10];
        //Object[] object = dates;
        //object[0] = "string";

        List l2 = new ArrayList<Date>();
        l2.add(new Date());
        l2.add("hello");

        Trap<Mouse> trap = new Trap<>();
        trap.snare(new Mouse());
        Mouse mouse = trap.release();

        List<Trap<Bear>> bearTraps = new ArrayList<>();

        List<?> anyList;
        List<Date> dateList = new ArrayList<Date>();
        List<String> stringList = new ArrayList<String>();

        anyList = dateList;
        anyList = stringList;

        List<? extends Date> dd = new ArrayList<TestDate>();
        List<? super TestDate> dd2 = new ArrayList<Date>();
    }
}

class Mouse {}
class Bear {}

class Trap<T> {
    T trapped;
    List<T> trappedList = new ArrayList<>();

    void snare(T trapped) {
        this.trapped = trapped;
        this.trappedList.add(trapped);
    }

    T release() {
        int trappedIndex = trappedList.indexOf(this.trapped);
        if (trappedIndex == -1) {
            return null;
        }

        this.trapped = null;
        return trappedList.get(trappedIndex);
    }

    void test(List<T> ...lists) {

    }
}

class TestDate extends Date {}
class TestInterface<T extends Iterable & Runnable> {}