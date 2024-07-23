package me.florixak.uhcrevamp.manager;

import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;

public class BorderManager {

    private final WorldBorder wb;

    public BorderManager() {
        this.wb = Bukkit.getWorld("world").getWorldBorder();
    }

    public void setBorder() {
        wb.setCenter(0, 0);
        wb.setSize(GameValues.BORDER.INIT_BORDER_SIZE);
    }

    public void removeBorder() {
        wb.setSize(0);
    }

    public void setSize(double size) {
        wb.setSize(size);
    }

    public double getSize() {
        return wb.getSize();
    }

    public double getMaxSize() {
        return GameValues.BORDER.INIT_BORDER_SIZE;
    }

    public double getSpeed() {
        return GameValues.BORDER.BORDER_SPEED;
    }

    public boolean exists() {
        return wb != null && wb.getSize() == getMaxSize();
    }

    public boolean isInBorder(Location loc) {
        if (loc.getX() > getSize() && loc.getZ() > getSize()) return true;
        return false;
    }

    public void checkBorder() {
        if (!exists()) setBorder();
    }

    public void shrinkBorder() {
        setSize(getSize() - getSpeed());
    }
}