package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.manager.gameManager.GameManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathMCd extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;
    public static int count;

    public DeathMCd(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("deathmatch-countdown");
    }

    @Override
    public void run() {

        if (count <= 0) {
            cancel();
            gameManager.setGameState(GameState.ENDING);
            return;
        }

//        gameManager.checkGame();
        count--;
    }
}
