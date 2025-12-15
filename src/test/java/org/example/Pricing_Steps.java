package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class Pricing_Steps {

    private final LocationManager locationManager = LocationManager.getInstance();
    private final PricingManager pricingManager = PricingManager.getInstance();

    private Location currentLocation;

    private LocalDateTime currentTime;
    private LocalDateTime pricingUpdateTime;

    private Map<String, Pricing> oldPricingSnapshot;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DecimalFormat priceFormat;

    public Pricing_Steps() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        priceFormat = new DecimalFormat("0.00", symbols);
        priceFormat.setMinimumFractionDigits(2);
        priceFormat.setGroupingUsed(false);
    }


    @Given("the current time is {string}")
    public void the_current_time_is(String time) {
        time = time.trim().replace('\u00A0', ' ');
        currentTime = LocalDateTime.parse(time, dtf);
    }


    @And("the location has prices:")
    public void the_location_has_prices(DataTable table) {
        assertNotNull(locationManager, "LocationManager not available.");

        if (currentLocation == null) {
            currentLocation = locationManager.viewLocation("Vienna West Station");
        }
        assertNotNull(currentLocation, "No current location found. Ensure 'the location \"...\" exists' runs before this step.");

        for (Map<String, String> row : table.asMaps()) {
            String mode = row.get("Mode");
            double kwh = parsePrice(row.get("Price per kWh"));
            double min = parsePrice(row.get("Price per Minute"));

            assertTrue(kwh >= 0 && min >= 0, "Invalid price: negative values are not allowed");

            upsertPricing(currentLocation, mode, kwh, min);
        }
    }



    @When("a new location {string} is created")
    public void a_new_location_is_created(String name) {
        currentLocation = locationManager.createLocation(name);
        assertNotNull(currentLocation, "Location could not be created: " + name);
    }

    @When("the owner sets the pricing for {string} to:")
    public void the_owner_sets_the_pricing_for_to(String locationName, DataTable table) {
        Location loc = locationManager.viewLocation(locationName);
        assertNotNull(loc, "Location does not exist: " + locationName);

        oldPricingSnapshot = new HashMap<>();
        for (Pricing p : loc.getPricingList()) {
            oldPricingSnapshot.put(
                    p.getMode().toUpperCase(),
                    new Pricing(p.getMode(), p.getPricePerKwh(), p.getPricePerMinute())
            );
        }

        for (Map<String, String> row : table.asMaps()) {
            String mode = row.get("Mode");
            double kwh = parsePrice(row.get("Price per kWh"));
            double min = parsePrice(row.get("Price per Minute"));

            assertTrue(kwh >= 0 && min >= 0, "Invalid price: negative values are not allowed");

            upsertPricing(loc, mode, kwh, min);
        }

        currentLocation = loc;
        pricingUpdateTime = currentTime;
    }

    @When("the owner selects location {string}")
    public void the_owner_selects_location(String name) {
        currentLocation = locationManager.viewLocation(name);
        assertNotNull(currentLocation, "Location does not exist: " + name);
    }



    @Then("the pricing for {string} is stored")
    public void the_pricing_for_is_stored(String name) {
        Location loc = locationManager.viewLocation(name);
        assertNotNull(loc, "Location does not exist: " + name);

        assertNotNull(loc.getPricingForMode("AC"), "Expected AC pricing to be stored for " + name);
        assertNotNull(loc.getPricingForMode("DC"), "Expected DC pricing to be stored for " + name);
    }

    @And("all charging processes started after the update at {string} use the stored prices")
    public void all_charging_processes_started_after_the_update_at_use_the_stored_prices(String time) {
        LocalDateTime startTime = LocalDateTime.parse(time.trim(), dtf);

        assertNotNull(pricingUpdateTime, "No pricing update time recorded (pricingUpdateTime is null).");
        assertTrue(
                startTime.isEqual(pricingUpdateTime) || startTime.isAfter(pricingUpdateTime),
                "Charging process did not start after pricing update"
        );

        assertNotNull(currentLocation, "No location selected.");
        assertNotNull(currentLocation.getPricingForMode("AC"), "AC pricing missing after update.");
        assertNotNull(currentLocation.getPricingForMode("DC"), "DC pricing missing after update.");
    }

    @And("charging processes already in progress continue using the old pricing")
    public void charging_processes_already_in_progress_continue_using_the_old_pricing() {
        assertNotNull(oldPricingSnapshot, "Old pricing snapshot was not captured.");

        Pricing oldAc = oldPricingSnapshot.get("AC");
        Pricing oldDc = oldPricingSnapshot.get("DC");

        assertNotNull(oldAc, "Old AC pricing snapshot missing.");
        assertNotNull(oldDc, "Old DC pricing snapshot missing.");
    }

    @Then("the system shows:")
    public void the_system_shows(String expected) {
        assertNotNull(currentLocation, "No location selected.");
        assertNotNull(currentTime, "Current time must be set via 'Given the current time is \"yyyy-MM-dd HH:mm\"'.");

        String actual = buildPricingView(currentLocation, currentTime);
        assertEquals(normalize(expected), normalize(actual));
    }



    private void upsertPricing(Location loc, String mode, double priceKwh, double priceMin) {
        Pricing existing = loc.getPricingForMode(mode);
        if (existing != null) {
            existing.setPricePerKwh(priceKwh);
            existing.setPricePerMinute(priceMin);
        } else {
            Pricing p = pricingManager.createPricing(mode, priceKwh, priceMin);
            loc.addPricing(p);
        }
    }

    private double parsePrice(String s) {
        if (s == null) return 0.0;

        String cleaned = s
                .replace("EUR", "")
                .replace("€", "")
                .trim()
                .replace('\u00A0', ' ')
                .replace(',', '.')
                .replaceAll("\\s+", "");

        return Double.parseDouble(cleaned);
    }

    private String formatPrice(double v) {
        return priceFormat.format(v);
    }

    private String normalize(String s) {
        String normalized = s.replace("\r", "").trim();
        return Arrays.stream(normalized.split("\n"))
                .map(line -> line.replaceAll("\\s+$", ""))
                .collect(Collectors.joining("\n"))
                .trim();
    }

    private String buildPricingView(Location loc, LocalDateTime asOf) {
        Pricing ac = loc.getPricingForMode("AC");
        Pricing dc = loc.getPricingForMode("DC");

        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------\n");
        sb.append("Pricing for: ").append(loc.getName()).append("\n");
        sb.append("As of: ").append(asOf.format(dtf)).append("\n\n");


        if (ac != null) {
            sb.append("Mode: AC\n");
            sb.append("  Price per kWh:     ").append(formatPrice(ac.getPricePerKwh())).append(" EUR\n");
            sb.append("  Price per Minute:  ").append(formatPrice(ac.getPricePerMinute())).append(" EUR\n\n");
        }

        if (dc != null) {
            sb.append("Mode: DC\n");
            sb.append("  Price per kWh:     ").append(formatPrice(dc.getPricePerKwh())).append(" EUR\n");
            sb.append("  Price per Minute:  ").append(formatPrice(dc.getPricePerMinute())).append(" EUR\n");
        }

        sb.append("------------------------------------");
        return sb.toString();
    }
}
