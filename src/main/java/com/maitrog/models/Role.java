package com.maitrog.models;

public enum Role {
    ADMIN,
    USER;

    public static int getValue(Role role) {
        switch (role) {
            case ADMIN -> {
                return 0;
            }
            case USER -> {
                return 1;
            }
            default -> {
                return 2;
            }
        }
    }
}
