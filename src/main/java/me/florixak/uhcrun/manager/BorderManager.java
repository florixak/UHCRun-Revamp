package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;

public class BorderManager {

    private FileConfiguration config;
    private WorldBorder wb;

    public BorderManager(GameManager gameManager) {
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.wb = Bukkit.getWorld(gameManager.getGameWorld().getName()).getWorldBorder();
    }

    public void setBorder() {
        wb.setCenter(0, 0);
        wb.setSize(config.getDouble("settings.border.size"));
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
        return config.getDouble("settings.border.size");
    }

    public double getSpeed() {
        return config.getDouble("settings.border.speed");
    }

    public boolean exists() {
        return wb != null && wb.getSize() == getMaxSize();
    }

    public void checkBorder() {
        if (!exists()) setBorder();
    }
}