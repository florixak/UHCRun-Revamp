package me.florixak.uhcrun.utility.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.florixak.uhcrun.UHCRun;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderExp extends PlaceholderExpansion {

    private UHCRun plugin;

    public PlaceholderExp(UHCRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "uhcrun";
    }

    @Override
    public String getAuthor() {
        return "FloriXak";
    }

    @Override
    public String getVersion() {
        return "alpha";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player != null && player.isOnline()){
            return onPlaceholderRequest(player.getPlayer(), params);
        }

        return null; // Placeholder is unknown by the Expansion
    }

    @Override
    public String onPlaceholderRequest(Player p, String params){
        if (p == null){
            return null;
        }

        if (params.equalsIgnoreCase("team")) {
            if (plugin.getGame().isWaiting() || plugin.getGame().isStarting()) return PlaceholderAPI.setPlaceholders(p, "%luckperms_prefix%");

            return plugin.getTeamManager().getTeam(p) + " | ";
        }
        return null;
    }
}
