package me.florixak.uhcrun.game.deathmatch;

import org.bukkit.Location;

public class Deathmatch {

    private Location location;
    private double borderSize;

    public Deathmatch(Location location, double borderSize) {
        this.location = location;
        this.borderSize = borderSize;
    }

    public Location getLocation() {
        return location;
    }

    public double getBorderSize() {
        return borderSize;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setBorderSize(double borderSize) {
        this.borderSize = borderSize;
    }
}
