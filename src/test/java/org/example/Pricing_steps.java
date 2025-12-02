package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class Pricing_steps {
    private Map<String, Map<String, Price>> locationPrices = new HashMap<>();
    private String currentLocation;
    private double currentPrice;
    private LocalDateTime lastUpdateTime;

    static class Price {
        double perKWh;
        double perMinute;

        Price(double perKWh, double perMinute) {
            this.perKWh = perKWh;
            this.perMinute = perMinute;
        }
        @Given("a location {string} exists")
        public void a_location_exists(String location) {
            double locationPrices;
            String currentLocation = location;
        }
        @And("has prices:")
        public void has_prices(DataTable dataTable) {
            Map<String, Price> prices = new HashMap<>();
            for (Map<String, String> row : dataTable.asMaps()) {
                String mode = row.get("Mode");
                double perKWh = Double.parseDouble(row.get("Price per kWh").replace(" EUR", ""));
                double perMinute = Double.parseDouble(row.get("Price per Minute").replace(" EUR", ""));
                prices.put(mode, new Price(perKWh, perMinute));
            }
            locationPrices.put(currentLocation, prices);
        }

    }
}
