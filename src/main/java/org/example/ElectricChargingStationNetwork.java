package org.example;

public class ElectricChargingStationNetwork {

    public static void main(String[] args) {

        System.out.println("=== Electric Charging Station Network ===");

        LocationManager lm = LocationManager.getInstance().clearLocations();

        lm.createLocation("Vienna West Station")
                .setAddress("Mariahilfer Str. 120, 1070 Vienna")
                .setChargerCount(4);

        lm.createLocation("Linz Center Garage")
                .setAddress("Landstraße 45, 4020 Linz")
                .setChargerCount(2);

        System.out.println("\nCurrent Locations:");
        System.out.println(lm);
    }
}
