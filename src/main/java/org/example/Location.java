package org.example;

public class Location {

    private final String name;
    private String address;
    private int chargerCount;

    public Location(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Location setChargerCount(int count) {
        this.chargerCount = count;
        return this;
    }

    public int getChargerCount() {
        return chargerCount;
    }

    @Override
    public String toString() {
        return name + ": " + address + " (" + chargerCount + " chargers)";
    }
}
