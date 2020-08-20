package com.kdpark.sickdan.domain;

public enum MealCategory {
    NONE(0), BREAKFAST(1), LUNCH(2), DINNER(3), SNACK(4);

    private int intVal;

    MealCategory(int type) {
        this.intVal = type;
    }

    public int getInt() {
        return intVal;
    }
}

