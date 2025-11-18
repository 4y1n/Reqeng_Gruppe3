package org.example;

import java.util.ArrayList;
import java.util.List;

public class CustomerManager {

    private static CustomerManager instance;

    private final List<Customer> customers = new ArrayList<>();

    private CustomerManager() { }

    public static CustomerManager getInstance() {
        if (instance == null) {
            instance = new CustomerManager();
        }
        return instance;
    }

    public CustomerManager clearCustomers() {
        customers.clear();
        return this;
    }

    public Customer createCustomer(String id) {
        Customer c = new Customer(id);
        customers.add(c);
        return c;
    }

    public Customer createCustomer(String id, String name, String email, double credit) {
        Customer c = new Customer(id, name, email, credit);
        customers.add(c);
        return c;
    }

    public Customer viewCustomer(String id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void deleteCustomer(String id) {
        customers.removeIf(c -> c.getId().equals(id));
    }

    public List<Customer> getAllCustomers() {
        return customers;
    }

    public int getNumberOfCustomers() {
        return customers.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Customers:\n");
        for (Customer c : customers) {
            sb.append("  ").append(c.toString()).append("\n");
        }
        return sb.toString().trim();
    }
}
