// java
package org.example;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChargersManager {

    private static final ChargersManager INSTANCE = new ChargersManager();
    private final List<Chargers> chargers = new ArrayList<>();

    private ChargersManager() {}

    public static ChargersManager getInstance() {
        return INSTANCE;
    }

    public ChargersManager clearChargers() {
        chargers.clear();
        return this;
    }


    public Chargers createCharger(String id, String type, String status, Location location) {
        if (viewCharger(id) != null) {
            throw new IllegalArgumentException("Charger already exists: " + id);
        }
        Chargers c = new Chargers(id, type, status, location);
        chargers.add(c);
        return c;
    }

    public Chargers createCharger(String id, Location location) {
        return createCharger(id, "unknown", "available", location);
    }

    public Chargers viewCharger(String id) {
        return chargers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Chargers updateCharger(String id, String type, String status, Location location) {
        Chargers c = viewCharger(id);
        if (c == null) {
            throw new RuntimeException("Charger not found: " + id);
        }
        if (type != null) c.setType(type);
        if (status != null) c.setStatus(status);
        if (location != null) c.setLocation(location);
        return c;
    }

    public void deleteCharger(String id) {
        chargers.removeIf(ch -> ch.getId().equals(id));
    }

    public List<Chargers> getAllChargers() {
        return chargers;
    }

    public List<Chargers> getChargersByLocation(Location location) {
        return chargers.stream()
                .filter(c -> c.getLocation().equals(location))
                .collect(Collectors.toList());
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Chargers:\n");
        for (Chargers c : chargers) {
            sb.append("  ").append(c.toString()).append("\n");
        }
        return sb.toString().trim();
    }

}