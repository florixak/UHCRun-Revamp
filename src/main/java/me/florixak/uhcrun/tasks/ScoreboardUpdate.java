package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdate extends BukkitRunnable {

    private GameManager gameManager;

    public ScoreboardUpdate(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream().filter(player -> player.isOnline()).forEach(gameManager.getScoreboardManager()::setScoreboard);
        Bukkit.getOnlinePlayers().stream().filter(player -> player.isOnline()).forEach(gameManager.getTabManager()::setPlayerList);
    }
}
