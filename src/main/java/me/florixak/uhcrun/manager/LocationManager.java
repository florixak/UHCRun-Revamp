package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LocationManager {

    private GameManager gameManager;
    private FileConfiguration lobby_config;

    public LocationManager(GameManager gameManager){
        this.gameManager = gameManager;
        this.lobby_config = gameManager.getConfigManager().getFile(ConfigType.LOBBY).getConfig();
    }

    /** WAITING LOBBY **/
    public void setWaitingLobby(Location loc){
        lobby_config.set("waiting.world", loc.getWorld().getName());
        lobby_config.set("waiting.x", loc.getX());
        lobby_config.set("waiting.y", loc.getY());
        lobby_config.set("waiting.z", loc.getZ());
        lobby_config.set("waiting.yaw", loc.getYaw());
        lobby_config.set("waiting.pitch", loc.getPitch());

        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
    public Location getWaitingLobbyLocation(){
        return new Location(
                Bukkit.getWorld(lobby_config.getString("waiting.world")),
                lobby_config.getDouble("waiting.x"),
                lobby_config.getDouble("waiting.y"),
                lobby_config.getDouble("waiting.z"),
                (float) lobby_config.getDouble("waiting.yaw"),
                (float) lobby_config.getDouble("waiting.pitch"));
    }
    public boolean existsWaitingLobby(){
        return lobby_config.get("waiting") != null;
    }
    public void deleteWaitingLobby(){
        lobby_config.set("waiting", null);
        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }

    /** ENDING LOBBY **/
    public void setEndingLobby(Location loc){
        lobby_config.set("ending.world", loc.getWorld().getName());
        lobby_config.set("ending.x", loc.getX());
        lobby_config.set("ending.y", loc.getY());
        lobby_config.set("ending.z", loc.getZ());
        lobby_config.set("ending.yaw", loc.getYaw());
        lobby_config.set("ending.pitch", loc.getPitch());

        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
    public Location getEndingLobbyLocation(){
        return new Location(
                Bukkit.getWorld(lobby_config.getString("ending.world")),
                lobby_config.getDouble("ending.x"),
                lobby_config.getDouble("ending.y"),
                lobby_config.getDouble("ending.z"),
                (float) lobby_config.getDouble("ending.yaw"),
                (float) lobby_config.getDouble("ending.pitch"));
    }
    public boolean existsEndingLobby(){
        return lobby_config.get("ending") != null;
    }
    public void deleteEndingLobby(){
        lobby_config.set("lobby.ending", null);
        gameManager.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
}

