package me.florixak.uhcrun.manager.lobby;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LobbyManager {

    private GameManager gameManager;
    private FileConfiguration lobby_config;

    public LobbyManager(GameManager gameManager){
        this.gameManager = gameManager;
        this.lobby_config = gameManager.getConfigManager().getFile(ConfigType.LOBBY).getConfig();
    }

    public void setLobby(LobbyType lobbyType, Location loc) {

        lobby_config.set("lobby." + lobbyType.name().toLowerCase() + ".world", gameManager.getGameWorld().getName());
        lobby_config.set("lobby." + lobbyType.name().toLowerCase() + ".x", loc.getX());
        lobby_config.set("lobby." + lobbyType.name().toLowerCase() + ".y", loc.getY());
        lobby_config.set("lobby." + lobbyType.name().toLowerCase() + ".z", loc.getZ());
        lobby_config.set("lobby." + lobbyType.name().toLowerCase() + ".yaw", loc.getYaw());
        lobby_config.set("lobby." + lobbyType.name().toLowerCase() + ".pitch", loc.getPitch());

        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }

    public Location getLocation(LobbyType lobbyType) {
        Location loc = new Location(Bukkit.getWorld("world"), 0, 70, 0);

        if (!existsLobby(lobbyType)) return loc;

        loc = new Location(
                Bukkit.getWorld(lobby_config.getString("lobby."+ lobbyType.name().toLowerCase() + ".world")),
                lobby_config.getDouble("lobby." + lobbyType.name().toLowerCase() + ".x"),
                lobby_config.getDouble("lobby." + lobbyType.name().toLowerCase() + ".y"),
                lobby_config.getDouble("lobby." + lobbyType.name().toLowerCase() + ".z"),
                (float) lobby_config.getDouble("lobby." + lobbyType.name().toLowerCase() + ".yaw"),
                (float) lobby_config.getDouble("lobby." + lobbyType.name().toLowerCase() + ".pitch"));

        return loc;
    }

    public boolean existsLobby(LobbyType lobbyType) {
        return lobby_config.getConfigurationSection("lobby." + lobbyType.name().toLowerCase()) != null;
    }

    public void deleteLobby(LobbyType lobbyType) {
        if (existsLobby(lobbyType)) return;
        lobby_config.set("lobby." + lobbyType.name().toLowerCase(), null);
        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
}

