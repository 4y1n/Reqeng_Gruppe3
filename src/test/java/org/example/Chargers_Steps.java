package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Chargers_Steps {

    private final LocationManager locationManager;
    private final ChargersManager chargerManager;
    private Exception lastException;

    private Chargers currentCharger;
    private String viewedChargerListOutput;

    public Chargers_Steps() {
        this.locationManager = LocationManager.getInstance();
        this.chargerManager = ChargersManager.getInstance();
    }

    // ----------------------
    // GIVEN STEPS
    // ----------------------

    @Given("a location {string} exists for chargers")
    public void chargerLocationExists(String locationName) {
        if (locationManager.viewLocation(locationName) == null) {
            Location loc = locationManager.createLocation(locationName);
            loc.setAddress("Unknown");
        }
    }

    @Given("the following chargers exist:")
    public void chargersExist(DataTable table) {
        chargerManager.clearChargers();
        for (Map<String, String> row : table.asMaps()) {
            String locName = row.get("Location");
            Location loc = locationManager.viewLocation(locName);
            if (loc == null) {
                loc = locationManager.createLocation(locName);
                loc.setAddress("Unknown");
            }
            Chargers c = chargerManager.createCharger(row.get("ID"), loc);
            c.setType(row.get("Type"));
            c.setStatus(row.get("Status"));
        }
    }

    @Given("the following charger exists:")
    public void theFollowingChargerExists(DataTable table) {
        chargersExist(table); // Reuse the existing method
    }

    // ----------------------
    // WHEN STEPS
    // ----------------------

    @When("owner creates a charger with ID {string} at location {string}")
    public void ownerCreatesCharger(String id, String locationName) {
        Location loc = locationManager.viewLocation(locationName);
        assertNotNull(loc);
        currentCharger = chargerManager.createCharger(id, loc);
    }

    @When("owner attempts to create a charger with ID {string} at location {string}")
    public void ownerAttemptsToCreateAChargerWithIDAtLocation(String id, String locationName) {
        try {
            Location loc = locationManager.viewLocation(locationName);
            if (loc == null) {
                loc = locationManager.createLocation(locationName);
                loc.setAddress("Unknown");
            }
            currentCharger = chargerManager.createCharger(id, loc);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("owner attempts to create a charger with long ID {string} followed by {int} {string} characters at location {string}")
    public void ownerAttemptsToCreateAChargerWithLongID(String baseId, int count, String chars, String locationName) {
        StringBuilder sb = new StringBuilder(baseId);
        for (int i = 0; i < count; i++) sb.append(chars);
        ownerAttemptsToCreateAChargerWithIDAtLocation(sb.toString(), locationName);
    }

    @When("owner updates the status of charger {string} to {string}")
    public void updateChargerStatus(String id, String newStatus) {
        Chargers c = chargerManager.viewCharger(id);
        assertNotNull(c);
        currentCharger = chargerManager.updateCharger(id, null, newStatus, null);
    }

    @When("owner attempts to update the status of charger {string} to {string}")
    public void ownerAttemptsToUpdateTheStatusOfCharger(String id, String newStatus) {
        try {
            currentCharger = chargerManager.updateCharger(id, null, newStatus, null);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("owner deletes the charger {string}")
    public void deleteCharger(String id) {
        chargerManager.deleteCharger(id);
    }

    @When("owner attempts to delete the charger {string}")
    public void ownerAttemptsToDeleteTheCharger(String id) {
        try {
            chargerManager.deleteCharger(id);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("owner views the list of all chargers")
    public void viewChargerList() {
        viewedChargerListOutput = chargerManager.toString();
    }

    // ----------------------
    // THEN STEPS
    // ----------------------

    @Then("the charger {string} is part of the charger list")
    public void chargerExists(String id) {
        assertNotNull(chargerManager.viewCharger(id));
    }

    @Then("the charger with long ID {string} followed by {int} {string} characters is part of the charger list")
    public void chargerIsPartOfListLongID(String baseId, int count, String chars) {
        StringBuilder sb = new StringBuilder(baseId);
        for (int i = 0; i < count; i++) sb.append(chars);
        assertNotNull(chargerManager.viewCharger(sb.toString()));
    }

    @Then("the charger {string} no longer exists in the charger list")
    public void chargerNoLongerExists(String id) {
        assertNull(chargerManager.viewCharger(id));
    }

    @Then("the charger {string} has status {string}")
    public void chargerHasStatus(String id, String expected) {
        assertEquals(expected, chargerManager.viewCharger(id).getStatus());
    }

    @Then("viewing the charger list shows:")
    public void chargerListShows(String expected) {
        assertEquals(expected.trim(), viewedChargerListOutput.trim());
    }

    @Then("an error message for charger is shown: {string}")
    public void anErrorMessageForChargerIsShown(String expectedMessage) {
        assertNotNull(lastException, "Expected an exception but none was thrown.");
        assertEquals(expectedMessage, lastException.getMessage());
    }
}
