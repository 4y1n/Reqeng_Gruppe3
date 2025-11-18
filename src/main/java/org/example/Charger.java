package org.example;

public class Charger {

    private final String id;
    private String type;   // AC / DC
    private String status; // available / unavailable
    private Location location;

    public Charger(String id, String type, String status, Location location) {
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

    @Override
    public String toString() {
        return id + ": " + type + " - " + status + " (Location: " + location.getName() + ")";
    }
}
