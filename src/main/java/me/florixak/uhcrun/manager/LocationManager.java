package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LocationManager {

    private UHCRun plugin;
    private FileConfiguration lobby;

    public LocationManager(UHCRun plugin){
        this.plugin = plugin;
        this.lobby = plugin.getConfigManager().getFile(ConfigType.LOBBY).getConfig();
    }

    /** WAITING LOBBY **/
    public void setWaitingLobby(Location loc){
        lobby.set("waiting.world", loc.getWorld().getName());
        lobby.set("waiting.x", loc.getX());
        lobby.set("waiting.y", loc.getY());
        lobby.set("waiting.z", loc.getZ());
        lobby.set("waiting.yaw", loc.getYaw());
        lobby.set("waiting.pitch", loc.getPitch());

        plugin.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
    public Location getWaitingLobbyLocation(){
        return new Location(
                Bukkit.getWorld(lobby.getString("waiting.world")),
                lobby.getDouble("waiting.x"),
                lobby.getDouble("waiting.y"),
                lobby.getDouble("waiting.z"),
                (float) lobby.getDouble("waiting.yaw"),
                (float) lobby.getDouble("waiting.pitch"));
    }
    public boolean existsWaitingLobby(){
        return lobby.get("waiting") != null;
    }
    public void deleteWaitingLobby(){
        lobby.set("waiting", null);
        plugin.getConfigManager().getFile(ConfigType.LOBBY).save();
    }

    /** ENDING LOBBY **/
    public void setEndingLobby(Location loc){
        lobby.set("ending.world", loc.getWorld().getName());
        lobby.set("ending.x", loc.getX());
        lobby.set("ending.y", loc.getY());
        lobby.set("ending.z", loc.getZ());
        lobby.set("ending.yaw", loc.getYaw());
        lobby.set("ending.pitch", loc.getPitch());

        plugin.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
    public Location getEndingLobbyLocation(){
        return new Location(
                Bukkit.getWorld(lobby.getString("ending.world")),
                lobby.getDouble("ending.x"),
                lobby.getDouble("ending.y"),
                lobby.getDouble("ending.z"),
                (float) lobby.getDouble("ending.yaw"),
                (float) lobby.getDouble("ending.pitch"));
    }
    public boolean existsEndingLobby(){
        return lobby.get("ending") != null;
    }
    public void deleteEndingLobby(){
        lobby.set("lobby.ending", null);
        plugin.getConfigManager().getFile(ConfigType.LOBBY).save();
    }
}

