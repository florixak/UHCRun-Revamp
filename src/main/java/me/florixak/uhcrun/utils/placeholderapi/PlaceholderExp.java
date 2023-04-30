package me.florixak.uhcrun.utils.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.player.UHCPlayer;
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

        UHCPlayer uhcPlayer = plugin.getGameManager().getPlayerManager().getUHCPlayer(p.getUniqueId());

        if (params.equalsIgnoreCase("uhc-author")) {
            return getAuthor();
        }

        if (params.equalsIgnoreCase("winner")) {
            if (plugin.getGameManager().getPlayerManager().getUHCWinner() != null) return "NONE";
            if (plugin.getGameManager().isTeamMode()) {
                return plugin.getGameManager().getTeamManager().getGameWinner().getName();
            }
            return plugin.getGameManager().getPlayerManager().getUHCWinner().getName();
        }

        if (params.equalsIgnoreCase("team")) {
            if (!uhcPlayer.hasTeam()) return "";
            return plugin.getGameManager().getPlayerManager().getUHCPlayer(p.getUniqueId()).getTeam() + " | ";
        }

        if (params.equalsIgnoreCase("kills")) {
            return String.valueOf(uhcPlayer.getKills());
        }

        if (params.equalsIgnoreCase("kills")) {
            return String.valueOf(uhcPlayer.getKills());
        }

        if (params.equalsIgnoreCase("uhc-level")) {
            return String.valueOf(uhcPlayer.getData().getLevel());
        }

        if (params.equalsIgnoreCase("uhc-kills")) {
            return String.valueOf(uhcPlayer.getData().getKills());
        }

        if (params.equalsIgnoreCase("uhc-wins")) {
            return String.valueOf(uhcPlayer.getData().getWins());
        }

        if (params.equalsIgnoreCase("uhc-deaths")) {
            return String.valueOf(uhcPlayer.getData().getDeaths());
        }

        if (params.equalsIgnoreCase("uhc-money")) {
            return String.valueOf(uhcPlayer.getData().getMoney());
        }
        return null;
    }
}
