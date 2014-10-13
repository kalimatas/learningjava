package com.github.kalimatas.learningjava;

public enum Enumindex {
    MOVE_LEFT(1),
    MOVE_RIGHT(2),
    MOVE_UP(3),
    MOVE_DOWN(4),
    ACTION_COUNT(5);

    private int actionIndex;

    private Enumindex(final int actionIndex) {
        this.actionIndex = actionIndex;
    }

    public static Enumindex getAction(int actionIndex) {
        for (Enumindex action : values()) {
            if (action.actionIndex == actionIndex) {
                return action;
            }
        }
        throw new IllegalArgumentException();
    }
}
