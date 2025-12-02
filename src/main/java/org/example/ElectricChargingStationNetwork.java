package org.example;

public class ElectricChargingStationNetwork {

    public static void main(String[] args) {

        System.out.println("=== Electric Charging Station Network ===");

        LocationManager lm = LocationManager.getInstance().clearLocations();
        ChargerManager cm = ChargerManager.getInstance().clearChargers();
        CustomerManager um = CustomerManager.getInstance().clearCustomers();


        Location loc1 = lm.createLocation("Vienna West Station")
                .setAddress("Mariahilfer Str. 130, 1070 Vienna");

        Location loc2 = lm.createLocation("Linz Center Garage")
                .setAddress("Landstraße 45, 4020 Linz");


        cm.createCharger("CHG-001", "AC", "available", loc1);
        cm.createCharger("CHG-002", "DC", "unavailable", loc1);
        cm.createCharger("CHG-003", "AC", "available", loc2);


        System.out.println("\nCurrent Locations (with chargers):");
        System.out.println(lm);



        System.out.println("\nUpdating CHG-002 status to available");
        cm.viewCharger("CHG-002").setStatus("available");

        System.out.println("\nLocations after charger update:");
        System.out.println(lm);



        System.out.println("\nDeleting charger CHG-003");
        cm.deleteCharger("CHG-003");

        System.out.println("\nLocations after charger deletion:");
        System.out.println(lm);



        Customer c1 = um.createCustomer("001")
                .setName("Alissa Strom")
                .setEmail("alissa@strom.at")
                .setCredit(50.0);

        Customer c2 = um.createCustomer("002")
                .setName("Eduard Power")
                .setEmail("eduard@power.at")
                .setCredit(0.0);

        System.out.println("\nCurrent Customers:");
        System.out.println(um);
    }
}
