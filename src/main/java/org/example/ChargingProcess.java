package org.example;
import java.time.Duration;
import java.time.LocalDateTime;

public class ChargingProcess {
    private final String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int duration;
    private double energykwH;
    private Charger charger;
    private Price price;
    private double totalPrice;

    public ChargingProcess(int duration, double energykWh, Price price) {
        this.id = "session-" + System.currentTimeMillis(); // auto-generate ID
        this.duration = duration;
        this.energykwH = energykwH;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            this.duration = (int) Duration.between(startTime, endTime).toMinutes();
        }
    }


    public double getEnergykwH() {
        return energykwH;
    }

    public void setEnergykwH(double energykwH) {
        this.energykwH = energykwH;
    }

    public Charger getCharger() {
        return charger;
    }

    public void setCharger(Charger charger) {
        this.charger = charger;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
    public void calculateBilling() {
        if (price == null || charger == null) {
            throw new IllegalStateException("Price or Charger not set");
        }
        totalPrice = price.calculate(charger.getType(), energykwH, duration);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return id + ": " + startTime + endTime + "duration: " + duration + "energy loaded: " + energykwH + "charger type: " + (charger.getType())
                + "total price" + totalPrice;
    }
}
