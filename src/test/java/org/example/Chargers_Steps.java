package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Chargers_Steps {

    private final LocationManager locationManager;
    private final ChargersManager chargerManager;

    private Chargers currentChargers;
    private String viewedChargerListOutput;

    public Chargers_Steps() {
        this.locationManager = LocationManager.getInstance();
        this.chargerManager = ChargersManager.getInstance();
    }




    @Given("a location {string} exists for chargers")
    public void chargerLocationExists(String locationName) {
        if (locationManager.viewLocation(locationName) == null) {
            Location loc = locationManager.createLocation(locationName);
            loc.setAddress("Unknown");
        }
    }

    @When("owner creates a charger with ID {string} at location {string}")
    public void ownerCreatesCharger(String id, String locationName) {
        Location loc = locationManager.viewLocation(locationName);
        assertNotNull(loc);

        currentChargers = chargerManager.createCharger(id, loc);
    }

    @And("sets the charger type to {string}")
    public void setChargerType(String type) {
        currentChargers.setType(type);
    }

    @And("sets the charger status to {string}")
    public void setChargerStatus(String status) {
        currentChargers.setStatus(status);
    }

    @Then("the charger {string} is part of the charger list")
    public void chargerExists(String id) {
        assertNotNull(chargerManager.viewCharger(id));
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




    @When("owner views the list of all chargers")
    public void viewChargerList() {
        viewedChargerListOutput = chargerManager.toString();
    }

    @Then("viewing the charger list shows:")
    public void chargerListShows(String expected) {
        assertEquals(expected.trim(), viewedChargerListOutput.trim());
    }




    @When("owner updates the status of charger {string} to {string}")
    public void updateChargerStatus(String id, String newStatus) {
        Chargers c = chargerManager.viewCharger(id);
        assertNotNull(c);
        Chargers updated = chargerManager.updateCharger(id, null, newStatus, null);
        assertNotNull(updated);
        currentChargers = updated;
    }

    @Then("the charger {string} has status {string}")
    public void chargerHasStatus(String id, String expected) {
        assertEquals(expected, chargerManager.viewCharger(id).getStatus());
    }




    @When("owner deletes the charger {string}")
    public void deleteCharger(String id) {
        chargerManager.deleteCharger(id);
    }

    @Then("the charger {string} no longer exists in the charger list")
    public void chargerNotExists(String id) {
        assertNull(chargerManager.viewCharger(id));
    }
}
