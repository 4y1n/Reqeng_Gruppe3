package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Invoice {

    private final String invoiceId;
    private final Customer customer;
    private final Chargers chargers;
    private final String mode;

    private final double energyKwh;
    private final long minutes;
    private final LocalDateTime end;

    private final Pricing pricing;
    private final double totalPrice;

    private final double balanceAfter;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Invoice(
            String invoiceId,
            Customer customer,
            Chargers chargers,
            String mode,
            double energyKwh,
            long minutes,
            LocalDateTime end,
            Pricing pricing
    ) {
        this.invoiceId = invoiceId;
        this.customer = customer;
        this.chargers = chargers;
        this.mode = mode;
        this.energyKwh = energyKwh;
        this.minutes = minutes;
        this.end = end;
        this.pricing = pricing;
        this.totalPrice = calculateTotalPrice(pricing.getPricePerKwh(), pricing.getPricePerMinute());

        this.balanceAfter = customer.getCredit();
    }

    public String getInvoiceId() { return invoiceId; }
    public Customer getCustomer() { return customer; }

    public String generatePrintout() {
        return toPrint();
    }


    public String toPrint() {
        String[] lines = new String[] {
                "------------------------------------",
                "Invoice ID: " + invoiceId,
                "Charging Date: " + end.format(dtf),
                "",
                "Customer ID: " + customer.getId(),
                "Charger: " + chargers.getId(),
                "Location: " + chargers.getLocation().getName(),
                "Mode: " + mode,
                "",
                "Charging Amount:",
                "  " + energyKwh + " kWh * " + format(pricing.getPricePerKwh()) + " EUR = " + format(energyKwh * pricing.getPricePerKwh()) + " EUR",
                "  " + minutes + " minutes * " + format(pricing.getPricePerMinute()) + " EUR = " + format(minutes * pricing.getPricePerMinute()) + " EUR",
                "",
                "Total Price: " + format(totalPrice) + " EUR",
                "Balance after transaction: " + format(balanceAfter) + " EUR",
                "------------------------------------"
        };

        return String.join("\n", lines);
    }

    private String format(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "customerId='" + customer.getId() + '\'' +
                ", chargerId='" + chargers.getId() + '\'' +
                ", amount=" + totalPrice +
                ", date=" + end.format(dtf) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Invoice invoice = (Invoice) obj;
        return invoiceId.equals(invoice.invoiceId);
    }

    @Override
    public int hashCode() {
        return invoiceId.hashCode();
    }

    public double calculateTotalPrice(double pricePerKwh, double pricePerMinute) {
        double totalPrice = (pricePerKwh * energyKwh) + (pricePerMinute * minutes);
        return roundToTwoDecimalPlaces(totalPrice);
    }

    private double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
