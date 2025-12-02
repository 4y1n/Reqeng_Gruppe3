package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceManager {

    private static InvoiceManager instance;

    private final List<Invoice> invoices = new ArrayList<>();

    private InvoiceManager() {}

    public static InvoiceManager getInstance() {
        if (instance == null) {
            instance = new InvoiceManager();
        }
        return instance;
    }

    public InvoiceManager clearInvoices() {
        invoices.clear();
        return this;
    }

    public void createInvoice(String invoiceId, Customer customer, LocalDate date, double amount) {
        Invoice invoice = new Invoice(invoiceId, customer, date, amount);
        invoices.add(invoice);
    }

    public Invoice viewInvoice(String invoiceId) {
        return invoices.stream()
                .filter(i -> i.getInvoiceId().equals(invoiceId))
                .findFirst()
                .orElse(null);
    }

    public void deleteInvoice(String invoiceId) {
        invoices.removeIf(i -> i.getInvoiceId().equals(invoiceId));
    }

    public List<Invoice> getAllInvoices() {
        return invoices;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Invoices:\n");
        for (Invoice i : invoices) {
            sb.append("  ").append(i.toString()).append("\n");
        }
        return sb.toString().trim();
    }
}
