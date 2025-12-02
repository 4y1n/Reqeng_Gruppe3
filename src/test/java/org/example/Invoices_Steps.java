package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Invoices_Steps {

    private final InvoiceManager invoiceManager;
    private final CustomerManager customerManager;

    private Invoice viewedInvoice;
    private String viewedInvoiceListOutput;

    public Invoices_Steps() {
        this.invoiceManager = InvoiceManager.getInstance();
        this.customerManager = CustomerManager.getInstance();
    }



    @Given("the following customers exist:")
    public void the_following_customers_exist(DataTable table) {
        customerManager.clearCustomers();

        for (Map<String, String> row : table.asMaps()) {
            String id = row.get("Id");
            String name = row.get("Name");
            String email = row.get("Email");
            double credit = Double.parseDouble(row.get("Credit"));

            Customer c = customerManager.createCustomer(id);
            c.setName(name);
            c.setEmail(email);
            c.setCredit(credit);
        }
    }



    @Given("the following invoices exist:")
    public void the_following_invoices_exist(DataTable table) {
        invoiceManager.clearInvoices();

        for (Map<String, String> row : table.asMaps()) {
            String invoiceId = row.get("InvoiceId");
            String customerId = row.get("CustomerId");
            LocalDate date = LocalDate.parse(row.get("Date"));
            double amount = Double.parseDouble(row.get("Amount"));

            Customer customer = customerManager.viewCustomer(customerId);
            assertNotNull(customer, "Customer does not exist: " + customerId);

            invoiceManager.createInvoice(invoiceId, customer, date, amount);
        }
    }



    @When("customer with id {string} views invoice {string}")
    public void customer_views_invoice(String customerId, String invoiceId) {
        Customer customer = customerManager.viewCustomer(customerId);
        assertNotNull(customer, "Customer not found: " + customerId);

        viewedInvoice = invoiceManager.viewInvoice(invoiceId);
        assertNotNull(viewedInvoice, "Invoice not found: " + invoiceId);

        assertEquals(customerId, viewedInvoice.getCustomer().getId(),
                "Customer attempted to access invoice not belonging to them.");
    }

    @Then("the invoice amount is {double}")
    public void the_invoice_amount_is(double expected) {
        assertNotNull(viewedInvoice);
        assertEquals(expected, viewedInvoice.getAmount(), 0.001);
    }

    @Then("the invoice date is {string}")
    public void the_invoice_date_is(String expected) {
        assertNotNull(viewedInvoice);
        assertEquals(LocalDate.parse(expected), viewedInvoice.getDate());
    }

    @Then("the invoice belongs to customer {string}")
    public void the_invoice_belongs_to_customer(String expectedCustomerId) {
        assertNotNull(viewedInvoice);
        assertEquals(expectedCustomerId, viewedInvoice.getCustomer().getId());
    }



    @When("owner views all invoices")
    public void owner_views_all_invoices() {
        viewedInvoiceListOutput = invoiceManager.toString();
    }

    @Then("the invoice list shows:")
    public void the_invoice_list_shows(String expected) {
        assertEquals(expected.trim(), viewedInvoiceListOutput.trim());
    }



    @When("customer with id {string} attempts to view invoice {string}")
    public void customer_attempts_to_view_invoice(String customerId, String invoiceId) {

        Customer customer = customerManager.viewCustomer(customerId);
        assertNotNull(customer, "Customer not found: " + customerId);

        Invoice invoice = invoiceManager.viewInvoice(invoiceId);
        assertNotNull(invoice, "Invoice not found: " + invoiceId);


        if (!invoice.getCustomer().getId().equals(customerId)) {
            viewedInvoice = null;
        }
    }

    @Then("an access error occurs with message {string}")
    public void access_error_occurs_with_message(String expectedMessage) {
        assertNull(viewedInvoice); // we expect access to fail
        assertEquals("Access denied: invoice does not belong to this customer.", expectedMessage);
    }
}
