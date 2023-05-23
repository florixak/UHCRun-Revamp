package me.florixak.uhcrun.game.deathmatch;

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

    private Deathmatch deathmatch;

    public DeathmatchManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

        this.path = "settings.deathmatch";
    }

    public void loadDeathmatch() {
        if (!isDeathmatchEnabled()) return;

        Location loc = new Location(
                Bukkit.getWorld(config.getString(path + ".location.world", "world")),
                config.getDouble(path + ".location.x", 0.0),
                config.getDouble(path + ".location.y", 75.0),
                config.getDouble(path + ".location.z", 0.0)
        );

        this.deathmatch = new Deathmatch(loc, getDeathmatchBorderSize());
    }

    public Deathmatch getDeathmatch() {
        return deathmatch;
    }

    public void setDeathmatchLocation(Location location) {

        config.set(path + ".location.world", location.getWorld().getName());
        config.set(path + ".location.x", location.getX());
        config.set(path + ".location.y", location.getY());
        config.set(path + ".location.z", location.getZ());
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.SETTINGS).save();

        if (deathmatch == null) {
            this.deathmatch = new Deathmatch(location, getDeathmatchBorderSize());
        } else {
            this.deathmatch.setLocation(location);
        }
    }

    public double getDeathmatchBorderSize() {
        return config.getDouble(path + ".border-size", 50.0);
    }

    public boolean isDeathmatchEnabled() {
        return config.getBoolean(path + ".enabled", true);
    }

    public Location getTeleportLocation() {
        Random ran = new Random();
        Location loc = getDeathmatch().getLocation();

        return new Location(loc.getWorld(),
                loc.getX()+ran.nextInt((int)(getDeathmatch().getBorderSize()*2))-((int)getDeathmatch().getBorderSize()-1),
                loc.getWorld().getHighestBlockYAt(loc),
                loc.getX()+ran.nextInt((int)(getDeathmatch().getBorderSize()*2))-((int)getDeathmatch().getBorderSize()-1)
        );
    }

    public int getPVPResistCD() {
        return config.getInt(path + ".pvp-resistance");
    }
}