package org.example;
import java.time.LocalDateTime;

public class ChargingProcess {
    private final String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    int duration;
    double energykwH;

    public ChargingProcess(String id) {
        this.id = id;
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
    }


    public double getEnergykwH() {
        return energykwH;
    }

    public void setEnergykwH(double energykwH) {
        this.energykwH = energykwH;
    }

    @Override
    public String toString() {
        return id + ": " + startTime + endTime + "duration: " + duration + "energy loaded: " + energykwH;
    }
}
