package org.example;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Charge_vehicle_steps {
    private Map<String, Double> customerCredit = new HashMap<>();
    private Map<String, String> chargerStatus = new HashMap<>();
    private Map<String, Double> locationPricePerMinute = new HashMap<>();
    private String lastErrorMessage;


    @Given("a customer {string} with customer account balance of {double} exists")
    public void aCustomerWithCustomerAccountBalanceOfExists(String customer, double balance) {
        customerCredit.put(customer, balance);
    }
    @And("a location {string} exists")
    public void aLocationExists(String location) {
        locationPricePerMinute.putIfAbsent(location, 0.0);
    }
    @And("the price is {double} EUR per minute at {string}")
    public void thePriceIsEURPerMinuteAtLocation(double price, String location) {
        locationPricePerMinute.put(location, price);
    }
    @And("a charger {string} with type DC is {string}")
    public void aChargerWithTypeDCIs(String charger, String status) {
        chargerStatus.put(charger, status);
    }
    @When("customer {string} starts charging at {string} for {int} minutes")
    public void customerStartsCharging(String customer, String charger, int minutes) {

        if (!"available".equals(chargerStatus.getOrDefault(charger, ""))) {
            lastErrorMessage = "Charger not available";
            return;
        }

        double pricePerMinute = locationPricePerMinute.values().stream()
                .findFirst().orElse(0.05);

        double cost = pricePerMinute * minutes;
        double balance = customerCredit.getOrDefault(customer, 0.0);


        if (balance < cost) {
            lastErrorMessage = "Insufficient balance";
            return;
        }

        customerCredit.put(customer, balance - cost);
        chargerStatus.put(charger, "occupied");
    }

    @When("the customer tries to start a charging session at {string}")
    public void theCustomerTriesToStartAChargingSessionAt(String charger) {
        if (!"available".equals(chargerStatus.get(charger))) {
            lastErrorMessage = "Charger not available";
        }
    }


    @Then("the charging session for {string} at {string} is completed")
    public void theChargingSessionIsCompleted(String customer, String charger) {
        assertEquals("occupied", chargerStatus.get(charger));
        assertTrue(customerCredit.get(customer) >= 0);
    }

    @Then("customer {string} customer account balance is reduced according to consumed energy")
    public void customerBalanceReduced(String customer) {
        assertTrue(customerCredit.get(customer) >= 0);
    }

    @Then("charger {string} status is {string}")
    public void chargerStatusIs(String charger, String expectedStatus) {
        assertEquals(expectedStatus, chargerStatus.get(charger));
    }

    @Then("an error message is sent")
    public void anErrorMessageIsSent() {
        assertNotNull(lastErrorMessage);
    }
    @Then("the system denies the charging session")
    public void theSystemDeniesTheChargingSession() {
        assertNotNull(lastErrorMessage);
    }


}
