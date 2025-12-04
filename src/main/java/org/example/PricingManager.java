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
}
