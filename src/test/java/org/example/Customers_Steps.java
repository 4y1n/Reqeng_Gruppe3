package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Customers_Steps {


    private final CustomerManager customerManager;
    private Customer currentCustomer;


    public Customers_Steps() {
        this.customerManager = CustomerManager.getInstance();
    }



    @When("customer creates a customer account with the unique id {string}")
    public void customerCreatesAccount(String id) {
        currentCustomer = customerManager.createCustomer(id);
    }

    @And("sets the customer name to {string}")
    public void setCustomerName(String name) {
        currentCustomer.setName(name);
    }

    @And("sets the customer email to {string}")
    public void setCustomerEmail(String email) {
        currentCustomer.setEmail(email);
    }

    @Then("the customer account {string} is part of the customer account list")
    public void customerExists(String id) {
        assertNotNull(customerManager.viewCustomer(id));
    }

    @And("the customer name is {string}")
    public void checkCustomerName(String expected) {
        assertEquals(expected, currentCustomer.getName());
    }

    @And("the customer email is {string}")
    public void checkCustomerEmail(String expected) {
        assertEquals(expected, currentCustomer.getEmail());
    }

    @And("the customer credit is {double}")
    public void checkCustomerCredit(Double expected) {
        assertEquals(expected, currentCustomer.getCredit());
    }


    @Given("the following customer accounts exist:")
    public void customerAccountsExist(DataTable table) {
        customerManager.clearCustomers();

        for (Map<String, String> row : table.asMaps()) {
            Customer c = customerManager.createCustomer(row.get("Id"));
            c.setName(row.get("Name"));
            c.setEmail(row.get("Email"));
            c.setCredit(Double.parseDouble(row.get("Credit")));
        }
    }


    @When("customer views the customer account with id {string}")
    public void viewCustomerAccount(String id) {
        currentCustomer = customerManager.viewCustomer(id);
        assertNotNull(currentCustomer);
    }




    @When("customer updates the customer name of {string} to {string}")
    public void updateCustomerName(String id, String newName) {
        Customer c = customerManager.viewCustomer(id);
        assertNotNull(c);
        c.setName(newName);
        currentCustomer = c;
    }

    @When("customer updates the customer email of {string} to {string}")
    public void updateCustomerEmail(String id, String newEmail) {
        Customer c = customerManager.viewCustomer(id);
        assertNotNull(c);
        c.setEmail(newEmail);
        currentCustomer = c;
    }



    @When("customer deletes the customer account {string}")
    public void deleteCustomer(String id) {
        customerManager.deleteCustomer(id);
    }

    @Then("the customer account {string} no longer exists")
    public void customerNotExists(String id) {
        assertNull(customerManager.viewCustomer(id));
    }

    @Then("the number of customer accounts is {int}")
    public void theNumberOfCustomerAccountsIs(Integer expectedCount) {
        assertEquals(expectedCount.intValue(), customerManager.getNumberOfCustomers());
    }




    @Given("an existing customer account with id {string}")
    public void existingCustomer(String id) {
        customerManager.clearCustomers();
        currentCustomer = customerManager.createCustomer(id);
    }

    @Given("the customer has a credit of {double}")
    public void customerHasCredit(Double amount) {
        currentCustomer.setCredit(amount);
    }

    @When("customer adds {double} credit to the customer account")
    public void addCredit(Double amount) {
        currentCustomer.addCredit(amount);
    }

    @Then("the customer account {string} has a credit of {double}")
    public void checkUpdatedCredit(String id, Double expected) {
        Customer c = customerManager.viewCustomer(id);
        assertNotNull(c);
        assertEquals(expected, c.getCredit());
    }
}
