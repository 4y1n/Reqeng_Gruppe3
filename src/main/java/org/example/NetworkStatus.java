package org.example;

import java.util.List;


public class NetworkStatus {

    public static void print() {
        System.out.println("\n=== NETWORK STATUS ===");

        LocationManager lm = LocationManager.getInstance();
        ChargersManager cm = ChargersManager.getInstance();
        CustomerManager um = CustomerManager.getInstance();
        PricingManager pm = PricingManager.getInstance();

        System.out.println("Locations: " + lm.getNumberOfLocations());
        System.out.println("Chargers: " + cm.getAllChargers().size());
        System.out.println("Customers: " + um.getAllCustomers().size());
        System.out.println("Pricing entries: " + pm.getPricingList().size());

        System.out.println("\n-- Locations and current pricing --");
        for (Location loc : lm.getAllLocations()) {
            System.out.println("Location: " + loc.getName() + " (" + loc.getAddress() + ")");

            List<Pricing> pricingList = loc.getPricingList();
            if (pricingList == null || pricingList.isEmpty()) {
                System.out.println("  No pricing defined for this location");
            } else {
                for (Pricing p : pricingList) {
                    System.out.println("  Mode: " + p.getMode() + " | kWh: " + p.getPricePerKwh() + " EUR | min: " + p.getPricePerMinute() + " EUR");
                }
            }
        }

        System.out.println("\n-- Chargers (id : type - status) --");
        for (Chargers ch : cm.getAllChargers()) {
            System.out.println("  " + ch.getId() + ": " + ch.getType() + " - " + ch.getStatus() + " (Location: " + ch.getLocation().getName() + ")");
        }

        System.out.println("\n========================");
    }
}
