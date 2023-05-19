package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayingRewards extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;

    public PlayingRewards(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    @Override
    public void run() {
        if (!(gameManager.getGameState() == GameState.LOBBY
                || gameManager.getGameState() == GameState.STARTING)) {
            String path = "settings.rewards.playing-time";
            for (UHCPlayer uhcPlayer : gameManager.getPlayerManager().getAlivePlayers()) {
                double money = config.getDouble(path + ".money");
                double player_exp = config.getDouble(path + ".player-exp");

                uhcPlayer.getData().addMoney(money);
                uhcPlayer.getData().addExp(player_exp);

                uhcPlayer.sendMessage(Messages.REWARDS_PER_TIME.toString()
                        .replace("%money-per-time%", String.valueOf(money))
                        .replace("%xp-per-time%", String.valueOf(player_exp)));
            }
        }
    }
}
