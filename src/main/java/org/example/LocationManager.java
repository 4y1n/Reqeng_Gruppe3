package org.example;

import java.util.ArrayList;
import java.util.List;

public class LocationManager {

    private static final LocationManager INSTANCE = new LocationManager();

    private final List<Location> locations = new ArrayList<>();

    private LocationManager() {}
    public static LocationManager getInstance() {
        return INSTANCE;
    }

    public LocationManager clearLocations() {
        locations.clear();
        return this;
    }

    public Location createLocation(String name) {
        if (viewLocation(name) != null)
            throw new IllegalArgumentException("Location \"" + name + "\" already exists.");

        Location loc = new Location(name);
        locations.add(loc);
        return loc;
    }

    public Location viewLocation(String name) {
        return locations.stream()
                .filter(l -> l.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Update the name of an existing location.
     * Throws IllegalArgumentException if the source doesn't exist or the target name is already used.
     */
    public Location updateLocation(String oldName, String newName) {
        if (oldName == null || newName == null)
            throw new IllegalArgumentException("Location names must not be null.");
        if (oldName.equals(newName))
            return viewLocation(oldName);
        Location existing = viewLocation(oldName);
        if (existing == null)
            throw new IllegalArgumentException("Location \"" + oldName + "\" does not exist.");
        if (viewLocation(newName) != null)
            throw new IllegalArgumentException("Location \"" + newName + "\" already exists.");
        existing.setName(newName);
        return existing;
    }



    public void deleteLocation(String name) {
        locations.remove(viewLocation(name));
    }

    public int getNumberOfLocations() {
        return locations.size();
    }

    /**
     * Return the internal list of locations so callers can iterate locations.
     * NOTE: this returns the live list (no defensive copy) for simplicity.
     */
    public List<Location> getAllLocations() {
        return locations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Locations:\n");
        for (Location loc : locations) {
            sb.append("  ").append(loc.toString()).append("\n");
        }
        return sb.toString().trim();
    }

}
