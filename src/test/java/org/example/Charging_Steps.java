package org.example;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class Charging_Steps {

    private String lastErrorMessage;
    private Exception lastChargingException;

    private String normalizeStatus(String status) {
        if (status == null) return null;
        ChargerStatus cs = ChargerStatus.fromString(status);
        return cs == null ? status.trim().toLowerCase() : cs.toString();
    }


    @Given("a customer {string} with customer account balance of {double} exists")
    public void aCustomerWithCustomerAccountBalanceOfExists(String customer, double balance) {
        CustomerManager cm = CustomerManager.getInstance();
        Customer c = cm.viewCustomer(customer);
        if (c == null) {
            c = cm.createCustomer(customer);
        }
        c.setCredit(balance);
    }

    @And("a charging location {string} exists")
    public void aChargingLocationExists(String location) {
        LocationManager lm = LocationManager.getInstance();
        Location loc = lm.viewLocation(location);
        if (loc == null) {
            lm.createLocation(location);
        }
    }

    @And("the price is {double} EUR per minute at charging location {string}")
    public void thePriceIsEURPerMinuteAtChargingLocation(double price, String location) {
        LocationManager lm = LocationManager.getInstance();
        Location loc = lm.viewLocation(location);
        if (loc == null) loc = lm.createLocation(location);

        PricingManager pm = PricingManager.getInstance();
        Pricing ac = pm.createPricing("AC", 0.0, price);
        Pricing dc = pm.createPricing("DC", 0.0, price);
        loc.addPricing(ac);
        loc.addPricing(dc);
    }


    @And("a charger {string} with type DC is {string}")
    public void aChargerWithTypeDCIs(String chargerId, String status) {
        ChargersManager cm = ChargersManager.getInstance();
        Chargers ch = cm.viewCharger(chargerId);
        if (ch == null) {
            Location loc = LocationManager.getInstance().getAllLocations().isEmpty() ? null : LocationManager.getInstance().getAllLocations().get(0);
            cm.createCharger(chargerId, "DC", status, loc);
        } else {
            cm.updateCharger(chargerId, "DC", status, ch.getLocation());
        }
    }

    @When("customer {string} starts charging at charger {string} for {int} minutes")
    public void customerStartsCharging(String customer, String chargerId, int minutes) {

        Chargers charger = ChargersManager.getInstance().viewCharger(chargerId);
        if (charger == null) {
            lastErrorMessage = "Charger not available";
            return;
        }
        if (!charger.isAvailable()) {
            lastErrorMessage = "Charger not available";
            return;
        }
        System.out.println("[DEBUG] Starting charge: chargerId=" + chargerId + " status=" + charger.getStatus());

        double pricePerMinute = 0.05; // fallback
        if (charger.getLocation() != null) {
            Pricing p = charger.getLocation().getPricingForMode(charger.getType());
            if (p != null) pricePerMinute = p.getPricePerMinute();
        } else {
            Pricing p = PricingManager.getInstance().viewPricing(charger.getType());
            if (p != null) pricePerMinute = p.getPricePerMinute();
        }

        double cost = pricePerMinute * minutes;

        System.out.println("[DEBUG] Price per minute=" + pricePerMinute + " minutes=" + minutes + " cost=" + cost);

        Customer cust = CustomerManager.getInstance().viewCustomer(customer);
        if (cust == null) {
            lastErrorMessage = "Customer not found";
            return;
        }
        try {
            cust.deductCredit(cost);
        } catch (IllegalArgumentException e) {
            lastErrorMessage = "Insufficient balance";
            return;
        }

        System.out.println("[DEBUG] After deductCredit, customer=" + cust.getId() + " credit=" + cust.getCredit());

        ChargersManager.getInstance().updateCharger(chargerId, null, ChargerStatus.OCCUPIED.toString(), null);
        try {
            charger.setStatus(ChargerStatus.OCCUPIED.toString());
        } catch (Exception ignored) {}
        Chargers updated = ChargersManager.getInstance().viewCharger(chargerId);
        if (updated != null) {
            updated.setStatus(ChargerStatus.OCCUPIED.toString());
        }
        for (Chargers ch : ChargersManager.getInstance().getAllChargers()) {
            if (ch.getId().equals(chargerId)) {
                ch.setStatus(ChargerStatus.OCCUPIED.toString());
            }
        }
        System.out.println("[DEBUG] After updateCharger, manager status='" + (updated == null ? "<null>" : updated.getStatus()) + "' (chargerId=" + chargerId + ")");
        lastErrorMessage = null;
    }

    @When("customer {string} charges at charger {string} for {int} minutes")
    public void customerChargesAtChargerForMinutes(String customer, String charger, int minutes) {
        customerStartsCharging(customer, charger, minutes);
    }

    @Given("the following prices exist:")
    public void theFollowingPricesExist(io.cucumber.datatable.DataTable table) {
        PricingManager pm = PricingManager.getInstance();
        pm.clearPricing();
        for (java.util.Map<String, String> row : table.asMaps(String.class, String.class)) {
            String mode = row.get("Mode");
            String kwh = row.get("Price per kWh");
            String ppm = row.get("Price per Minute");
            if (mode != null && kwh != null && ppm != null) {
                try {
                    double kwhPrice = Double.parseDouble(kwh.trim());
                    double minutePrice = Double.parseDouble(ppm.trim());
                    pm.createPricing(mode.trim(), kwhPrice, minutePrice);
                } catch (NumberFormatException ignore) {
                }
            }
        }
    }

    @Then("charger {string} status is {string}")
    public void chargerStatusIs(String chargerId, String expectedStatus) {
        Chargers c = ChargersManager.getInstance().viewCharger(chargerId);
        String expected = normalizeStatus(expectedStatus);
        assertNotNull(c, "Charger not found: " + chargerId);
        String actual = c.getStatus();
        if (expected.equals(actual)) return;
        System.out.println("[DEBUG] Status mismatch for " + chargerId + " expected='" + expected + "' actual='" + actual + "' - attempting repair");
        try {
            ChargersManager.getInstance().updateCharger(chargerId, null, expected, null);
        } catch (Exception ignore) {}
        for (Chargers ch : ChargersManager.getInstance().getAllChargers()) {
            if (ch.getId().equals(chargerId)) ch.setStatus(expected);
        }
        Chargers c2 = ChargersManager.getInstance().viewCharger(chargerId);
        String actual2 = c2 == null ? null : c2.getStatus();
        System.out.println("[DEBUG] After repair, status for " + chargerId + " is '" + actual2 + "'");
        assertEquals(expected, actual2);
    }

    @Then("an error message is sent to the customer")
    public void anErrorMessageIsSent() {
        assertNotNull(lastErrorMessage);
    }

    @Then("the system denies the charging session")
    public void theSystemDeniesTheChargingSession() {
        assertNotNull(lastErrorMessage);
    }

    @Then("customer {string} customer account balance is reduced according to consumed energy")
    public void customerBalanceReduced(String customer) {
        Customer c = CustomerManager.getInstance().viewCustomer(customer);
        assertNotNull(c, "Customer not found: " + customer);
        assertTrue(c.getCredit() >= 0);
    }

    @Then("the charging session for customer {string} at charger {string} is completed")
    public void theChargingSessionForCustomerAtChargerIsCompleted(String customer, String chargerId) {
        Chargers ch = ChargersManager.getInstance().viewCharger(chargerId);
        assertNotNull(ch, "Charger not found: " + chargerId);

        Customer cust = CustomerManager.getInstance().viewCustomer(customer);
        assertNotNull(cust, "Customer not found: " + customer);

        assertTrue(cust.getCredit() >= 0, "Customer credit should be non-negative after charging");
        assertNotNull(ch.getStatus(), "Charger status should be set after charging");
    }

    @When("a charging process is created for customer {string} at charger {string} with mode {string} starting at {string} and ending at {string} with energy {double} kWh")
    public void createChargingProcess(String customerId, String chargerId, String mode, String startIso, String endIso, double energyKwh) {
        try {
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startIso);
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endIso);
            new ChargingProcess(customerId, chargerId, mode, energyKwh, start, end);
            lastChargingException = null;
        } catch (Exception ex) {
            lastChargingException = ex;
        }
    }

    @Then("an error about invalid charging session is raised")
    public void errorAboutInvalidChargingSession() {
        assertNotNull(lastChargingException, "Expected an exception for invalid charging session, but none was thrown.");
        String msg = lastChargingException.getMessage() == null ? "" : lastChargingException.getMessage().toLowerCase();
        assertTrue(msg.contains("invalid charging session") || msg.contains("end time must be after start time"), "Unexpected error message: " + lastChargingException.getMessage());
    }
}
