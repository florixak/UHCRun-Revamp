package me.florixak.uhcrun.manager;

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

    public void setLobby(LobbyType lobbyType, Location loc){
        lobby_config.set("lobby.waiting.world", gameManager.getGameWorld().getName());
        lobby_config.set("lobby.waiting.x", loc.getX());
        lobby_config.set("lobby.waiting.y", loc.getY());
        lobby_config.set("lobby.waiting.z", loc.getZ());
        lobby_config.set("lobby.waiting.yaw", loc.getYaw());
        lobby_config.set("lobby.waiting.pitch", loc.getPitch());

        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
    public Location getLocation(LobbyType lobbyType){
        return new Location(
                Bukkit.getWorld(lobby_config.getString("lobby.waiting.world")),
                lobby_config.getDouble("lobby.waiting.x"),
                lobby_config.getDouble("lobby.waiting.y"),
                lobby_config.getDouble("lobby.waiting.z"),
                (float) lobby_config.getDouble("lobby.waiting.yaw"),
                (float) lobby_config.getDouble("lobby.waiting.pitch"));
    }
    public boolean existsLobby(LobbyType lobbyType){
        return lobby_config.get("lobby.waiting") != null;
    }
    public void deleteLobby(){
        lobby_config.set("lobby.waiting", null);
        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
}

