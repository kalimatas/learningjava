package com.github.kalimatas.learningjava.chap10;

import java.util.Scanner;
import java.util.regex.*;

public class Text {
    public static void main(String[] args) {
        String one = "first string";
        System.out.println(one.charAt(2));

        byte[] bytes = new byte[] {(byte)42, (byte)13};
        String second = new String(bytes);

        System.out.println(second);

        String foo = "Foo";
        String foo2 = new String(new char[]{'f', 'o', 'o'});

        System.out.println(foo == foo2);
        System.out.println(foo.equals(foo2));
        System.out.println(foo.equalsIgnoreCase(foo2));

        StringBuilder sb = new StringBuilder("Word");
        sb.append(" new word");
        System.out.println(sb);

        int bin = Integer.parseInt("110", 2);
        System.out.println(bin);

        int hex = new Scanner("A").nextInt(16);
        System.out.println(hex);

        String text = "A horse is a horse, of course of course...";
        String pattern = "horse|course";

        Matcher matcher = Pattern.compile(pattern).matcher(text);
        while (matcher.find()) {
            System.out.printf("Matched: '%s' at position %d\n", matcher.group(), matcher.start());
        }
    }
}
