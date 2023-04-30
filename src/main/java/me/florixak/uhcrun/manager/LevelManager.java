package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.configuration.file.FileConfiguration;

public class LevelManager {

    private GameManager gameManager;
    private FileConfiguration config, statistics;

    public LevelManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.statistics = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
    }

    public void addPlayerLevel(UHCPlayer player, double xp_level) {
        statistics.set("statistics." + player.getUUID().toString() + ".requiredXP", getRequiredExp(player)-xp_level);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (getRequiredExp(player) <= 0) {
            levelUp(player);
        }
    }

    public int getPlayerLevel(UHCPlayer player) {
        return statistics.getInt("statistics." + player.getUUID().toString() + ".level");
    }

    public double getRequiredExp(UHCPlayer player) {
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
        return statistics.getInt("statistics." + player.getUUID().toString() + ".level")-1;
    }

    public void levelUp(UHCPlayer player) {

        statistics.set("statistics." + player.getUUID().toString() + ".level", getPlayerLevel(player)+1);
        statistics.set("statistics." + player.getUUID().toString() + ".requiredXP", setRequiredExp(player));
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        gameManager.getSoundManager().playLevelUP(player.getPlayer());

        player.sendMessage(Messages.LEVEL_UP.toString()
                .replace("%newLevel%", String.valueOf(getPlayerLevel(player)))
                .replace("%previousLevel%", String.valueOf(getPreviousLevel(player))));
    }
}