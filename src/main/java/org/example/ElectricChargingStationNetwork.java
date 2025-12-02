package org.example;

import java.time.LocalDate;

public class ElectricChargingStationNetwork {

    public static void main(String[] args) {

        System.out.println("=== Electric Charging Station Network ===");

        LocationManager lm = LocationManager.getInstance().clearLocations();
        ChargerManager cm = ChargerManager.getInstance().clearChargers();
        CustomerManager um = CustomerManager.getInstance().clearCustomers();
        InvoiceManager im = InvoiceManager.getInstance().clearInvoices();
        PricingManager pm = PricingManager.getInstance().clearPricing();


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


        im.createInvoice("1001", c1, LocalDate.of(2025, 1, 10), 12.50);
        im.createInvoice("1002", c1, LocalDate.of(2025, 2, 1), 8.40);
        im.createInvoice("2001", c2, LocalDate.of(2025, 2, 15), 15.90);

        System.out.println("\nCurrent Invoices:");
        System.out.println(im);



        Pricing pAC = pm.createPricing("AC", 0.10, 0.10);
        loc1.addPricing(pAC);

        Pricing pDC = pm.createPricing("DC", 0.40, 0.15);
        loc1.addPricing(pDC);

        System.out.println("\nPricing added at Vienna West Station:");
        for (Pricing p : loc1.getPricingList()) {
            System.out.println("  Mode: " + p.getMode()
                    + " | kWh: " + p.getPricePerKwh()
                    + " | min: " + p.getPricePerMinute());
        }



        // Alissa lädt
        System.out.println("\n=== Chargin - success ===");

        Charger charger = cm.viewCharger("CHG-001");

        int minutes = 20;
        double pricePerMinute = pAC.getPricePerMinute();
        double cost = minutes * pricePerMinute;

        if (!charger.getStatus().equals("available")) {
            System.out.println("Charger not available.");
        } else if (c1.getCredit() < cost) {
            System.out.println("Insufficient credit for Alissa.");
        } else {
            c1.setCredit(c1.getCredit() - cost);
            charger.setStatus("occupied");
            System.out.println("Alissa charged for " + minutes + " minutes.");
            System.out.println("Cost: " + cost + " EUR");
            System.out.println("Remaining credit: " + c1.getCredit());
            System.out.println("Charger status: " + charger.getStatus());
        }



        // Eduard lädt - fehlgeschlagen

        System.out.println("\n=== Charging - error ===");

        int minutesEduard = 60;
        double costEduard = minutesEduard * pricePerMinute;

        System.out.println("Eduard tries to charge...");

        if (!charger.getStatus().equals("available")) {
            System.out.println("ERROR: Charger not available.");
        } else if (c2.getCredit() < costEduard) {
            System.out.println("ERROR: Insufficient credit for Eduard");
        } else {
            System.out.println("Unexpected success (should not happen)");
        }

        System.out.println("\n=== END OF SIMULATION ===");

    }
}
