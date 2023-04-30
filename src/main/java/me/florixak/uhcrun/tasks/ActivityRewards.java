package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class ActivityRewards extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;

    public ActivityRewards(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    @Override
    public void run() {
        if (!(gameManager.getGameState() == GameState.LOBBY
                || gameManager.getGameState() == GameState.STARTING)) {
            for (UHCPlayer player : gameManager.getPlayerManager().getAlivePlayers()) {
                double money = config.getDouble("rewards-per-time.money");
                double level_xp = config.getDouble("rewards-per-time.level-xp");

                gameManager.getStatistics().addMoney(player, money);
                gameManager.getLevelManager().addPlayerLevel(player, level_xp);

                player.sendMessage(Messages.REWARDS_PER_TIME.toString()
                        .replace("%money-per-time%", String.valueOf(money))
                        .replace("%xp-per-time%", String.valueOf(level_xp)));
            }
        }
    }
}
