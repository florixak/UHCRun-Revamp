package me.florixak.uhcrevamp.utils.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderExp extends PlaceholderExpansion {

    private UHCRevamp plugin;

    public PlaceholderExp(UHCRevamp plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "uhcrevamp";
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
        if (player != null && player.isOnline()) {
            return onPlaceholderRequest(player.getPlayer(), params);
        }
        return null; // Placeholder is unknown by the Expansion
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        GameManager gameManager = plugin.getGameManager();
        if (p == null || gameManager.getConfigManager().getFile(ConfigType.SETTINGS)
                .getConfig().getBoolean("settings.addons.use-PlaceholderAPI", false)) {
            return null;
        }

        UHCPlayer uhcPlayer = plugin.getGameManager().getPlayerManager().getUHCPlayer(p.getUniqueId());

        if (params.equalsIgnoreCase("author")) {
            return getAuthor();
        }

        if (params.equalsIgnoreCase("luckperms-prefix")) {
            return uhcPlayer.getLuckPermsPrefix();
        }

        if (params.equalsIgnoreCase("winner")) {
            if (plugin.getGameManager().getPlayerManager().getUHCWinner() != null) return "None";
            return plugin.getGameManager().getPlayerManager().getUHCWinner();
        }

        if (params.equalsIgnoreCase("team")) {
            if (!GameValues.TEAM.TEAM_MODE) return "";
            if (!uhcPlayer.hasTeam()) return "";
            return uhcPlayer.getTeam().getName();
        }

        if (params.equalsIgnoreCase("kills")) {
            return String.valueOf(uhcPlayer.getKills());
        }

        if (params.equalsIgnoreCase("level")) {
            return String.valueOf(uhcPlayer.getData().getUHCLevel());
        }

        if (params.equalsIgnoreCase("total-kills")) {
            return String.valueOf(uhcPlayer.getData().getKills());
        }

        if (params.equalsIgnoreCase("deaths")) {
            return String.valueOf(uhcPlayer.getData().getDeaths());
        }

        if (params.equalsIgnoreCase("wins")) {
            return String.valueOf(uhcPlayer.getData().getWins());
        }

        if (params.equalsIgnoreCase("losses")) {
            return String.valueOf(uhcPlayer.getData().getDeaths());
        }

        if (params.equalsIgnoreCase("games-played")) {
            return String.valueOf(uhcPlayer.getData().getGamesPlayed());
        }

        if (params.equalsIgnoreCase("money")) {
            return String.valueOf(uhcPlayer.getData().getMoney());
        }
        return null;
    }
}
