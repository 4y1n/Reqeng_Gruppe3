package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Chargers_Steps {

    private final LocationManager locationManager;
    private final ChargerManager chargerManager;
    private Charger currentCharger;
    private String viewedChargerListOutput;

    public Chargers_Steps() {
        this.chargerManager = ChargerManager.getInstance();
        this.locationManager = LocationManager.getInstance();
    }




    @Given("a location {string} exists for chargers")
    public void chargerLocationExists(String locationName) {
        if (locationManager.viewLocation(locationName) == null) {
            locationManager.createLocation(locationName);
        }
    }

    @When("owner creates a charger with ID {string} at location {string}")
    public void ownerCreatesCharger(String id, String locationName) {
        Location loc = locationManager.viewLocation(locationName);
        assertNotNull(loc);

        currentCharger = chargerManager.createCharger(id, loc);
    }

    @And("sets the charger type to {string}")
    public void setChargerType(String type) {
        currentCharger.setType(type);
    }

    @And("sets the charger status to {string}")
    public void setChargerStatus(String status) {
        currentCharger.setStatus(status);
    }

    @Then("the charger {string} is part of the charger list")
    public void chargerExists(String id) {
        assertNotNull(chargerManager.viewCharger(id));
    }

    @Then("the charger type is {string}")
    public void chargerTypeIs(String expected) {
        assertEquals(expected, currentCharger.getType());
    }

    @Then("the charger status is {string}")
    public void chargerStatusIs(String expected) {
        assertEquals(expected, currentCharger.getStatus());
    }

    @Then("the charger belongs to location {string}")
    public void chargerBelongsTo(String location) {
        assertEquals(location, currentCharger.getLocation().getName());
    }


    @Given("the following chargers exist:")
    public void chargersExist(DataTable table) {
        chargerManager.clearChargers();

        for (Map<String, String> row : table.asMaps()) {
            String locName = row.get("Location");

            Location loc = locationManager.viewLocation(locName);
            if (loc == null) loc = locationManager.createLocation(locName);

            Charger c = chargerManager.createCharger(row.get("ID"), loc);
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
        Charger c = chargerManager.viewCharger(id);
        assertNotNull(c);
        c.setStatus(newStatus);
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
