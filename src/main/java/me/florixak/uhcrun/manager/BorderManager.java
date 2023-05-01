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
    private double damage;
    private double speed;

    public BorderManager(GameManager gameManager) {
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

        this.world = Bukkit.getWorld(config.getString("settings.game.game-world"));
        this.wb = world.getWorldBorder();

        this.size = config.getDouble("settings.border.size");
        this.damage = config.getDouble("settings.border.damage");
        this.speed = config.getDouble("settings.border.speed");
    }

    public void createBorder() {

        if (wb == null) return;

        wb.setCenter(0, 0);
        wb.setSize(size);
        wb.setDamageAmount(damage);
    }

    public void setSize(double size) {
        this.size = size;
        wb.setSize(size);
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void resetBorder() {
        wb.reset();
    }
    public void removeBorder() {
        wb.setSize(0);
    }

    public boolean exist() {
        return wb.getSize() == config.getInt("settings.border.size");
    }

    public double getSize() {
        return wb.getSize();
    }
    public double getDamage() {
        return wb.getDamageAmount();
    }
    public int getWarningDistance() { return wb.getWarningDistance(); }
    public double getSpeed() {
        return speed;
    }

    public void checkBorder() {
        if (!exist()) {
            removeBorder();
            createBorder();
        }
    }
}
