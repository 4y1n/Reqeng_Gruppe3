package org.example;

public class Chargers {


    private final String id;
    private String type;
    private ChargerStatus status;
    private Location location;

    public Chargers(String id, String type, String status, Location location) {
        this.id = id;
        this.type = type;
        this.status = ChargerStatus.fromString(status);
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status == null ? null : status.toString();
    }

    public Location getLocation() {
        return location;
    }

    public void setStatus(String status) {
        this.status = ChargerStatus.fromString(status);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public boolean isAvailable() {
        return this.status == ChargerStatus.AVAILABLE;
    }

    public boolean isOccupied() {
        return this.status == ChargerStatus.OCCUPIED;
    }

    public boolean isOutOfOrder() {
        return this.status == ChargerStatus.OUT_OF_ORDER || this.status == ChargerStatus.UNAVAILABLE;
    }

    @Override
    public String toString() {
        return id + ": " + type + " - " + (status == null ? "<no-status>" : status.toString()) + " (Location: " + (location == null ? "<no-location>" : location.getName()) + ")";
    }
}
