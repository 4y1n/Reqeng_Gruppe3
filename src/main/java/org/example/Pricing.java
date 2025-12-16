package org.example;

public class Pricing {

    private String mode;              // AC / DC
    private double pricePerKwh;       // Price per kWh
    private double pricePerMinute;    // Price per minute

    public Pricing(String mode, double pricePerKwh, double pricePerMinute) {
        this.mode = mode;
        this.pricePerKwh = pricePerKwh;
        this.pricePerMinute = pricePerMinute;
    }

    public String getMode() {
        return mode;
    }

    public double getPricePerKwh() {
        return pricePerKwh;
    }

    public Pricing setPricePerKwh(double pricePerKwh) {
        this.pricePerKwh = pricePerKwh;
        return this;
    }

    public double getPricePerMinute() {
        return pricePerMinute;
    }

    public Pricing setPricePerMinute(double pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
        return this;
    }

    @Override
    public String toString() {
        return "Pricing{" +
                "mode='" + mode + '\'' +
                ", pricePerKwh=" + pricePerKwh +
                ", pricePerMinute=" + pricePerMinute +
                '}';
    }
}
