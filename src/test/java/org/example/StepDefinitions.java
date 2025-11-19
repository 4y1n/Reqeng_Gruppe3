package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class StepDefinitions {

    private LocationManager locationManager;
    private ChargerManager chargerManager;
    private CustomerManager customerManager;

    private Location currentLocation;
    private Charger currentCharger;
    private Customer currentCustomer;

    private String viewedLocationListOutput;
    private String viewedChargerListOutput;
    private String viewedCustomerListOutput;



    @Given("a new FillingStationNetwork")
    public void aNewFillingStationNetwork() {
        locationManager = LocationManager.getInstance().clearLocations();
        chargerManager = ChargerManager.getInstance().clearChargers();
        customerManager = CustomerManager.getInstance().clearCustomers();
    }



    //Location

    @When("owner creates a location with the unique name {string}")
    public void ownerCreatesLocation(String name) {
        currentLocation = locationManager.createLocation(name);
    }

    @And("sets the address to {string}")
    public void setsLocationAddress(String address) {
        currentLocation.setAddress(address);
    }

    @And("sets the number of chargers to {int}")
    public void setsLocationChargerCount(Integer count) {
        currentLocation.setChargerCount(count);
    }

    @Then("the location {string} is part of the location list")
    public void locationExists(String name) {
        assertNotNull(locationManager.viewLocation(name));
    }

    @And("the address is {string}")
    public void addressIs(String expected) {
        assertEquals(expected, currentLocation.getAddress());
    }

    @And("the number of chargers is {int}")
    public void chargerCountIs(Integer expected) {
        assertEquals(expected.intValue(), currentLocation.getChargerCount());
    }


    @Given("the following locations exist:")
    public void locationsExist(DataTable table) {
        locationManager.clearLocations();

        for (Map<String, String> row : table.asMaps()) {
            Location loc = locationManager.createLocation(row.get("Name"));
            loc.setAddress(row.get("Address"));
            loc.setChargerCount(Integer.parseInt(row.get("Chargers")));
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
        locationManager.clearLocations();
        currentLocation = locationManager.createLocation(name);
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



    // Charger

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



    //Customer

    @When("customer creates a customer account with the unique id {string}")
    public void customerCreatesAccount(String id) {
        currentCustomer = customerManager.createCustomer(id);
    }

    @And("sets the customer name to {string}")
    public void setCustomerName(String name) {
        currentCustomer.setName(name);
    }

    @And("sets the customer email to {string}")
    public void setCustomerEmail(String email) {
        currentCustomer.setEmail(email);
    }

    @Then("the customer account {string} is part of the customer account list")
    public void customerExists(String id) {
        assertNotNull(customerManager.viewCustomer(id));
    }

    @And("the customer name is {string}")
    public void checkCustomerName(String expected) {
        assertEquals(expected, currentCustomer.getName());
    }

    @And("the customer email is {string}")
    public void checkCustomerEmail(String expected) {
        assertEquals(expected, currentCustomer.getEmail());
    }

    @And("the customer credit is {double}")
    public void checkCustomerCredit(Double expected) {
        assertEquals(expected, currentCustomer.getCredit());
    }


    @Given("the following customer accounts exist:")
    public void customerAccountsExist(DataTable table) {
        customerManager.clearCustomers();

        for (Map<String, String> row : table.asMaps()) {
            Customer c = customerManager.createCustomer(row.get("Id"));
            c.setName(row.get("Name"));
            c.setEmail(row.get("Email"));
            c.setCredit(Double.parseDouble(row.get("Credit")));
        }
    }


    @When("customer views the customer account with id {string}")
    public void viewCustomerAccount(String id) {
        currentCustomer = customerManager.viewCustomer(id);
        assertNotNull(currentCustomer);
    }




    @When("customer updates the customer name of {string} to {string}")
    public void updateCustomerName(String id, String newName) {
        Customer c = customerManager.viewCustomer(id);
        assertNotNull(c);
        c.setName(newName);
        currentCustomer = c;
    }

    @When("customer updates the customer email of {string} to {string}")
    public void updateCustomerEmail(String id, String newEmail) {
        Customer c = customerManager.viewCustomer(id);
        assertNotNull(c);
        c.setEmail(newEmail);
        currentCustomer = c;
    }



    @When("customer deletes the customer account {string}")
    public void deleteCustomer(String id) {
        customerManager.deleteCustomer(id);
    }

    @Then("the customer account {string} no longer exists")
    public void customerNotExists(String id) {
        assertNull(customerManager.viewCustomer(id));
    }

    @Then("the number of customer accounts is {int}")
    public void theNumberOfCustomerAccountsIs(Integer expectedCount) {
        assertEquals(expectedCount.intValue(), customerManager.getNumberOfCustomers());
    }




    @Given("an existing customer account with id {string}")
    public void existingCustomer(String id) {
        customerManager.clearCustomers();
        currentCustomer = customerManager.createCustomer(id);
    }

    @Given("the customer has a credit of {double}")
    public void customerHasCredit(Double amount) {
        currentCustomer.setCredit(amount);
    }

    @When("customer adds {double} credit to the customer account")
    public void addCredit(Double amount) {
        currentCustomer.addCredit(amount);
    }

    @Then("the customer account {string} has a credit of {double}")
    public void checkUpdatedCredit(String id, Double expected) {
        Customer c = customerManager.viewCustomer(id);
        assertNotNull(c);
        assertEquals(expected, c.getCredit());
    }


}
