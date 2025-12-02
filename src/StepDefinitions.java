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




    // Charger





    //Customer




}
