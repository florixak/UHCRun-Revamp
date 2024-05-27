package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ActivityRewards extends BukkitRunnable {

    private final GameManager gameManager;

    public ActivityRewards(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        if (gameManager.isPlaying()) {
            for (UHCPlayer uhcPlayer : gameManager.getPlayerManager().getAliveList()) {
                double money = GameValues.ACTIVITY_REWARDS_MONEY;
                double playerExp = GameValues.ACTIVITY_REWARDS_EXP;

                uhcPlayer.getData().depositMoney(money);
                uhcPlayer.getData().addUHCExp(playerExp);

                uhcPlayer.sendMessage(Messages.REWARDS_PER_TIME.toString()
                        .replace("%money-per-time%", String.valueOf(money))
                        .replace("%xp-per-time%", String.valueOf(playerExp)));
            }
        }
    }
}
