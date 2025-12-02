package org.example;

import io.cucumber.java.en.Given;

public class Network_Steps {

    @Given("a new FillingStationNetwork")
    public void aNewFillingStationNetwork() {
        LocationManager.getInstance().clearLocations();
        ChargerManager.getInstance().clearChargers();
        CustomerManager.getInstance().clearCustomers();
        InvoiceManager.getInstance().clearInvoices();
    }
}
