package org.example;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class ManageLocations_Steps {

    private LocationManager locationManager;
    private Location currentLocation;
    private String viewedLocationListOutput;

    // --------------------------------------------------------
    // Background
    // --------------------------------------------------------
    @Given("a new FillingStationNetwork")
    public void aNewFillingStationNetwork() {
        locationManager = LocationManager.getInstance().clearLocations();
    }

    // --------------------------------------------------------
    // Create Location
    // --------------------------------------------------------
    @When("owner creates a location with the unique name {string}")
    public void ownerCreatesALocationWithTheUniqueName(String name) {
        currentLocation = locationManager.createLocation(name);
    }

    @And("sets the address to {string}")
    public void setsTheAddressTo(String address) {
        currentLocation.setAddress(address);
    }

    @And("sets the number of chargers to {int}")
    public void setsTheNumberOfChargersTo(Integer chargers) {
        currentLocation.setChargerCount(chargers);
    }

    @Then("the location {string} is part of the location list")
    public void theLocationIsPartOfTheLocationList(String name) {
        assertNotNull(locationManager.readLocation(name));
    }

    @And("the address is {string}")
    public void theAddressIs(String expectedAddress) {
        assertEquals(expectedAddress, currentLocation.getAddress());
    }

    @And("the number of chargers is {int}")
    public void theNumberOfChargersIs(Integer expectedCount) {
        assertEquals(expectedCount.intValue(), currentLocation.getChargerCount());
    }

    // --------------------------------------------------------
    // View Locations
    // --------------------------------------------------------
    @Given("the following locations exist:")
    public void theFollowingLocationsExist(DataTable dataTable) {
        locationManager.clearLocations();

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            Location loc = locationManager.createLocation(row.get("Name"));
            loc.setAddress(row.get("Address"));
            loc.setChargerCount(Integer.parseInt(row.get("Chargers")));
        }
    }

    @When("owner views the list of all locations")
    public void ownerViewsTheListOfAllLocations() {
        viewedLocationListOutput = locationManager.toString();
    }

    @Then("the number of locations is {int}")
    public void theNumberOfLocationsIs(Integer expectedCount) {
        assertEquals(expectedCount.intValue(), locationManager.getNumberOfLocations());
    }

    @And("viewing the location list shows the following output:")
    public void viewingTheLocationListShowsTheFollowingOutput(String docString) {
        assertEquals(docString.trim(), viewedLocationListOutput.trim());
    }

    // --------------------------------------------------------
    // Update Location
    // --------------------------------------------------------
    @Given("an existing location {string}")
    public void anExistingLocation(String name) {
        locationManager.clearLocations();
        currentLocation = locationManager.createLocation(name);
    }

    @When("owner updates the address to {string}")
    public void ownerUpdatesTheAddressTo(String newAddress) {
        currentLocation.setAddress(newAddress);
    }

    @Then("the location {string} has the new address {string}")
    public void theLocationHasTheNewAddress(String name, String expectedAddress) {
        assertEquals(expectedAddress,
                locationManager.readLocation(name).getAddress());
    }

    // --------------------------------------------------------
    // Delete Location
    // --------------------------------------------------------
    @Given("the location {string} exists")
    public void theLocationExists(String name) {
        locationManager.clearLocations();
        locationManager.createLocation(name);
    }

    @When("owner deletes the location {string}")
    public void ownerDeletesTheLocation(String name) {
        locationManager.deleteLocation(name);
    }

    @Then("the location {string} is no longer part of the location list")
    public void theLocationIsNoLongerPartOfTheLocationList(String name) {
        assertNull(locationManager.readLocation(name));
    }
}
