package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ActivityRewards extends BukkitRunnable {

    private UHCRun plugin;
    private FileConfiguration config;

    public ActivityRewards(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    @Override
    public void run() {
        if (!(plugin.getGame().getGameState() == GameState.WAITING
                || plugin.getGame().getGameState() == GameState.STARTING)) {
            for (UHCPlayer player : plugin.getPlayerManager().getAliveList()) {
                double money = config.getDouble("rewards-per-time.money");
                double level_xp = config.getDouble("rewards-per-time.level-xp");

                plugin.getStatistics().addMoney(player, money);
                plugin.getLevelManager().addPlayerLevel(player, level_xp);

                player.sendMessage(Messages.REWARDS_PER_TIME.toString()
                        .replace("%money-per-time%", String.valueOf(money))
                        .replace("%xp-per-time%", String.valueOf(level_xp)));
            }
        }
    }
}
