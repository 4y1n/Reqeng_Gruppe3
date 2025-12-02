package org.example;

import java.time.LocalDateTime;

public class Price {
    private LocalDateTime date;
    private double pricekWhAC;
    private double pricekWhDC;
    private double priceMinAC;
    private double priceMinDC;

    public double getPricekWhAC() {
        return pricekWhAC;
    }

    public void setPricekWhAC(double pricekWhAC) {
        this.pricekWhAC = pricekWhAC;
    }

    public double getPricekWhDC() {
        return pricekWhDC;
    }

    public void setPricekWhDC(double pricekWhDC) {
        this.pricekWhDC = pricekWhDC;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getPriceMinAC() {
        return priceMinAC;
    }

    public void setPriceMinAC(double priceMinAC) {
        this.priceMinAC = priceMinAC;
    }

    public double getPriceMinDC() {
        return priceMinDC;
    }

    public void setPriceMinDC(double priceMinDC) {
        this.priceMinDC = priceMinDC;
    }
    public double calculate(String type, double energyKWh, int durationMin) {
        if (type == null) {
            throw new IllegalArgumentException("Charger type is null");
        }

        if (type.equalsIgnoreCase("AC")) {
            return (energyKWh * pricekWhAC) + (durationMin * priceMinAC);

        } else if (type.equalsIgnoreCase("DC")) {
            return (energyKWh * pricekWhDC) + (durationMin * priceMinDC);

        } else {
            throw new IllegalArgumentException("Unknown charger type: " + type);
        }
    }
}
