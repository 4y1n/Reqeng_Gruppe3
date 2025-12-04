package org.example;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomerManager {

    private static final CustomerManager INSTANCE = new CustomerManager();
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    private CustomerManager() {}

    public static CustomerManager getInstance() {
        return INSTANCE;
    }

    public CustomerManager clearCustomers() {
        customers.clear();
        return this;
    }


    public Customer createCustomer(String id, String name, String email) {
        if (customers.containsKey(id)) {
            throw new IllegalArgumentException("Customer already exists: " + id);
        }
        Customer c = new Customer(id, name, email);
        customers.put(id, c);
        return c;
    }

    public Customer createCustomer(String id) {
        if (customers.containsKey(id)) {
            throw new IllegalArgumentException("Customer already exists: " + id);
        }
        Customer c = new Customer(id);
        customers.put(id, c);
        return c;
    }

    public Customer viewCustomer(String id) {
        return customers.get(id);
    }

    public Customer updateCustomer(String id, String name, String email) {
        Customer c = viewCustomer(id);
        if (c == null) {
            throw new RuntimeException("Customer not found: " + id);
        }
        if (name != null) c.setName(name);
        if (email != null) c.setEmail(email);
        return c;
    }

    public void deleteCustomer(String id) {
        customers.remove(id);
    }

    public Map<String, Customer> getAllCustomers() {
        return customers;
    }

    public int getNumberOfCustomers() {
        return customers.size();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customers:\n");
        for (Customer c : customers.values()) {
            sb.append("  ").append(c.getId())
                    .append(": ")
                    .append(c.getName() == null ? "<unknown>" : c.getName())
                    .append(" (")
                    .append(c.getEmail() == null ? "<no-email>" : c.getEmail())
                    .append(") - credit: ")
                    .append(String.format(java.util.Locale.US, "%.2f", c.getCredit()))
                    .append("\n");
        }
        return sb.toString().trim();
    }
}
