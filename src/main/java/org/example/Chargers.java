package org.example;

public class Chargers {


    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_OCCUPIED = "occupied";
    public static final String STATUS_OUT_OF_ORDER = "out of order";
    public static final String STATUS_UNAVAILABLE = "unavailable";

    private final String id;
    private String type;
    private String status;
    private Location location;

    public Chargers(String id, String type, String status, Location location) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public Location getLocation() {
        return location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public boolean isAvailable() {
        return STATUS_AVAILABLE.equalsIgnoreCase(this.status);
    }

    public boolean isOccupied() {
        return STATUS_OCCUPIED.equalsIgnoreCase(this.status);
    }

    public boolean isOutOfOrder() {
        return STATUS_OUT_OF_ORDER.equalsIgnoreCase(this.status)
                || STATUS_UNAVAILABLE.equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return id + ": " + type + " - " + status + " (Location: " + location.getName() + ")";
    }
}
