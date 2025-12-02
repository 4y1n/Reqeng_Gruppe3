package org.example;

import java.time.LocalDateTime;

public class CustomerCredit {
    private final String id;
    private LocalDateTime date;
    private double billing;

    public CustomerCredit(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getBilling() {
        return billing;
    }

    public void setBilling(double billing) {
        this.billing = billing;
    }
}
