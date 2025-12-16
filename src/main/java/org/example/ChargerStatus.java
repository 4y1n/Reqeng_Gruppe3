package org.example;

public enum ChargerStatus {
    AVAILABLE("available"),
    OCCUPIED("occupied"),
    OUT_OF_ORDER("out of order"),
    UNAVAILABLE("unavailable");

    private final String text;

    ChargerStatus(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ChargerStatus fromString(String s) {
        if (s == null) return null;
        String v = s.trim().toLowerCase();
        switch (v) {
            case "available":
                return AVAILABLE;
            case "occupied":
                return OCCUPIED;
            case "unavailable":
                return UNAVAILABLE;
            case "out of order":
            case "out_of_order":
            case "outoforder":
            case "outofservice":
                return OUT_OF_ORDER;
            default:
                return null;
        }
    }
}

