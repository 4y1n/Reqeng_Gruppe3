

package org.example;


public class NetworkStatus {

    public static void print() {
        System.out.println("\n=== NETWORK STATUS ===");

        System.out.println("Locations: " + LocationManager.getInstance().getNumberOfLocations());
        System.out.println("Chargers: " + ChargerManager.getInstance().getAllChargers().size());
        System.out.println("Customers: " + CustomerManager.getInstance().getAllCustomers().size());
        System.out.println("Pricing entries: " + PricingManager.getInstance().getPricingList().size());
        System.out.println("Invoices: " + InvoiceManager.getInstance().getAllInvoices().size());

        System.out.println("========================");
    }
}
