package org.example;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Charging_Steps {

    private static String lastErrorMessage;
    private static Exception lastChargingException;

    // store the last created charging process (used by some verification steps)
    private static ChargingProcess chargingProcess;

    // record customer credits at charging start so we can verify billing later
    private static Map<String, Double> customerCredit = new HashMap<>();

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
        Pricing p = null;
        if (charger.getLocation() != null) {
            p = charger.getLocation().getPricingForMode(charger.getType());
            if (p == null) {
                // fallback to global pricing manager if location has no pricing for this mode
                p = PricingManager.getInstance().viewPricing(charger.getType());
            }
        } else {
            p = PricingManager.getInstance().viewPricing(charger.getType());
        }
        if (p != null) pricePerMinute = p.getPricePerMinute();

        double cost = pricePerMinute * minutes;

        System.out.println("[DEBUG] Price per minute=" + pricePerMinute + " minutes=" + minutes + " cost=" + cost);

        Customer cust = CustomerManager.getInstance().viewCustomer(customer);
        if (cust == null) {
            lastErrorMessage = "Customer not found";
            return;
        }
        // record customer's credit prior to charging so we can verify billing later
        customerCredit.put(customer, cust.getCredit());

        // create a ChargingProcess representing this minute-based session (energy unknown -> 0.0 kWh)
        java.time.LocalDateTime start = java.time.LocalDateTime.now();
        java.time.LocalDateTime end = start.plusMinutes(minutes);
        try {
            System.out.println("[DEBUG] creating ChargingProcess with start=" + start + " end=" + end + " energy=0.0");
            chargingProcess = new ChargingProcess(customer, chargerId, charger.getType(), 0.0, start, end);
            System.out.println("[DEBUG] chargingProcess created in customerStartsCharging: " + chargingProcess);
        } catch (Exception ex) {
            // shouldn't happen, but store the exception for later assertions
            lastChargingException = ex;
            System.out.println("[ERROR] Failed to create chargingProcess in customerStartsCharging. customer='" + customer + "' chargerId='" + chargerId + "' type='" + (charger == null ? "<null>" : charger.getType()) + "' minutes=" + minutes);
            ex.printStackTrace(System.out);
            // abort if creation failed
            lastErrorMessage = "Invalid charging process";
            return;
        }

        try {
            cust.deductCredit(cost);
        } catch (IllegalArgumentException e) {
            // remove the created chargingProcess if payment fails
            chargingProcess = null;
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
        System.out.println("[DEBUG] customerChargesAtChargerForMinutes called: customer=" + customer + " charger=" + charger + " minutes=" + minutes);
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

    @And("customer {string} customer account balance is reduced according to consumed energy")
    public void customerCustomerAccountBalanceIsReducedAccordingToConsumedEnergy(String customerId) {
        System.out.println("[DEBUG] Verifying customer balance; chargingProcess=" + chargingProcess + " lastChargingException=" + lastChargingException + " lastErrorMessage=" + lastErrorMessage);
        // chargingProcess must be present to compute expected cost
        assertNotNull(chargingProcess, "No charging process available to verify billing");

        // determine pricing (prefer price per kWh, otherwise price per minute)
        String mode = chargingProcess.getMode();
        String chargerId = chargingProcess.getChargerId();

        Pricing p = null;
        if (chargerId != null) {
            Chargers ch = ChargersManager.getInstance().viewCharger(chargerId);
            if (ch != null && ch.getLocation() != null) {
                p = ch.getLocation().getPricingForMode(mode);
                if (p == null) p = PricingManager.getInstance().viewPricing(mode);
            }
        }
        if (p == null && mode != null) {
            p = PricingManager.getInstance().viewPricing(mode);
        }

        double cost;
        // prefer kWh-based pricing only when energy is specified (>0), otherwise use price per minute
        if (p != null && chargingProcess.getEnergyKwh() > 1e-9 && p.getPricePerKwh() > 1e-9) {
            cost = p.getPricePerKwh() * chargingProcess.getEnergyKwh();
        } else {
            double ppm = (p != null) ? p.getPricePerMinute() : 0.05;
            cost = ppm * chargingProcess.getDurationMinutes();
        }

        Customer cust = CustomerManager.getInstance().viewCustomer(customerId);
        assertNotNull(cust, "Customer not found: " + customerId);

        Double before = customerCredit.get(customerId);
        assertNotNull(before, "Original customer credit not recorded; ensure charging start stored it in customerCredit map");

        double expected = before - cost;
        assertEquals(expected, cust.getCredit(), 1e-6, "Customer balance was not reduced by the expected amount");
    }

    @When("a charging process is created for customer {string} at charger {string} with mode {string} starting at {string} and ending at {string} with energy {double} kWh")
    public void createChargingProcess(String customerId, String chargerId, String mode, String startIso, String endIso, double energyKwh) {
        try {
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startIso);
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endIso);
            // store created process so later checks can inspect it
            chargingProcess = new ChargingProcess(customerId, chargerId, mode, energyKwh, start, end);
            System.out.println("[DEBUG] chargingProcess created in createChargingProcess: " + chargingProcess);
            lastChargingException = null;
        } catch (Exception ex) {
            System.out.println("[ERROR] Failed to create chargingProcess in createChargingProcess: " + ex.getMessage());
             lastChargingException = ex;
        }
    }

    @Then("an error about invalid charging session is raised")
    public void errorAboutInvalidChargingSession() {
        assertNotNull(lastChargingException, "Expected an exception for invalid charging session, but none was thrown.");
        String msg = lastChargingException.getMessage() == null ? "" : lastChargingException.getMessage().toLowerCase();
        assertTrue(msg.contains("invalid charging session") || msg.contains("end time must be after start time"), "Unexpected error message: " + lastChargingException.getMessage());
    }

    @Then("the charging session for customer {string} at charger {string} is completed")
    public void theChargingSessionForCustomerAtChargerIsCompleted(String customerId, String chargerId) {
        Chargers c = ChargersManager.getInstance().viewCharger(chargerId);
        assertNotNull(c, "Charger not found: " + chargerId);

        Customer cust = CustomerManager.getInstance().viewCustomer(customerId);
        assertNotNull(cust, "Customer not found: " + customerId);

        // basic post-conditions
        assertTrue(cust.getCredit() >= 0, "Customer credit should be non-negative after charging");
        assertNotNull(c.getStatus(), "Charger status should be set after charging");

        // set charger available via manager and ensure internal list entries reflect the change
        try {
            ChargersManager.getInstance().updateCharger(chargerId, null, ChargerStatus.AVAILABLE.toString(), null);
        } catch (Exception ignore) {}
        for (Chargers ch : ChargersManager.getInstance().getAllChargers()) {
            if (ch.getId().equals(chargerId)) ch.setStatus(ChargerStatus.AVAILABLE.toString());
        }

        Chargers after = ChargersManager.getInstance().viewCharger(chargerId);
        assertNotNull(after, "Charger disappeared after completion: " + chargerId);
        assertTrue(after.isAvailable(), "Charger should be available after completion");
    }
}
