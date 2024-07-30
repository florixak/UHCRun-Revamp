package me.florixak.uhcrevamp.manager;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class LobbyManager {

    private final GameManager gameManager;
    private final FileConfiguration config;
    private final String waitingLobbyName;
    private final String endingLobbyName;

    public LobbyManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        waitingLobbyName = config.getString("settings.lobby.waiting.world", "UHCWaitingLobby");
        endingLobbyName = config.getString("settings.lobby.ending.world", "UHCEndingLobby");
    }

    public void checkLobbies() {
        if (!worldExists(waitingLobbyName) || !worldExists(endingLobbyName)) {
            Bukkit.getLogger().info("Waiting: " + waitingLobbyName + " Ending:" + endingLobbyName);
            Bukkit.getLogger().info("One or more lobbies do not exist, creating them...");
            gameManager.getWorldManager().createLobbyWorld(waitingLobbyName);
            if (waitingLobbyName.equals(endingLobbyName)) return;
            gameManager.getWorldManager().createLobbyWorld(endingLobbyName);
        }
    }

    public void setWaitingLobbyLocation(Location location) {
        config.set("settings.lobby.waiting.world", location.getWorld().getName());
        config.set("settings.lobby.waiting.x", location.getX());
        config.set("settings.lobby.waiting.y", location.getY());
        config.set("settings.lobby.waiting.z", location.getZ());
        config.set("settings.lobby.waiting.yaw", location.getYaw());
        config.set("settings.lobby.waiting.pitch", location.getPitch());
        gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
    }

    public void removeWaitingLobby() {
        config.set("settings.lobby.waiting.world", "UHCWaitingLobby");
        config.set("settings.lobby.waiting.x", 0);
        config.set("settings.lobby.waiting.y", 10);
        config.set("settings.lobby.waiting.z", 0);
        config.set("settings.lobby.waiting.yaw", 0);
        config.set("settings.lobby.waiting.pitch", 0);
        gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
    }

    public Location getWaitingLobbyLocation() {
        return new Location(
                Bukkit.getWorld(waitingLobbyName),
                config.getDouble("settings.lobby.waiting.x"),
                config.getDouble("settings.lobby.waiting.y"),
                config.getDouble("settings.lobby.waiting.z"),
                (float) config.getDouble("settings.lobby.waiting.yaw"),
                (float) config.getDouble("settings.lobby.waiting.pitch")
        );
    }

    public void setEndingLobbyLocation(Location location) {
        config.set("settings.lobby.ending.world", location.getWorld().getName());
        config.set("settings.lobby.ending.x", location.getX());
        config.set("settings.lobby.ending.y", location.getY());
        config.set("settings.lobby.ending.z", location.getZ());
        config.set("settings.lobby.ending.yaw", location.getYaw());
        config.set("settings.lobby.ending.pitch", location.getPitch());
        gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
    }

    public void removeEndingLobby() {
        config.set("settings.lobby.ending.world", "UHCEndingLobby");
        config.set("settings.lobby.ending.x", 0);
        config.set("settings.lobby.ending.y", 10);
        config.set("settings.lobby.ending.z", 0);
        config.set("settings.lobby.ending.yaw", 0);
        config.set("settings.lobby.ending.pitch", 0);
        gameManager.getConfigManager().saveFile(ConfigType.SETTINGS);
    }

    public Location getEndingLobbyLocation() {
        return new Location(
                Bukkit.getWorld(waitingLobbyName),
                config.getDouble("settings.lobby.ending.x"),
                config.getDouble("settings.lobby.ending.y"),
                config.getDouble("settings.lobby.ending.z"),
                (float) config.getDouble("settings.lobby.ending.yaw"),
                (float) config.getDouble("settings.lobby.ending.pitch")
        );
    }

    private boolean worldExists(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return world != null;
    }
}