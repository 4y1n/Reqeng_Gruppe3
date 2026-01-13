package org.example;

public class ElectricChargingStationNetwork {

    public static void main(String[] args) {

        System.out.println("=== Electric Charging Station Network ===");

        LocationManager lm = LocationManager.getInstance().clearLocations();
        ChargersManager cm = ChargersManager.getInstance().clearChargers();
        CustomerManager um = CustomerManager.getInstance().clearCustomers();
        InvoiceManager.getInstance().clearInvoices();
        PricingManager pm = PricingManager.getInstance().clearPricing();



        Location loc1 = lm.createLocation("Vienna West Station").setAddress("Mariahilfer Str. 120, 1070 Vienna");
        Location loc2 = lm.createLocation("Linz Center Garage").setAddress("Landstraße 45, 4020 Linz");
        Location loc3 = lm.createLocation("Graz Main Square").setAddress("Herrengasse 3, 8010 Graz");
        Location loc4 = lm.createLocation("Salzburg Airport").setAddress("Innsbrucker Bundesstraße 95, 5020 Salzburg");
        Location loc5 = lm.createLocation("St. Pölten Forum Garage").setAddress("Kremser Gasse 22, 3100 St. Pölten");
        Location loc6 = lm.createLocation("Innsbruck City Mall").setAddress("Museumstraße 38, 6020 Innsbruck");
        Location loc7 = lm.createLocation("Klagenfurt Lakeside Park").setAddress("Lakeside B02, 9020 Klagenfurt");
        Location loc8 = lm.createLocation("Villach Süd").setAddress("Maria-Gailer-Straße 42, 9500 Villach");
        Location loc9 = lm.createLocation("Wiener Neustadt Center").setAddress("Stadionstraße 13, 2700 Wr. Neustadt");
        Location loc10 = lm.createLocation("Eisenstadt Downtown").setAddress("Hauptstraße 4, 7000 Eisenstadt");


        cm.createCharger("CHG-001", "AC", "available", loc1);
        cm.createCharger("CHG-002", "DC", "out of order", loc1);

        cm.createCharger("CHG-003", "AC", "available", loc2);
        cm.createCharger("CHG-004", "DC", "available", loc2);
        cm.createCharger("CHG-005", "AC", "out of order", loc2);

        cm.createCharger("CHG-006", "AC", "available", loc3);

        cm.createCharger("CHG-007", "DC", "out of order", loc4);
        cm.createCharger("CHG-008", "AC", "available", loc4);

        cm.createCharger("CHG-009", "AC", "available", loc5);
        cm.createCharger("CHG-010", "DC", "out of order", loc5);

        cm.createCharger("CHG-011", "AC", "available", loc6);

        cm.createCharger("CHG-012", "DC", "available", loc7);
        cm.createCharger("CHG-013", "AC", "out of order", loc7);

        cm.createCharger("CHG-014", "AC", "available", loc8);
        cm.createCharger("CHG-015", "DC", "out of order", loc8);

        cm.createCharger("CHG-016", "AC", "available", loc9);

        cm.createCharger("CHG-017", "DC", "out of order", loc10);
        cm.createCharger("CHG-018", "AC", "available", loc10);

        System.out.println("\nLocations and chargers initialized:");
        System.out.println(lm);

        System.out.println("\nUpdating CHG-002 status to available...");
        cm.viewCharger("CHG-002").setStatus("available");

        System.out.println("\nLocations after charger update:");
        System.out.println(lm);



        System.out.println("\nDeleting charger CHG-003...");
        cm.deleteCharger("CHG-003");

        System.out.println("\nLocations after charger deletion:");
        System.out.println(lm);




        loc1.setPricing("AC", 0.10, 0.05);
        loc1.setPricing("DC", 0.20, 0.10);

        loc2.setPricing("AC", 0.12, 0.06);
        loc2.setPricing("DC", 0.22, 0.11);

        loc3.setPricing("AC", 0.11, 0.05);

        loc4.setPricing("AC", 0.13, 0.07);
        loc4.setPricing("DC", 0.25, 0.12);

        loc5.setPricing("AC", 0.09, 0.04);
        loc5.setPricing("DC", 0.18, 0.09);

        loc6.setPricing("AC", 0.14, 0.06);

        loc7.setPricing("AC", 0.15, 0.07);
        loc7.setPricing("DC", 0.28, 0.14);

        loc8.setPricing("AC", 0.10, 0.05);
        loc8.setPricing("DC", 0.20, 0.10);

        loc9.setPricing("AC", 0.08, 0.04);

        loc10.setPricing("AC", 0.18, 0.09);
        loc10.setPricing("DC", 0.30, 0.15);


        System.out.println("\nAdd unique pricing for each location...");

        pm.displayLocationsWithPricing(lm);

        System.out.println("\nUpdate pricing for  Vienna West Station...");
        loc1.updatePricing("AC", 0.14, 0.07);
        loc1.updatePricing("DC", 0.25, 0.12);

        System.out.println("\nLocations after pricing update:");
        pm.displayLocationsWithPricing(lm);

        System.out.println("Adding customers...\n");

        Customer c1 = um.createCustomer("001")
                .setName("Alissa Strom")
                .setEmail("alissa@strom.at")
                .setCredit(50.0);

        Customer c2 = um.createCustomer("002")
                .setName("Eduard Power")
                .setEmail("eduard@power.at")
                .setCredit(0.0);

        Customer c3 = um.createCustomer("003")
                .setName("Jasmin Green")
                .setEmail("jasmin@green.at")
                .setCredit(30.0);

        System.out.println(um);



        System.out.println("\n=== Charging - Alissa ===");
        Chargers alissaCharger = cm.viewCharger("CHG-001");
        int alissaMinutes = 20;
        Pricing alissaPricing = loc1.getPricingForMode(alissaCharger.getType());
        double alissaCost = Math.round((alissaMinutes * alissaPricing.getPricePerMinute()) * 100.0) / 100.0;

        System.out.println("Alissa tries to charge at charger: " + alissaCharger.getId());
        System.out.println("Charging mode: " + alissaCharger.getType());
        if (!alissaCharger.getStatus().equals("available")) {
            System.out.println("Charger not available.");
        } else if (c1.getCredit() < alissaCost) {
            System.out.println("Insufficient credit for Alissa.");
        } else {
            c1.setCredit(c1.getCredit() - alissaCost);
            alissaCharger.setStatus("occupied");
            System.out.println("Alissa charged for " + alissaMinutes + " minutes.");
            System.out.println("Cost: " + alissaCost + " EUR");
            System.out.println("Remaining credit: " + c1.getCredit());
            System.out.println("Charger status: " + alissaCharger.getStatus());
            System.out.println();


            InvoiceManager im = InvoiceManager.getInstance();
            double alissaEnergyKwh = Math.round((alissaCost / alissaPricing.getPricePerKwh()) * 100.0) / 100.0;
            long alissaChargingMinutes = Math.round(alissaCost / alissaPricing.getPricePerMinute());
            Invoice alissaInvoice = im.createInvoice(c1.getId(), alissaCharger.getId(), alissaEnergyKwh, alissaChargingMinutes);
            System.out.println("Invoice created for Alissa:\n" + alissaInvoice.toPrint());
        }


        System.out.println("\n=== Charging - Eduard ===");
        Chargers eduardCharger = cm.viewCharger("CHG-002");
        int eduardMinutes = 60;
        Pricing eduardPricing = loc1.getPricingForMode(eduardCharger.getType());
        double eduardCost = Math.round((eduardMinutes * eduardPricing.getPricePerMinute()) * 100.0) / 100.0;

        System.out.println("Eduard tries to charge at charger: " + eduardCharger.getId());
        if (!eduardCharger.getStatus().equals("available")) {
            System.out.println("ERROR: Charger not available.");
        } else if (c2.getCredit() < eduardCost) {
            System.out.println("ERROR: Insufficient credit for Eduard.");
            c2.addCredit(20.0); // Eduard tops up credit
            System.out.println("Eduard tops up 20.0 EUR.");

            if (c2.getCredit() >= eduardCost) {
                c2.deductCredit(eduardCost);
                eduardCharger.setStatus("occupied");
                System.out.println("Eduard charged for " + eduardMinutes + " minutes.");
                System.out.println("Cost: " + eduardCost + " EUR");
                System.out.println("Remaining credit: " + c2.getCredit());
                System.out.println("Charger status: " + eduardCharger.getStatus());
            } else {
                System.out.println("ERROR: Still insufficient credit for Eduard.");
            }
        } else {
            System.out.println("Unexpected success (should not happen)");
        }


        System.out.println("\n=== Creating Invoice for Eduard ===");
        if (eduardCharger.getStatus().equals("occupied")) {
            InvoiceManager im = InvoiceManager.getInstance();
            double eduardEnergyKwh = Math.round((eduardCost / eduardPricing.getPricePerKwh()) * 100.0) / 100.0;
            long eduardChargingMinutes = Math.round(eduardCost / eduardPricing.getPricePerMinute());
            Invoice eduardInvoice = im.createInvoice(c2.getId(), eduardCharger.getId(), eduardEnergyKwh, eduardChargingMinutes);
            System.out.println("Invoice created for Eduard:\n" + eduardInvoice.toPrint());
        } else {
            System.out.println("ERROR: Cannot create invoice for Eduard as the charging session was not completed.");
        }


        System.out.println("\n=== Charging - Jasmin ===");
        Chargers jasminCharger = cm.viewCharger("CHG-013");
        int jasminMinutes = 30;
        Pricing jasminPricing = loc7.getPricingForMode(jasminCharger.getType());
        double jasminCost = Math.round((jasminMinutes * jasminPricing.getPricePerMinute()) * 100.0) / 100.0;

        System.out.println("Jasmin tries to charge at charger: " + jasminCharger.getId());
        if (!jasminCharger.getStatus().equals("available")) {
            System.out.println("ERROR: Charger not available (out of order).");
        } else if (c3.getCredit() < jasminCost) {
            System.out.println("ERROR: Insufficient credit for Jasmin.");
        } else {
            System.out.println("Unexpected success (should not happen)");
        }



        NetworkStatus.print();


    }
}
