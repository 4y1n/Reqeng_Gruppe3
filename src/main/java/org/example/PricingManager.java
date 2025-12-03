package org.example;

import java.util.ArrayList;
import java.util.List;

public class PricingManager {

    private static PricingManager instance;
    private final List<Pricing> pricingList = new ArrayList<>();

    private PricingManager() {}

    public static PricingManager getInstance() {
        if (instance == null) instance = new PricingManager();
        return instance;
    }

    public PricingManager clearPricing() {
        pricingList.clear();
        return this;
    }

    public List<Pricing> getPricingList() {
        return pricingList;
    }

    public Pricing createPricing(String mode, double kwh, double minute) {
        Pricing p = new Pricing(mode, kwh, minute);
        pricingList.add(p);
        return p;
    }
}

