package org.example;

import java.time.LocalDateTime;
import java.time.Duration;

public class ChargingProcess {

    private final String customerId;
    private final String chargerId;
    private final String mode;

    private final double energyKwh;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    private final long durationMinutes;



    public ChargingProcess(
            String customerId,
            String chargerId,
            String mode,
            double energyKwh,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        if (customerId == null || chargerId == null || mode == null) {
            throw new IllegalArgumentException("customerId, chargerId and mode must not be null");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("start and end times must not be null");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("Invalid charging session: end time must be after start time");
        }
        if (energyKwh < 0) {
            throw new IllegalArgumentException("Energy (kWh) must not be negative");
        }

        this.customerId = customerId;
        this.chargerId = chargerId;
        this.mode = mode;

        this.energyKwh = energyKwh;
        this.startTime = startTime;
        this.endTime = endTime;

        this.durationMinutes = Duration.between(startTime, endTime).toMinutes();
    }



    public String getCustomerId() {
        return customerId;
    }

    public String getChargerId() {
        return chargerId;
    }

    public String getMode() {
        return mode;
    }

    public double getEnergyKwh() {
        return energyKwh;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }



    @Override
    public String toString() {
        return "ChargingProcess[" +
                "customer=" + customerId +
                ", charger=" + chargerId +
                ", mode=" + mode +
                ", energy=" + energyKwh +
                ", start=" + startTime +
                ", end=" + endTime +
                ", minutes=" + durationMinutes +
                "]";
    }
}
