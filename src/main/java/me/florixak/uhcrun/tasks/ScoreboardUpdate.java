package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdate extends BukkitRunnable {

    private final GameManager gameManager;

    public ScoreboardUpdate(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(gameManager.getScoreboardManager()::setScoreboard);
        Bukkit.getOnlinePlayers().forEach(gameManager.getTabManager()::setPlayerList);
    }
}
