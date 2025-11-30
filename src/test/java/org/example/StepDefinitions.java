package org.example;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

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



// Location

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
        assertNotNull(locationManager.viewLocation(name));
    }

    @And("the address is {string}")
    public void theAddressIs(String expectedAddress) {
        assertEquals(expectedAddress, currentLocation.getAddress());
    }

    @And("the number of chargers is {int}")
    public void theNumberOfChargersIs(Integer expectedCount) {
        assertEquals(expectedCount.intValue(), currentLocation.getChargerCount());
    }


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
                locationManager.viewLocation(name).getAddress());
    }


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
        assertNull(locationManager.viewLocation(name));
    }




// Charger

    @Given("a location {string} exists for chargers")
    public void aLocationExistsForChargers(String locationName) {
        if (locationManager.viewLocation(locationName) == null) {
            locationManager.createLocation(locationName);
        }
    }

    @When("owner creates a charger with ID {string} at location {string}")
    public void ownerCreatesAChargerWithIDAtLocation(String chargerId, String locationName) {
        Location loc = locationManager.viewLocation(locationName);
        assertNotNull(loc);

        currentCharger = chargerManager.createCharger(chargerId, loc);
    }

    @And("sets the charger type to {string}")
    public void setsTheChargerTypeTo(String type) {
        currentCharger.setType(type);
    }

    @And("sets the charger status to {string}")
    public void setsTheChargerStatusTo(String status) {
        currentCharger.setStatus(status);
    }

    @Then("the charger {string} is part of the charger list")
    public void theChargerIsPartOfTheChargerList(String id) {
        assertNotNull(chargerManager.viewCharger(id));
    }

    @Then("the charger type is {string}")
    public void theChargerTypeIs(String expectedType) {
        assertEquals(expectedType, currentCharger.getType());
    }

    @Then("the charger status is {string}")
    public void theChargerStatusIs(String expectedStatus) {
        assertEquals(expectedStatus, currentCharger.getStatus());
    }

    @Then("the charger belongs to location {string}")
    public void theChargerBelongsToLocation(String locationName) {
        assertEquals(locationName, currentCharger.getLocation().getName());
    }





    @Given("the following chargers exist:")
    public void theFollowingChargersExist(DataTable dataTable) {

        chargerManager.clearChargers();

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {

            String locName = row.get("Location");
            Location loc = locationManager.viewLocation(locName);

            if (loc == null) {
                loc = locationManager.createLocation(locName);
            }

            Charger c = chargerManager.createCharger(row.get("ID"), loc);
            c.setType(row.get("Type"));
            c.setStatus(row.get("Status"));
        }
    }

    @When("owner views the list of all chargers")
    public void ownerViewsTheListOfAllChargers() {
        viewedChargerListOutput = chargerManager.toString();
    }

    @Then("viewing the charger list shows:")
    public void viewingTheChargerListShows(String docString) {
        assertEquals(docString.trim(), viewedChargerListOutput.trim());
    }




    @When("owner updates the status of charger {string} to {string}")
    public void ownerUpdatesTheStatusOfChargerTo(String id, String newStatus) {
        Charger c = chargerManager.viewCharger(id);
        assertNotNull(c);

        c.setStatus(newStatus);
    }

    @Then("the charger {string} has status {string}")
    public void theChargerHasStatus(String id, String expectedStatus) {
        assertEquals(expectedStatus, chargerManager.viewCharger(id).getStatus());
    }





    @When("owner deletes the charger {string}")
    public void ownerDeletesTheCharger(String id) {
        chargerManager.deleteCharger(id);
    }

    @Then("the charger {string} no longer exists in the charger list")
    public void theChargerNoLongerExistsInTheChargerList(String id) {
        assertNull(chargerManager.viewCharger(id));
    }





// Customer


    @When("customer creates a customer account with the unique id {string}")
    public void customerCreatesAccountWithId(String id) {
        customerManager = CustomerManager.getInstance();
        currentCustomer = customerManager.createCustomer(id);
    }

    @And("sets the customer name to {string}")
    public void setsTheCustomerNameTo(String name) {
        currentCustomer.setName(name);
    }

    @And("sets the customer email to {string}")
    public void setsTheCustomerEmailTo(String email) {
        currentCustomer.setEmail(email);
    }

    @Then("the customer account {string} is part of the customer account list")
    public void theCustomerAccountIsPartOfTheList(String id) {
        assertNotNull(customerManager.viewCustomer(id));
    }

    @And("the customer name is {string}")
    public void theCustomerNameIs(String expected) {
        assertEquals(expected, currentCustomer.getName());
    }

    @And("the customer email is {string}")
    public void theCustomerEmailIs(String expected) {
        assertEquals(expected, currentCustomer.getEmail());
    }

    @And("the customer credit is {double}")
    public void theCustomerCreditIs(Double expected) {
        assertEquals(expected, currentCustomer.getAccountBalance());
    }






    @Given("the following customer accounts exist:")
    public void theFollowingCustomerAccountsExist(DataTable dataTable) {
        customerManager = CustomerManager.getInstance().clearCustomers();

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            Customer c = customerManager.createCustomer(row.get("Id"));
            c.setName(row.get("Name"));
            c.setEmail(row.get("Email"));
            c.setAccountBalance(Double.parseDouble(row.get("Credit")));
        }
    }




    @When("customer views the customer account with id {string}")
    public void customerViewsAccount(String id) {
        currentCustomer = customerManager.viewCustomer(id);
        assertNotNull(currentCustomer);
    }





    @Given("an existing customer account with id {string}")
    public void anExistingCustomerAccountWithId(String id) {
        customerManager = CustomerManager.getInstance().clearCustomers();
        currentCustomer = customerManager.createCustomer(id);
    }

    @Given("the customer has a credit of {double}")
    public void customerHasCredit(Double amount) {
        currentCustomer.setAccountBalance(amount);
    }

    @When("customer adds {double} credit to the customer account")
    public void customerAddsCredit(Double amount) {
        currentCustomer.addCredit(amount);
    }

    @Then("the customer account {string} has a credit of {double}")
    public void customerAccountHasCredit(String id, Double expected) {
        Customer c = customerManager.viewCustomer(id);
        assertNotNull(c);
        assertEquals(expected, c.getAccountBalance());
    }






    @When("owner views the list of all customer accounts")
    public void ownerViewsCustomerList() {
        viewedCustomerListOutput = customerManager.toString();
    }

    @Then("the number of customer accounts is {int}")
    public void theNumberOfCustomerAccountsIs(Integer count) {
        assertEquals(count.intValue(), customerManager.getNumberOfCustomers());
    }

    @And("viewing the customer account list shows the following output:")
    public void viewingCustomerListShows(String docString) {
        assertEquals(docString.trim(), viewedCustomerListOutput.trim());
    }

}
