package org.example;

import java.util.ArrayList;
import java.util.List;

public class Location {

    private final String name;
    private String address;

    public Location(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getAddress() {
        return address;
    }

    private final List<Pricing> pricingList = new ArrayList<>();

    public Location addPricing(Pricing p) {
        pricingList.add(p);
        return this;
    }

    public Pricing getPricingForMode(String mode) {
        return pricingList.stream()
                .filter(p -> p.getMode().equalsIgnoreCase(mode))
                .findFirst()
                .orElse(null);
    }

    public List<Pricing> getPricingList() {
        return pricingList;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(name).append(": ").append(address);


        var chargers = ChargersManager.getInstance().getChargersByLocation(this);


        if (chargers != null && !chargers.isEmpty()) {
            for (Chargers c : chargers) {
                sb.append("\n    - ")
                        .append(c.getId())
                        .append(": ")
                        .append(c.getType())
                        .append(" - ")
                        .append(c.getStatus());
            }
        }

        return sb.toString();
    }


}
