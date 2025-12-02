package org.example;

import java.time.LocalDate;

public class Invoice {

    private final String invoiceId;
    private final Customer customer;
    private final LocalDate date;
    private final double amount;

    public Invoice(String invoiceId, Customer customer, LocalDate date, double amount) {
        this.invoiceId = invoiceId;
        this.customer = customer;
        this.date = date;
        this.amount = amount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return invoiceId + ": Customer " + customer.getId()
                + " - " + String.format(java.util.Locale.US, "%.2f", amount) + " EUR"
                + " - " + date.toString();
    }
}
