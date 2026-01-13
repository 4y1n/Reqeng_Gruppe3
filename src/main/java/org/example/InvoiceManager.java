package org.example;

import java.util.*;
import java.time.LocalDateTime;

public class InvoiceManager {

    private static final InvoiceManager INSTANCE = new InvoiceManager();

    public static InvoiceManager getInstance() {
        return INSTANCE;
    }

    private final List<Invoice> invoices = new ArrayList<>();

    private InvoiceManager() {}

    public InvoiceManager clearInvoices() {
        invoices.clear();
        return this;
    }

    public Invoice createInvoice(Invoice invoice) {
        if (invoices.contains(invoice)) {
            throw new IllegalArgumentException(
                    "Invoice with ID " + invoice.getInvoiceId() + " already exists."
            );
        }
        invoices.add(invoice);
        return invoice;
    }

    public Invoice createInvoice(String customerId, String chargerId, double amount) {
        Customer customer = CustomerManager.getInstance().viewCustomer(customerId);
        Chargers charger = ChargersManager.getInstance().viewCharger(chargerId);
        Pricing pricing = charger.getLocation().getPricingForMode(charger.getType());

        double energyKwh = amount / pricing.getPricePerKwh();
        long minutes = (long) (amount / pricing.getPricePerMinute());
        LocalDateTime end = LocalDateTime.now();

        Invoice invoice = new Invoice(
            nextInvoiceId(),
            customer,
            charger,
            charger.getType(),
            energyKwh,
            minutes,
            end,
            pricing,
            amount
        );

        invoices.add(invoice);
        return invoice;
    }

    public Invoice viewInvoice(String invoiceId) {
        for (Invoice inv : invoices) {
            if (inv.getInvoiceId().equals(invoiceId)) {
                return inv;
            }
        }
        throw new RuntimeException("Invoice not found: " + invoiceId);
    }

    public List<Invoice> viewAllInvoices() {
        return new ArrayList<>(invoices);
    }

    public List<Invoice> getAllInvoices() {
        return new ArrayList<>(invoices);
    }

    public void printAllInvoices() {
        System.out.println("\nInvoices:");
        for (Invoice invoice : invoices) {
            System.out.println(invoice);
        }
    }

    public int getNumberOfInvoices() {
        return invoices.size();
    }


    public String nextInvoiceId() {
        return String.format("INV-%03d", invoices.size() + 1);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Invoices:\n\n");

        for (Invoice inv : invoices) {
            String[] lines = inv.toPrint().split("\n");
            for (String line : lines) {
                sb.append("  ").append(line).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString().trim();
    }

}
