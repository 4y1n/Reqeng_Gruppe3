package org.example;

import java.util.ArrayList;
import java.util.List;

public class PricingManager {

    private static final PricingManager INSTANCE = new PricingManager();
    private final List<Pricing> pricingList = new ArrayList<>();

    private PricingManager() {}

    public static PricingManager getInstance() {
        return INSTANCE;
    }

    public PricingManager clearPricing() {
        pricingList.clear();
        return this;
    }


    public Pricing createPricing(String mode, double kwh, double minute) {
        Pricing p = new Pricing(mode, kwh, minute);
        pricingList.add(p);
        return p;
    }

    public Pricing viewPricing(String mode) {
        return pricingList.stream()
                .filter(p -> p.getMode().equalsIgnoreCase(mode))
                .findFirst()
                .orElse(null);
    }

    public List<Pricing> getPricingList() {
        return pricingList;
    }

    public void displayLocationsWithPricing(LocationManager locationManager) {
        System.out.println("\nLocations with Pricing:");
        for (Location location : locationManager.getAllLocations()) {
            System.out.println("Location: " + location.getName());
            System.out.println("Address: " + location.getAddress());
            for (Pricing pricing : location.getPricingList()) {
                System.out.println("  Mode: " + pricing.getMode()
                        + " | kWh: " + pricing.getPricePerKwh()
                        + " | min: " + pricing.getPricePerMinute());
            }
            System.out.println();
        }
    }
}
