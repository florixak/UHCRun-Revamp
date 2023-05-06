package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;

public class BorderManager {

    private FileConfiguration config;

    private World world;
    private WorldBorder wb;

    private double size;
    private double speed;

    public BorderManager(GameManager gameManager) {
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.world = Bukkit.getWorld(config.getString("settings.game.game-world"));

        this.wb = world.getWorldBorder();
        this.size = config.getDouble("settings.border.size");
        this.speed = config.getDouble("settings.border.speed");
    }

    public void createBorder() {
        wb.setCenter(0, 0);
        wb.setSize(size);
    }
    public void resetBorder() {
        wb.setCenter(0, 0);
        wb.setSize(size);
    }
    public void removeBorder() {
        wb.setSize(0);
    }

    public void setSize(double size) {
        this.size = size;
        wb.setSize(size);
    }
    public double getSize() {
        return wb.getSize();
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public double getSpeed() {
        return speed;
    }

    public boolean exists() {
        return wb.getSize() == config.getInt("settings.border.size");
    }

    public void checkBorder() {
        if (!exists()) {
            createBorder();
        }
    }
}
