package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Random;

public class DeathmatchManager {

    private GameManager gameManager;
    private FileConfiguration config;
    private String path;

    public DeathmatchManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

        this.path = "settings.deathmatch";
    }

    public Location getDeathmatchLocation() {

        return new Location(
                Bukkit.getWorld(config.getString(path + ".location.world", "world")),
                config.getDouble(path + ".location.x", 0.0),
                config.getDouble(path + ".location.y", 75.0),
                config.getDouble(path + ".location.z", 0.0));
    }

    public void setDeathmatchLocation(Location location) {

        config.set(path + ".location.world", location.getWorld().getName());
        config.set(path + ".location.x", location.getX());
        config.set(path + ".location.y", location.getY());
        config.set(path + ".location.z", location.getZ());
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.SETTINGS).save();
    }

    public void resetDeathmatchLocation() {
        config.set(path + ".location.world", "world");
        config.set(path + ".location.x", 0.0);
        config.set(path + ".location.y", 75.0);
        config.set(path + ".location.z", 0.0);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.SETTINGS).save();
    }

    public double getDeathmatchBorderSize() {
        return config.getDouble(path + ".border-size", 25.0);
    }

    public boolean isDeathmatchEnabled() {
        return config.getBoolean(path + ".enabled", true);
    }

    public Location getTeleportLocation() {
        Random ran = new Random();
        Location loc = getDeathmatchLocation();

        return new Location(loc.getWorld(),
                (loc.getX()+ran.nextInt((int)(getDeathmatchBorderSize()))-((int)getDeathmatchBorderSize()-2))-2,
                loc.getWorld().getHighestBlockYAt(loc),
                (loc.getX()+ran.nextInt((int)(getDeathmatchBorderSize()))-((int)getDeathmatchBorderSize()-2))-2
        );
    }

    public int getPVPResistCD() {
        return config.getInt(path + ".pvp-resistance");
    }
}