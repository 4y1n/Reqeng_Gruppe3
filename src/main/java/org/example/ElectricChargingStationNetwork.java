package org.example;

public class ElectricChargingStationNetwork {

    public static void main(String[] args) {

        System.out.println("=== Electric Charging Station Network ===");



        LocationManager lm = LocationManager.getInstance().clearLocations();

        Location loc1 = lm.createLocation("Vienna West Station")
                .setAddress("Mariahilfer Str. 130, 1070 Vienna")
                .setChargerCount(0);

        Location loc2 = lm.createLocation("Linz Center Garage")
                .setAddress("Landstraße 45, 4020 Linz")
                .setChargerCount(0);

        System.out.println("\ncurrent locations:");
        System.out.println(lm);





        ChargerManager cm = ChargerManager.getInstance().clearChargers();

        cm.createCharger("CHG-001", "AC", "available", loc1);
        cm.createCharger("CHG-002", "DC", "unavailable", loc1);
        cm.createCharger("CHG-003", "AC", "available", loc2);

        System.out.println("\ncurrent Chargers:");
        System.out.println(cm);



        System.out.println("\nupdating CHG-002 status to available");

        cm.viewCharger("CHG-002").setStatus("available");

        System.out.println("updated Chargers:");
        System.out.println(cm);

        System.out.println(lm);



        System.out.println("\ndeleting charger CHG-003");

        cm.deleteCharger("CHG-003");

        System.out.println("chargers after deletion:");
        System.out.println(cm);

        System.out.println(lm);





        CustomerManager um = CustomerManager.getInstance().clearCustomers();

        Customer c1 = um.createCustomer("001");
        c1.setName("Alissa Strom");
        c1.setEmail("alissa@strom.at");
        c1.setAccountBalance(50.0);

        Customer c2 = um.createCustomer("002");
        c2.setName("Eduard Power");
        c2.setEmail("eduard@power.at");
        c2.setAccountBalance(0.0);

        System.out.println("\ncurrent customers:");
        System.out.println(um);
    }
}

