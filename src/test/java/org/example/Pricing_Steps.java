package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pricing_Steps {
    private void applyPrice(Location loc, String mode, double perKwh, double perMin) {
        Price price = prices.getOrDefault(loc, new Price());

        if (mode.equalsIgnoreCase("AC")) {
            price.setPricekWhAC(perKwh);
            price.setPriceMinAC(perMin);
        } else if (mode.equalsIgnoreCase("DC")) {
            price.setPricekWhDC(perKwh);
            price.setPriceMinDC(perMin);
        }

        prices.put(loc, price);
    }

    private double getPricePerMinute(Location loc, String mode) {
        Price p = prices.get(loc);

        return mode.equalsIgnoreCase("AC")
                ? p.getPriceMinAC()
                : p.getPriceMinDC();
    }

    private double calculateCost(Location loc, String mode, double kWh, int minutes) {
        Price p = prices.get(loc);
        return p.calculate(mode, kWh, minutes);
    }

    private final LocationManager locationManager = LocationManager.getInstance();

    private final Map<Location, Price> prices = new HashMap<>();

    private Location currentLocation;
    private double queriedPrice;


    @Given("a location {string} exists")
    public void a_location_exists(String location) {
        currentLocation = locationManager.viewLocation(location);
        if (currentLocation == null)
            currentLocation = locationManager.createLocation(location);
    }
    @Given("has prices:")
    public void hasPrices(DataTable table) {
        List<Map<String, String>> rows = table.asMaps();
        for (Map<String, String> row : rows) {
            String mode = row.get("Mode");
            double perKwh = Double.parseDouble(row.get("Price per kWh").split(" ")[0]);
            double perMin = Double.parseDouble(row.get("Price per Minute").split(" ")[0]);
            applyPrice(currentLocation, mode, perKwh, perMin);
        }
    }

    @When("a new location {string} is created")
    public void aNewLocationIsCreated(String location) {
        currentLocation = locationManager.createLocation(location);
    }

    @And("owner sets the price for location {string} to:")
    public void ownerSetsThePriceForLocationTo(String location, DataTable table) {
        currentLocation = locationManager.viewLocation(location);
        List<Map<String, String>> rows = table.asMaps();
        for (Map<String, String> row : rows) {
            String mode = row.get("Mode");
            double perKwh = Double.parseDouble(row.get("Price per kWh").split(" ")[0]);
            double perMin = Double.parseDouble(row.get("Price per Minute").split(" ")[0]);
            applyPrice(currentLocation, mode, perKwh, perMin);
        }
    }

    @Then("the new prices are stored")
    public void theNewPricesAreStored() {
        if (!prices.containsKey(currentLocation)){
            throw new AssertionError("Prices not stored for location.");
        }

    }

    @And("all charging processes started after that at {string} use the prices")

    public void allChargingProcessesStartedAfterThatAtUseThePrices(String location) {
        Location loc = locationManager.viewLocation(location);
        if (loc == null) {
            throw new AssertionError("Location not found: " + location);
        }

        Price newPrice = prices.get(loc);
        if (newPrice == null) {
            throw new AssertionError("No stored prices for location: " + location);
        }

        ChargingProcess cp = new ChargingProcess(5, 10, newPrice);
        Charger charger = new Charger("CH-123", "AC", "available", loc);
        cp.setCharger(charger);

        cp.calculateBilling();

        double expected = calculateCost(loc, "AC", 10, 5);

        if (cp.getTotalPrice() != expected) {
            throw new AssertionError("New charging processes at " + location +
                    " must use updated prices! Expected: " + expected +
                    " but got: " + cp.getTotalPrice());
        }
    }

    @And("charging sessions already in progress continue with the old prices")
    public void chargingSessionsAlreadyInProgressContinueWithTheOldPrices() {

        Price oldPrice = prices.get(currentLocation);

        ChargingProcess cp = new ChargingProcess(5, 10, oldPrice);
        cp.setCharger(new Charger("CH-123", "AC", "available", currentLocation));
        cp.calculateBilling();
        double oldCost = cp.getTotalPrice();


        applyPrice(currentLocation, "AC", 0.99, 0.99);

        cp.calculateBilling();

        if (cp.getTotalPrice() != oldCost) {
            throw new AssertionError("Ongoing charging processes must keep OLD prices!");
        }
    }


    @When("owner selects location {string}")
    public void ownerSelectsLocation(String location) {
        currentLocation = locationManager.viewLocation(location);
    }

    @And("selects type AC")
    public void selectsTypeAC() {
       //context
    }

    @And("selects price per minute")
    public void selectsPricePerMinute() {
        queriedPrice = getPricePerMinute(currentLocation, "AC");
    }

    @Then("he is shown the price {double} EUR")
    public void heIsShownThePriceEUR(double expected) {
        if (queriedPrice != expected) {
            throw new AssertionError("Expected " + expected + " but got " + queriedPrice);
        }
    }


}
