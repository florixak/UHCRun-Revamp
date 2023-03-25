package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
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

    public void addPlayerLevel(UUID uuid, double xp_level) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addRequiredXP(uuid,-xp_level);
        } else {
            statistics.set("statistics." + uuid.toString() + ".requiredXP", getRequiredExp(uuid)-xp_level);
            plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        }
        if (getRequiredExp(uuid) <= 0) {
            levelUp(uuid);
        }
    }

    public int getPlayerLevel(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return plugin.data.getPlayerLevel(uuid);
        }
        return statistics.getInt("statistics." + uuid.toString() + ".level");
    }

    public double getRequiredExp(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return plugin.data.getRequiredXP(uuid);
        }
        return statistics.getInt("statistics." + uuid.toString() + ".requiredXP");
    }

    public double setRequiredExp(UUID uuid) {
        double totalExpRequiredForLevel = 0;
        if (getPlayerLevel(uuid) <= 100) {
            totalExpRequiredForLevel = 60000 * Math.pow(1.025, getPlayerLevel(uuid)) - 60000;
        }
        if (getPlayerLevel(uuid) > 100) {
            totalExpRequiredForLevel = (60000 * Math.pow(1.025, 100) * Math.pow(getPlayerLevel(uuid), 2.5)/Math.pow(100, 2.5));
        }
        return totalExpRequiredForLevel;
    }

    public int getPreviousLevel(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return plugin.data.getPlayerLevel(uuid)-1;
        }
        return statistics.getInt("statistics." + uuid.toString() + ".level")-1;
    }

    public void levelUp(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addPlayerLevel(uuid);
            plugin.data.setRequiredXP(uuid);
        }
        else {
            statistics.set("statistics." + uuid.toString() + ".level", getPlayerLevel(uuid)+1);
            statistics.set("statistics." + uuid.toString() + ".requiredXP", getRequiredExp(uuid));
            plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        }

        SoundManager.playLevelUP(Bukkit.getPlayer(uuid));

        Bukkit.getPlayer(uuid).sendMessage(Messages.LEVEL_UP.toString()
                .replace("%newLevel%", String.valueOf(getPlayerLevel(uuid)))
                .replace("%previousLevel%", String.valueOf(getPreviousLevel(uuid))));
    }
}