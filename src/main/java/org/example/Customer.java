package org.example;

public class Customer {

    private final String id;
    private String name;
    private String email;
    private double credit;


    public Customer(String id) {
        this.id = id;
    }

    public Customer(String id, String name, String email, double credit) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.credit = credit;
    }

    public Customer(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.credit = 0.0;
    }

    public String getId() {
        return id;
    }

    public Customer setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public Customer setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Customer setCredit(double credit) {
        this.credit = credit;
        return this;
    }

    public double getCredit() {
        return credit;
    }

    public void addCredit(double amount) {
        this.credit += amount;
    }

    public void deductCredit(double amount) {
        if (amount > this.credit) {
            throw new IllegalArgumentException("Insufficient credit.");
        }
        this.credit -= amount;
    }


    @Override
    public String toString() {
        return id + ": " + name + " (" + email + ") - credit: " + credit;
    }
}
