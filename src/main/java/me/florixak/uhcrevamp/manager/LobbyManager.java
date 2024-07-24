package me.florixak.uhcrevamp.manager;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LobbyManager {

    private final GameManager gameManager;
    private final FileConfiguration lobbyConfig;

    private String configPath;

    public LobbyManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.lobbyConfig = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.configPath = "settings.lobby.";
    }

    public void setLobby(String lobbyType, Location loc) {

        lobbyConfig.set(configPath + lobbyType.toLowerCase() + ".world", loc.getWorld().getName());
        lobbyConfig.set(configPath + lobbyType.toLowerCase() + ".x", loc.getX());
        lobbyConfig.set(configPath + lobbyType.toLowerCase() + ".y", loc.getY());
        lobbyConfig.set(configPath + lobbyType.toLowerCase() + ".z", loc.getZ());
        lobbyConfig.set(configPath + lobbyType.toLowerCase() + ".yaw", loc.getYaw());
        lobbyConfig.set(configPath + lobbyType.toLowerCase() + ".pitch", loc.getPitch());

        gameManager.getConfigManager().getFile(ConfigType.SETTINGS).save();
    }

    public Location getLocation(String lobbyType) {
        Location loc = new Location(Bukkit.getWorld("UHCLobby"), 0.0, Bukkit.getWorld("UHCLobby").getHighestBlockYAt(0, 0), 0.0);

        if (!existsLobby(lobbyType)
                || Bukkit.getWorld(getWorld("waiting")) == null
                || Bukkit.getWorld(getWorld("ending")) == null)
            return loc;

        loc = new Location(
                Bukkit.getWorld(lobbyConfig.getString(configPath + lobbyType.toLowerCase() + ".world", "lobby")),
                lobbyConfig.getDouble(configPath + lobbyType.toLowerCase() + ".x"),
                lobbyConfig.getDouble(configPath + lobbyType.toLowerCase() + ".y"),
                lobbyConfig.getDouble(configPath + lobbyType.toLowerCase() + ".z"),
                (float) lobbyConfig.getDouble(configPath + lobbyType.toLowerCase() + ".yaw"),
                (float) lobbyConfig.getDouble(configPath + lobbyType.toLowerCase() + ".pitch"));

        return loc;
    }

    public String getWorld(String lobbyType) {
        return lobbyConfig.getString(configPath + lobbyType.toLowerCase() + ".world");
    }

    public boolean existsLobby(String lobbyType) {
        return lobbyConfig.getConfigurationSection(configPath + lobbyType.toLowerCase()) != null;
    }

    public void deleteLobby(String lobbyType) {
        if (existsLobby(lobbyType)) return;
        lobbyConfig.set("lobby." + lobbyType.toLowerCase(), null);
        gameManager.getConfigManager().getFile(ConfigType.SETTINGS).save();
    }
}

