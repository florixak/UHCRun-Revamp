package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathMCd extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;
    public static int count;

    public DeathMCd(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("deathmatch-countdown");
    }

    @Override
    public void run() {

        if (count <= 0) {
            cancel();
            gameManager.setGameState(GameState.ENDING);
            return;
        }

        count--;
    }
}
