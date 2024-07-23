package me.florixak.uhcrevamp.manager.lobby;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LobbyManager {

    private final GameManager gameManager;
    private final FileConfiguration lobbyConfig;

    public LobbyManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.lobbyConfig = gameManager.getConfigManager().getFile(ConfigType.LOBBY).getConfig();
    }

    public void setLobby(LobbyType lobbyType, Location loc) {

        lobbyConfig.set("lobby." + lobbyType.name().toLowerCase() + ".world", loc.getWorld().getName());
        lobbyConfig.set("lobby." + lobbyType.name().toLowerCase() + ".x", loc.getX());
        lobbyConfig.set("lobby." + lobbyType.name().toLowerCase() + ".y", loc.getY());
        lobbyConfig.set("lobby." + lobbyType.name().toLowerCase() + ".z", loc.getZ());
        lobbyConfig.set("lobby." + lobbyType.name().toLowerCase() + ".yaw", loc.getYaw());
        lobbyConfig.set("lobby." + lobbyType.name().toLowerCase() + ".pitch", loc.getPitch());

        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }

    public Location getLocation(LobbyType lobbyType) {
        Location loc = new Location(Bukkit.getWorld("UHCLobby"), 0.0, Bukkit.getWorld("UHCLobby").getHighestBlockYAt(0, 0), 0.0);

        if (!existsLobby(lobbyType)
                || Bukkit.getWorld(getWorld(LobbyType.WAITING)) == null
                || Bukkit.getWorld(getWorld(LobbyType.ENDING)) == null)
            return loc;

        loc = new Location(
                Bukkit.getWorld(lobbyConfig.getString("lobby." + lobbyType.name().toLowerCase() + ".world", "lobby")),
                lobbyConfig.getDouble("lobby." + lobbyType.name().toLowerCase() + ".x"),
                lobbyConfig.getDouble("lobby." + lobbyType.name().toLowerCase() + ".y"),
                lobbyConfig.getDouble("lobby." + lobbyType.name().toLowerCase() + ".z"),
                (float) lobbyConfig.getDouble("lobby." + lobbyType.name().toLowerCase() + ".yaw"),
                (float) lobbyConfig.getDouble("lobby." + lobbyType.name().toLowerCase() + ".pitch"));

        return loc;
    }

    public String getWorld(LobbyType lobbyType) {
        return lobbyConfig.getString("lobby." + lobbyType.name().toLowerCase() + ".world");
    }

    public boolean existsLobby(LobbyType lobbyType) {
        return lobbyConfig.getConfigurationSection("lobby." + lobbyType.name().toLowerCase()) != null;
    }

    public void deleteLobby(LobbyType lobbyType) {
        if (existsLobby(lobbyType)) return;
        lobbyConfig.set("lobby." + lobbyType.name().toLowerCase(), null);
        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
}

