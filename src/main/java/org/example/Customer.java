package org.example;

public class Customer {

    private final String id;
    private String name;
    private String email;
    private double credit;

    public Customer(String id) {
        this.id = id;
        this.name = "unknown";
        this.email = "unknown";
        this.credit = 0.0;
    }

    public Customer(String id, String name, String email, double accountBalance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.credit = accountBalance;
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
        return credit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountBalance(double accountBalance) {
        this.credit = accountBalance;
    }

    public void addCredit(double amount) {
        this.credit += amount;
    }

    @Override
    public String toString() {
        return id + ": " + name + " (" + email + ") - credit: " + credit;
    }
}
