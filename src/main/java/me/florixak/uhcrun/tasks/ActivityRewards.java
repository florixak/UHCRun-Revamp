package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.player.UHCPlayer;
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
                uhcPlayer.getData().addActivityRewards();
            }
        }
    }
}
