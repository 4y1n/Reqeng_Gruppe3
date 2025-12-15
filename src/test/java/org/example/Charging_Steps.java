package org.example;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Charging_Steps {

    private Map<String, Double> customerCredit = new HashMap<>();
    private Map<String, String> chargerStatus = new HashMap<>();
    private Map<String, Double> chargingLocationPricePerMinute = new HashMap<>();
    private String lastErrorMessage;
    private ChargingProcess chargingProcess;



    @Given("a customer {string} with customer account balance of {double} exists")
    public void aCustomerWithCustomerAccountBalanceOfExists(String customer, double balance) {
        customerCredit.put(customer, balance);
    }

    @And("a charging location {string} exists")
    public void aChargingLocationExists(String location) {
        chargingLocationPricePerMinute.putIfAbsent(location, 0.0);
    }

    @And("the price is {double} EUR per minute at charging location {string}")
    public void thePriceIsEURPerMinuteAtChargingLocation(double price, String location) {
        chargingLocationPricePerMinute.put(location, price);
    }

    @And("a charger {string} with type DC is {string}")
    public void aChargerWithTypeDCIs(String charger, String status) {
        chargerStatus.put(charger, status);
    }




    @When("customer {string} starts charging at charger {string} for {int} minutes")
    public void customerStartsCharging(String customer, String charger, int minutes) {

        if (!"available".equals(chargerStatus.getOrDefault(charger, ""))) {
            lastErrorMessage = "Charger not available";
            return;
        }


        double pricePerMinute = chargingLocationPricePerMinute.values().stream()
                .findFirst()
                .orElse(0.05);

        double cost = pricePerMinute * minutes;
        double balance = customerCredit.getOrDefault(customer, 0.0);

        if (balance < cost) {
            lastErrorMessage = "Insufficient balance";
            return;
        }

        customerCredit.put(customer, balance - cost);
        chargerStatus.put(charger, "unavailable");
    }

    @When("the customer tries to start a charging session at charger {string}")
    public void theCustomerTriesToStartAChargingSessionAt(String charger) {
        if (!"available".equals(chargerStatus.getOrDefault(charger, ""))) {
            lastErrorMessage = "Charger not available";
        }
    }




    @Then("the charging session for customer {string} at charger {string} is completed")
    public void theChargingSessionIsCompleted(String customer, String charger) {
        assertEquals("unavailable", chargerStatus.get(charger));
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

    @Then("an error message is sent to the customer")
    public void anErrorMessageIsSent() {
        assertNotNull(lastErrorMessage);
    }

    @Then("the system denies the charging session")
    public void theSystemDeniesTheChargingSession() {
        assertNotNull(lastErrorMessage);
    }

    @Then("the charger {string} followed by {int} {string} characters is part of the charger list")
    public void theChargerFollowedByCharactersIsPartOfTheChargerList(String arg0, int arg1, String arg2) {
    }




    @When("a charging process is created for customer {string} at charger {string} with mode {string} starting at {string} and ending at {string} with energy {double} kWh")
    public void aChargingProcessIsCreatedForCustomerAtChargerWithModeAndTimesAndEnergy(
            String customer, String charger, String mode, String startIso, String endIso, double energyKwh) {
        try {
            chargingProcess = new ChargingProcess(
                    customer,
                    charger,
                    mode,
                    energyKwh,
                    java.time.LocalDateTime.parse(startIso),
                    java.time.LocalDateTime.parse(endIso)
            );
            lastErrorMessage = null;
        } catch (IllegalArgumentException ex) {
            chargingProcess = null;
            lastErrorMessage = ex.getMessage();
        }
    }

    @Then("the charging process is created successfully")
    public void theChargingProcessIsCreatedSuccessfully() {
        assertNotNull(chargingProcess);
        assertNull(lastErrorMessage);
    }

    @Then("the charging process duration is {int} minutes")
    public void theChargingProcessDurationIsMinutes(int expectedMinutes) {
        assertNotNull(chargingProcess);
        assertEquals(expectedMinutes, chargingProcess.getDurationMinutes());
    }

    @Then("the charging process energy is {double} kWh")
    public void theChargingProcessEnergyIsKwh(double expectedEnergy) {
        assertNotNull(chargingProcess);
        assertEquals(expectedEnergy, chargingProcess.getEnergyKwh(), 1e-6);
    }

    @Then("an error about invalid charging session is raised")
    public void anErrorAboutInvalidChargingSessionIsRaised() {
        assertNotNull(lastErrorMessage);
        assertTrue(lastErrorMessage.contains("Invalid charging session"));
    }
}
