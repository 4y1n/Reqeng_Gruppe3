package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class Pricing_Steps {

    private final LocationManager locationManager = LocationManager.getInstance();
    private final PricingManager pricingManager = PricingManager.getInstance();

    private Location currentLocation;
    private String errorMessage;
    private LocalDateTime currentTime;
    private String lastViewedPricingPrintout;
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

    @Given("the location {string} exists")
    public void the_location_exists(String name) {
        Location loc = locationManager.viewLocation(name);
        if (loc == null) {
            locationManager.createLocation(name);
        }
    }

    @And("the location {string} has pricing:")
    public void the_location_has_pricing(String name, DataTable table) {
        Location loc = locationManager.viewLocation(name);
        assertNotNull(loc, "Location does not exist: " + name);

        for (Map<String, String> row : table.asMaps()) {
            String mode = row.get("Mode");
            double priceKwh = parsePrice(row.get("Price per kWh"));
            double priceMin = parsePrice(row.get("Price per Minute"));

            Pricing p = pricingManager.createPricing(mode, priceKwh, priceMin);
            loc.addPricing(p);
        }
    }


    @When("the owner creates a new location {string}")
    public void owner_creates_new_location(String name) {
        currentLocation = locationManager.createLocation(name);
    }

    @When("the owner sets the pricing for {string} to:")
    public void owner_sets_pricing_for_location(String name, DataTable table) {
        Location loc = locationManager.viewLocation(name);
        assertNotNull(loc, "Location does not exist: " + name);


        for (Map<String, String> row : table.asMaps()) {
            String mode = row.get("Mode");
            double kwh = parsePrice(row.get("Price per kWh"));
            double min = parsePrice(row.get("Price per Minute"));

            if (kwh < 0 || min < 0) {
                errorMessage = "Invalid price: negative values are not allowed";
                return;
            }

            Pricing p = pricingManager.createPricing(mode, kwh, min);
            loc.addPricing(p);
        }
        currentLocation = loc;
    }

    @Then("the pricing for {string} is stored")
    public void the_pricing_is_stored(String name) {
        Location loc = locationManager.viewLocation(name);
        assertNotNull(loc);
        assertFalse(loc.getPricingList().isEmpty());
    }

    @And("all charging processes started after the update at {string} use the stored prices")
    public void allChargingProcessesStartedAfterTheUpdateAtUseTheStoredPrices(String time) {
        LocalDateTime startTime = LocalDateTime.parse(time, dtf);

        assertTrue(
                startTime.isEqual(pricingUpdateTime) || startTime.isAfter(pricingUpdateTime),
                "Charging process did not start after pricing update"
        );

        Pricing ac = currentLocation.getPricingForMode("AC");
        assertNotNull(ac);

        assertEquals(0.33, ac.getPricePerKwh(), 0.0001);
        assertEquals(0.07, ac.getPricePerMinute(), 0.0001);
    }

    @And("charging processes already in progress continue using the old pricing")
    public void chargingProcessesAlreadyInProgressContinueUsingTheOldPricing() {
        assertNotNull(oldPricingSnapshot);

        Pricing oldAc = oldPricingSnapshot.get("AC");
        assertNotNull(oldAc);

        assertEquals(0.30, oldAc.getPricePerKwh(), 0.0001);
        assertEquals(0.05, oldAc.getPricePerMinute(), 0.0001);
    }

    @When("the owner selects location {string}")
    public void the_owner_selects_location(String name) {
        currentLocation = locationManager.viewLocation(name);
        assertNotNull(currentLocation);
    }

    @When("the owner selects type AC")
    public void the_owner_selects_type_ac() { }

    @Then("the system shows:")
    public void the_system_shows(String expected) {
        assertNotNull(currentLocation, "No location selected");
        LocalDateTime asOf = currentTime != null ? currentTime : LocalDateTime.now();

        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------\n");
        sb.append("Pricing for: ").append(currentLocation.getName()).append("\n");
        sb.append("As of: ").append(asOf.format(dtf)).append("\n\n");

        for (Pricing p : currentLocation.getPricingList()) {
            sb.append("Mode: ").append(p.getMode()).append("\n");
            sb.append("  Price per kWh:     ").append(formatPrice(p.getPricePerKwh())).append(" EUR\n");
            sb.append("  Price per Minute:  ").append(formatPrice(p.getPricePerMinute())).append(" EUR\n\n");
        }

        sb.append("------------------------------------\n");

        lastViewedPricingPrintout = sb.toString().trim();

        assertEquals(normalize(expected), normalize(lastViewedPricingPrintout));
    }


    // ---- helpers ----
    private double parsePrice(String s) {
        if (s == null) return 0.0;

        String cleaned = s
                .replace("EUR", "")
                .replace("€", "")
                .trim()
                .replace(',', '.')
                .replaceAll("\\s+", "");

        return Double.parseDouble(cleaned);
    }

    private String formatPrice(double v) {
        return priceFormat.format(v);
    }

    private String normalize(String s) {
        String[] lines = s.replace("\r", "").split("\n");
        return Arrays.stream(lines)
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .collect(Collectors.joining("\n"));
    }


    @When("a new location {string} is created")
    public void aNewLocationIsCreated(String name) {
        locationManager.createLocation(name);
    }

    @And("the location has prices:")
    public void theLocationHasPrices(DataTable table) {
        PricingManager pm = PricingManager.getInstance();
        for (Map<String, String> row : table.asMaps()) {
            pm.createPricing(
                    row.get("Mode"),
                    parsePrice(row.get("Price per kWh")),
                    parsePrice(row.get("Price per Minute"))
            );
        }
    }
}
