package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class LevelManager {

    private UHCRun plugin;
    private FileConfiguration config, statistics;

    public LevelManager(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.statistics = plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
    }

    public void addPlayerLevel(UHCPlayer player, double xp_level) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addRequiredXP(player.getUUID(),-xp_level);
        } else {
            statistics.set("statistics." + player.getUUID().toString() + ".requiredXP", getRequiredExp(player)-xp_level);
            plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        }
        if (getRequiredExp(player) <= 0) {
            levelUp(player);
        }
    }

    public int getPlayerLevel(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return plugin.data.getPlayerLevel(player.getUUID());
        }
        return statistics.getInt("statistics." + player.getUUID().toString() + ".level");
    }

    public double getRequiredExp(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return plugin.data.getRequiredXP(player.getUUID());
        }
        return statistics.getInt("statistics." + player.getUUID().toString() + ".requiredXP");
    }

    public double setRequiredExp(UHCPlayer player) {
        double totalExpRequiredForLevel = 0;
        if (getPlayerLevel(player) <= 100) {
            totalExpRequiredForLevel = 60000 * Math.pow(1.025, getPlayerLevel(player)) - 60000;
        }
        if (getPlayerLevel(player) > 100) {
            totalExpRequiredForLevel = (60000 * Math.pow(1.025, 100) * Math.pow(getPlayerLevel(player), 2.5)/Math.pow(100, 2.5));
        }
        return totalExpRequiredForLevel;
    }

    public int getPreviousLevel(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return plugin.data.getPlayerLevel(player.getUUID())-1;
        }
        return statistics.getInt("statistics." + player.getUUID().toString() + ".level")-1;
    }

    public void levelUp(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addPlayerLevel(player.getUUID());
            plugin.data.setRequiredXP(player.getUUID());
        }
        else {
            statistics.set("statistics." + player.getUUID().toString() + ".level", getPlayerLevel(player)+1);
            statistics.set("statistics." + player.getUUID().toString() + ".requiredXP", setRequiredExp(player));
            plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        }

        // SoundManager.playLevelUP(player);

        player.sendMessage(Messages.LEVEL_UP.toString()
                .replace("%newLevel%", String.valueOf(getPlayerLevel(player)))
                .replace("%previousLevel%", String.valueOf(getPreviousLevel(player))));
    }
}