package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Locations_Steps {

    private final LocationManager locationManager;
    private Location currentLocation;
    private String viewedLocationListOutput;
    private String lastErrorMessage;

    public Locations_Steps() {
        this.locationManager = LocationManager.getInstance();
    }




    @When("the following locations are created:")
    public void createMultipleLocations(DataTable table) {
        locationManager.clearLocations();

        for (Map<String, String> row : table.asMaps()) {
            Location loc = locationManager.createLocation(row.get("Name"));
            loc.setAddress(row.get("Address"));
        }
    }




    @Given("the following locations exist:")
    public void locationsExist(DataTable table) {
        locationManager.clearLocations();

        for (Map<String, String> row : table.asMaps()) {
            Location loc = locationManager.createLocation(row.get("Name"));
            loc.setAddress(row.get("Address"));
            // Chargers handled elsewhere
        }
    }




    @When("owner views the list of all locations")
    public void viewLocationList() {
        viewedLocationListOutput = locationManager.toString();
    }

    @Then("the number of locations is {int}")
    public void numberOfLocationsIs(Integer expected) {
        assertEquals(expected.intValue(), locationManager.getNumberOfLocations());
    }

    @And("viewing the location list shows the following output:")
    public void locationListOutput(String expected) {
        assertEquals(expected.trim(), viewedLocationListOutput.trim());
    }




    @Given("an existing location {string}")
    public void ensureLocationExists(String name) {

        // IMPORTANT: Do NOT clearInvoices the system here
        Location loc = locationManager.viewLocation(name);
        if (loc == null) {
            loc = locationManager.createLocation(name);
        }
        currentLocation = loc;
    }

    @When("owner updates the address to {string}")
    public void ownerUpdatesAddress(String newAddress) {
        currentLocation.setAddress(newAddress);
    }

    @Then("the location {string} has the new address {string}")
    public void locationHasNewAddress(String name, String expected) {
        assertEquals(expected, locationManager.viewLocation(name).getAddress());
    }




    @Given("the location {string} exists")
    public void locationExistsOnly(String name) {
        locationManager.clearLocations();
        locationManager.createLocation(name);
    }

    @When("owner deletes the location {string}")
    public void deleteLocation(String name) {
        locationManager.deleteLocation(name);
    }

    @Then("the location {string} is no longer part of the location list")
    public void locationNotExists(String name) {
        assertNull(locationManager.viewLocation(name));
    }


    // Error und Edge Cases:
    @When("owner attempts to create a location {string}")
    public void ownerAttemptsToCreateLocation(String name) {
        try {
            locationManager.createLocation(name);
            lastErrorMessage = null;
        } catch (IllegalArgumentException ex) {
            lastErrorMessage = ex.getMessage();
        }
    }

    @Then("an error about duplicate location is raised")
    public void errorAboutDuplicateLocationRaised() {
        assertNotNull(lastErrorMessage);
        assertTrue(lastErrorMessage.contains("already exists"));
    }

    @When("owner renames the location from {string} to {string}")
    public void ownerRenamesLocation(String oldName, String newName) {
        try {
            locationManager.updateLocation(oldName, newName);
            lastErrorMessage = null;
        } catch (IllegalArgumentException ex) {
            lastErrorMessage = ex.getMessage();
        }
    }

    @Then("the location {string} still exists")
    public void theLocationStillExists(String name) {
        assertNotNull(locationManager.viewLocation(name));
    }
}
