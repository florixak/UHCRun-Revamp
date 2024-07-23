package me.florixak.uhcrun.game.worldGenerator;

import org.bukkit.Location;

public class LimitedRegion {

    private Location center;
    private int radius;

    public LimitedRegion(Location center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    public boolean isInRegion(Location location) {
        if (!location.getWorld().equals(center.getWorld())) {
            return false; // Different worlds
        }
        return center.distance(location) <= radius;
    }
}
