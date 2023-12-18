package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class DeathmatchManager {

    private final GameManager gameManager;
    private final FileConfiguration config;
    private final String path;

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
        gameManager.getConfigManager().getFile(ConfigType.SETTINGS).save();
    }

    public void resetDeathmatchLocation() {
        config.set(path + ".location.world", "world");
        config.set(path + ".location.x", 0.0);
        config.set(path + ".location.y", 75.0);
        config.set(path + ".location.z", 0.0);
        gameManager.getConfigManager().getFile(ConfigType.SETTINGS).save();
    }

    public double getDeathmatchBorderSize() {
        return config.getDouble(path + ".border-size", 40.0);
    }

    public boolean isDeathmatchEnabled() {
        return config.getBoolean(path + ".enabled", true);
    }

    public Location getTeleportLocation() {
        Location loc = getDeathmatchLocation();

        return new Location(loc.getWorld(),
                (loc.getX() + (int)(Math.random() * ((getDeathmatchBorderSize()-1)-loc.getX()+5)))/2.2,
                loc.getWorld().getHighestBlockYAt(loc),
                (loc.getZ() + (int)(Math.random() * ((getDeathmatchBorderSize()-1)-loc.getZ()+5)))/2.2
        );
    }

    public void prepareDeathmatch() {
        gameManager.getBorderManager().setSize(getDeathmatchBorderSize());
        gameManager.getTeamManager().getTeams().forEach(uhcTeam -> uhcTeam.teleport(getTeleportLocation()));
        gameManager.getPlayerManager().getSpectatorList().stream()
                .filter(UHCPlayer::isOnline)
                .forEach(uhcPlayer -> uhcPlayer.teleport(getTeleportLocation()));

        if (getPVPResistCD() > 0) {
            gameManager.setPvP(false);
            gameManager.getTaskManager().startDeathmatchResist();
        }
    }

    public int getPVPResistCD() {
        return config.getInt(path + ".pvp-resistance-countdown");
    }
}