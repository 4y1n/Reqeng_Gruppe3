package org.example;

import java.util.ArrayList;
import java.util.List;

public class ChargerManager {

    private static ChargerManager instance;

    private List<Charger> chargers = new ArrayList<>();

    private ChargerManager() { }

    public static ChargerManager getInstance() {
        if (instance == null) {
            instance = new ChargerManager();
        }
        return instance;
    }

    public ChargerManager clearChargers() {
        chargers.clear();
        return this;
    }

    public Charger createCharger(String id, String type, String status, Location location) {
        Charger c = new Charger(id, type, status, location);
        chargers.add(c);
        return c;
    }

    public Charger createCharger(String id, Location location) {
        return createCharger(id, "unknown", "available", location);
    }

    public Charger viewCharger(String id) {
        return chargers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void deleteCharger(String id) {

        Charger c = viewCharger(id);
        if (c == null) return;

        Location loc = c.getLocation();


        chargers.removeIf(ch -> ch.getId().equals(id));
    }

    public List<Charger> getAllChargers() {
        return chargers;
    }

    public List<Charger> getChargersByLocation(Location location) {
        return chargers.stream()
                .filter(c -> c.getLocation().equals(location))
                .toList();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Chargers:\n");
        for (Charger c : chargers) {
            sb.append("  ").append(c.toString()).append("\n");
        }
        return sb.toString().trim();
    }
}
