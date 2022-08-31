package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
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
        if (!(plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING)) {
            for (UUID uuid : PlayerManager.online) {
                double money = config.getDouble("rewards-per-time.money");
                double level_xp = config.getDouble("rewards-per-time.level-xp");

                plugin.getStatisticManager().addMoney(Bukkit.getPlayer(uuid), money);
                plugin.getLevelManager().addPlayerLevel(Bukkit.getPlayer(uuid).getUniqueId(), level_xp);

                Bukkit.getPlayer(uuid).sendMessage(Messages.REWARDS_PER_TIME.toString()
                        .replace("%money-per-time%", String.valueOf(money))
                        .replace("%xp-per-time%", String.valueOf(level_xp)));
            }
        }
    }
}
