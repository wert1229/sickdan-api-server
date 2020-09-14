package com.kdpark.sickdan.domain;

public enum RelationshipStatus {
    NONE(0), REQUESTING(1), REQUESTED(2), FRIEND(3), SELF(4);

    private int intVal;

    RelationshipStatus(int type) {
        this.intVal = type;
    }

    public int getInt() {
        return intVal;
    }
}
