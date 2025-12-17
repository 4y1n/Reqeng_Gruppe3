package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class Invoices_Steps {

    private Exception lastException;
    private String lastViewedInvoicePrintout;   // gespeichert für "Then the invoice shows"
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");



    @Given("the following customers exist:")
    public void the_following_customers_exist(DataTable table) {
        CustomerManager cm = CustomerManager.getInstance();
        for (Map<String, String> row : table.asMaps()) {
            cm.createCustomer(row.get("Id"), row.get("Name"), row.get("Email"));
        }
    }

    @Given("customer {string} tops up {double} EUR")
    public void customer_tops_up(String id, double amount) {
        CustomerManager.getInstance().viewCustomer(id).addCredit(amount);
    }

    @Given("owner sets pricing:")
    public void owner_sets_pricing(DataTable table) {
        PricingManager pm = PricingManager.getInstance();
        for (Map<String, String> row : table.asMaps()) {
            pm.createPricing(
                    row.get("Mode"),
                    Double.parseDouble(row.get("Price per kWh")),
                    Double.parseDouble(row.get("Price per Minute"))
            );
        }
    }



    @When("customer {string} charges at charger {string} for:")
    public void customer_charges(String customerId, String chargerId, String block) {

        Map<String, String> data = parse(block);

        String mode = data.get("Mode");
        double energy = Double.parseDouble(data.get("Energy").replace(" kWh", ""));
        LocalDateTime start = LocalDateTime.parse(data.get("Start"), dtf);
        LocalDateTime end = LocalDateTime.parse(data.get("End"), dtf);

        Customer cust = CustomerManager.getInstance().viewCustomer(customerId);
        Chargers chargers = ChargersManager.getInstance().viewCharger(chargerId);
        Pricing pricing = PricingManager.getInstance().viewPricing(mode);


        if (pricing == null) {
            lastException = new RuntimeException("No pricing defined for mode " + mode);
            return;
        }

        ChargingProcess cp = new ChargingProcess(
                customerId,
                chargerId,
                mode,
                energy,
                start,
                end
        );

        double energyCost = cp.getEnergyKwh() * pricing.getPricePerKwh();
        double minuteCost = cp.getDurationMinutes() * pricing.getPricePerMinute();
        double total = energyCost + minuteCost;

        cust.deductCredit(total);

        String invoiceId = InvoiceManager.getInstance().nextInvoiceId();

        Invoice inv = new Invoice(
                invoiceId,
                cust,
                chargers,
                mode,
                cp.getEnergyKwh(),
                cp.getDurationMinutes(),
                end,
                pricing,
                total
        );

        InvoiceManager.getInstance().createInvoice(inv);
    }



    @When("customer with id {string} views invoice {string}")
    public void customer_views_invoice(String customerId, String invoiceId) {
        try {
            Invoice inv = InvoiceManager.getInstance().viewInvoice(invoiceId);

            if (inv == null) {
                throw new RuntimeException("Invoice not found: " + invoiceId);
            }

            if (!inv.getCustomer().getId().equals(customerId)) {
                throw new RuntimeException("Access denied: invoice does not belong to this customer.");
            }


            lastViewedInvoicePrintout = inv.generatePrintout();

        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("owner views all invoices")
    public void owner_views_all_invoices() {
        lastViewedInvoicePrintout = InvoiceManager.getInstance().toString();
    }



    @Then("invoice {string} is created for customer {string}")
    public void invoice_created_for(String invoiceId, String customerId) {
        Invoice inv = InvoiceManager.getInstance().viewInvoice(invoiceId);
        assertNotNull(inv);
        assertEquals(customerId, inv.getCustomer().getId());
    }

    @Then("an access error occurs with message {string}")
    public void access_error(String expected) {
        assertNotNull(lastException);
        assertEquals(expected, lastException.getMessage());
    }

    @Then("the invoice list shows:")
    public void invoice_list_shows(String expected) {
        String actual = InvoiceManager.getInstance().toString();
        if (lastViewedInvoicePrintout != null) actual = lastViewedInvoicePrintout;

        assertEquals(
                normalize(expected),
                normalize(actual)
        );
    }

    @Then("the invoice shows:")
    public void the_invoice_shows(String expected) {
        assertNotNull(lastViewedInvoicePrintout, "No invoice was viewed.");
        assertEquals(
                normalize(expected),
                normalize(lastViewedInvoicePrintout)
        );
    }



    private Map<String, String> parse(String block) {
        Map<String, String> m = new LinkedHashMap<>();
        for (String line : block.split("\n")) {
            if (line.contains(":")) {
                String[] p = line.split(":", 2);
                m.put(p[0].trim(), p[1].trim());
            }
        }
        return m;
    }

    private String normalize(String s) {
        if (s == null) return "";
        String t = s.replace("\r\n", "\n").replace("\r", "\n");
        String[] lines = t.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line.replaceAll("^[ \t]+", "").replaceAll("[ \t]+$", "")).append("\n");
        }
        String noTrailingSpaces = sb.toString().trim();
        String collapsed = noTrailingSpaces.replaceAll("\n{3,}", "\n\n");
        return collapsed.replace(",", ".").trim();
    }

    @When("owner attempts to create an invoice with id {string} for customer {string} at charger {string}")
    public void ownerAttemptsToCreateInvoiceWithId(String invoiceId, String customerId, String chargerId) {
        try {
            Customer cust = CustomerManager.getInstance().viewCustomer(customerId);
            Chargers chargers = ChargersManager.getInstance().viewCharger(chargerId);
            Pricing pricing = PricingManager.getInstance().viewPricing("AC");
            // Erzeuge eine kleine Dummy-Rechnung (Datum im Format der vorhandenen dtf)
            LocalDateTime end = LocalDateTime.parse("2025-01-01 10:01", dtf);
            Invoice inv = new Invoice(invoiceId, cust, chargers, "AC", 1.0, 1, end, pricing, 0.0);
            InvoiceManager.getInstance().createInvoice(inv);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
        }
    }

    @And("only one invoice {string} is created for customer {string}")
    public void onlyOneInvoiceIsCreatedForCustomer(String invoiceId, String customerId) {
        List<Invoice> all = InvoiceManager.getInstance().viewAllInvoices();
        long matching = all.stream()
                .filter(inv -> invoiceId.equals(inv.getInvoiceId()) && customerId.equals(inv.getCustomer().getId()))
                .count();
        assertEquals(1, matching);
        // zusätzlich sicherstellen, dass insgesamt nur eine Rechnung existiert (Szenario-spezifisch)
        assertEquals(1, InvoiceManager.getInstance().getNumberOfInvoices());
    }

    @When("owner attempts to create invoice {string} for customer {string}")
    public void ownerAttemptsToCreateInvoiceForCustomer(String invoiceId, String customerId) {
        // Charger nicht in Schritt angegeben — benutze Standard-CHG-001 aus Background
        ownerAttemptsToCreateInvoiceWithId(invoiceId, customerId, "CHG-001");
    }

    @And("only one invoice {string} is present for customer {string}")
    public void onlyOneInvoiceIsPresentForCustomer(String invoiceId, String customerId) {
        // Delegiere an die vorhandene Prüf-Methode, um Logik nicht zu duplizieren
        onlyOneInvoiceIsCreatedForCustomer(invoiceId, customerId);
    }

    @Given("no invoices exist")
    public void noInvoicesExist() {
        InvoiceManager.getInstance().clearInvoices();
        lastException = null;
        lastViewedInvoicePrintout = null;
    }
}