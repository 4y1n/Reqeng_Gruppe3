package org.example;

import java.util.ArrayList;
import java.util.List;

public class PricingManager {

    private static PricingManager instance;
    private final List<Pricing> allPricing = new ArrayList<>();

    private PricingManager() {}

    public static PricingManager getInstance() {
        if (instance == null) instance = new PricingManager();
        return instance;
    }

    public PricingManager clearPricing() {
        allPricing.clear();
        return this;
    }

    public Pricing createPricing(String mode, double kwh, double minute) {
        return new Pricing(mode, kwh, minute);
    }
}
