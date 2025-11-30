package org.example;

public class Customer {

    private final String id;
    private String name;
    private String email;
    private double accountBalance;

    public Customer(String id) {
        this.id = id;
        this.name = "unknown";
        this.email = "unknown";
        this.accountBalance = 0.0;
    }

    public Customer(String id, String name, String email, double accountBalance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.accountBalance = accountBalance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public void addCredit(double amount) {
        this.accountBalance += amount;
    }

    @Override
    public String toString() {
        return id + ": " + name + " (" + email + ") - accountBalance: " + accountBalance;
    }
}
