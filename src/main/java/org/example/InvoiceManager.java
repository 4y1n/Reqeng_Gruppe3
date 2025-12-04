package org.example;

import java.util.*;

public class InvoiceManager {

    private static final InvoiceManager INSTANCE = new InvoiceManager();

    public static InvoiceManager getInstance() {
        return INSTANCE;
    }

    private final Map<String, Invoice> invoices = new LinkedHashMap<>();

    private InvoiceManager() {}

    public InvoiceManager clearInvoices() {
        invoices.clear();
        return this;
    }

    public Invoice createInvoice(Invoice invoice) {
        if (invoices.containsKey(invoice.getInvoiceId())) {
            throw new IllegalArgumentException(
                    "Invoice with ID " + invoice.getInvoiceId() + " already exists."
            );
        }
        invoices.put(invoice.getInvoiceId(), invoice);
        return invoice;
    }

    public Invoice viewInvoice(String invoiceId) {
        Invoice inv = invoices.get(invoiceId);
        if (inv == null) {
            throw new RuntimeException("Invoice not found: " + invoiceId);
        }
        return inv;
    }

    public List<Invoice> viewAllInvoices() {
        return new ArrayList<>(invoices.values());
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

        for (Invoice inv : invoices.values()) {
            String[] lines = inv.toPrint().split("\n");
            for (String line : lines) {
                sb.append("  ").append(line).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString().trim();
    }

}
