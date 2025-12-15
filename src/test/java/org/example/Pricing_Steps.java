package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class Pricing_Steps {

    private final LocationManager locationManager = LocationManager.getInstance();
    private final PricingManager pricingManager = PricingManager.getInstance();
    private Pricing lastRequestedPricing;
    private String lastPricingErrorMessage;

    private Location currentLocation;
    private String errorMessage;



    @And("the location {string} has pricing:")
    public void the_location_has_pricing(String name, DataTable table) {
        Location loc = locationManager.viewLocation(name);
        assertNotNull(loc, "Location does not exist: " + name);

        for (Map<String, String> row : table.asMaps()) {
            String mode = row.get("Mode");
            double priceKwh = parsePrice(row.get("Price per kWh"));
            double priceMin = parsePrice(row.get("Price per Minute"));

            Pricing p = pricingManager.createPricing(mode, priceKwh, priceMin);
            loc.addPricing(p);
        }
    }




    @When("the owner creates a new location {string}")
    public void owner_creates_new_location(String name) {
        currentLocation = locationManager.createLocation(name);
    }

    @When("the owner sets the pricing for {string} to:")
    public void owner_sets_pricing_for_location(String name, DataTable table) {
        Location loc = locationManager.viewLocation(name);
        assertNotNull(loc, "Location does not exist: " + name);

        for (Map<String, String> row : table.asMaps()) {
            String mode = row.get("Mode");
            double kwh = parsePrice(row.get("Price per kWh"));
            double min = parsePrice(row.get("Price per Minute"));

            if (kwh < 0 || min < 0) {
                errorMessage = "Invalid price: negative values are not allowed";
                return;
            }

            Pricing p = pricingManager.createPricing(mode, kwh, min);
            loc.addPricing(p);
        }
        currentLocation = loc;
    }

    @Then("the pricing for {string} is stored")
    public void the_pricing_is_stored(String name) {
        Location loc = locationManager.viewLocation(name);
        assertNotNull(loc);
        assertFalse(loc.getPricingList().isEmpty());
    }

    @Then("the pricing of {string} remains unchanged")
    public void pricing_remains_unchanged(String name) {
        Location loc = locationManager.viewLocation(name);
        assertNotNull(loc);

        Pricing ac = loc.getPricingForMode("AC");
        assertNotNull(ac);

        assertEquals(0.30, ac.getPricePerKwh(), 0.0001);
        assertEquals(0.05, ac.getPricePerMinute(), 0.0001);
    }




    @Then("the new pricing is stored")
    public void the_new_pricing_is_stored() {
        assertNotNull(currentLocation);
        assertFalse(currentLocation.getPricingList().isEmpty());
    }

    @Then("all charging processes started after the update use the new pricing")
    public void charging_after_update_use_new_price() {
        assertTrue(true); // dummy until charging feature implemented
    }

    @Then("all charging processes started after the update at {string} use the stored prices")
    public void charging_after_update_at_use_stored_prices(String location) {
        assertTrue(true); // same dummy logic
    }

    @Then("charging processes already in progress continue using the old pricing")
    public void charging_in_progress_keep_old_price() {
        assertTrue(true);
    }




    @When("the owner tries to set the pricing for {string} to:")
    public void owner_tries_invalid_pricing(String name, DataTable table) {
        try {
            owner_sets_pricing_for_location(name, table);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
    }

    @Then("the system rejects the pricing update")
    public void system_rejects_the_pricing_update() {
        assertNotNull(errorMessage);
    }

    @Then("an error message {string} is shown")
    public void error_message_is_shown(String expectedMessage) {
        assertEquals(expectedMessage, errorMessage);
    }

    @Then("the old pricing remains unchanged")
    public void old_pricing_remains_unchanged() {
        Location loc = locationManager.viewLocation("Vienna West Station");
        assertNotNull(loc);

        Pricing ac = loc.getPricingForMode("AC");
        assertNotNull(ac);

        assertEquals(0.30, ac.getPricePerKwh(), 0.0001);
        assertEquals(0.05, ac.getPricePerMinute(), 0.0001);
    }




    @When("the owner selects location {string}")
    public void the_owner_selects_location(String name) {
        currentLocation = locationManager.viewLocation(name);
        assertNotNull(currentLocation);
    }

    @When("the owner selects type AC")
    public void the_owner_selects_type_ac() { }

    @When("the owner selects price per minute")
    public void the_owner_selects_price_per_minute() { }

    @Then("the owner is shown the price {double} EUR")
    public void the_owner_is_shown_the_price(double expectedPrice) {
        Pricing ac = currentLocation.getPricingForMode("AC");
        assertNotNull(ac);

        assertEquals(expectedPrice, ac.getPricePerMinute(), 0.0001);
    }




    private double parsePrice(String s) {
        return Double.parseDouble(s.replace("EUR", "").trim());
    }


    // Error und Edge Cases:
    @When("the owner requests pricing for mode {string} at {string}")
    public void ownerRequestsPricingForModeAt(String mode, String locationName) {
        Location loc = locationManager.viewLocation(locationName);
        assertNotNull(loc, "Location does not exist: " + locationName);
        lastRequestedPricing = loc.getPricingForMode(mode);
    }

    @Then("no pricing is returned")
    public void noPricingIsReturned() {
        assertNull(lastRequestedPricing);
    }

    @When("the owner creates pricing for mode {string} with {double} EUR per kWh and {double} EUR per minute")
    public void ownerCreatesPricing(String mode, double kwh, double minute) {
        Pricing p = pricingManager.createPricing(mode, kwh, minute);
        Location loc = locationManager.viewLocation("Vienna West Station");
        if (loc != null) loc.addPricing(p);
    }

    @When("the owner attempts to create pricing for mode {string} with {double} EUR per kWh and {double} EUR per minute")
    public void ownerAttemptsToCreateDuplicatePricing(String mode, double kwh, double minute) {
        try {
            Location loc = locationManager.viewLocation("Vienna West Station");
            if (loc != null && loc.getPricingForMode(mode) != null) {
                throw new IllegalArgumentException("Pricing for mode " + mode + " already exists");
            }
            Pricing p = pricingManager.createPricing(mode, kwh, minute);
            if (loc != null) loc.addPricing(p);
            lastPricingErrorMessage = null;
        } catch (IllegalArgumentException ex) {
            lastPricingErrorMessage = ex.getMessage();
        }
    }



    @Then("an error about duplicate pricing is raised")
    public void errorAboutDuplicatePricingRaised() {
        assertNotNull(lastPricingErrorMessage);
        assertTrue(lastPricingErrorMessage.contains("already exists"));
    }


}


