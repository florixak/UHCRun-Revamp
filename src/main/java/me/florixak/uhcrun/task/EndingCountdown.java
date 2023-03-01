package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.manager.BorderManager;
import me.florixak.uhcrun.manager.gameManager.GameManager;
import me.florixak.uhcrun.utility.Utilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingCountdown extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;
    private BorderManager borderManager;
    public static int count;

    public EndingCountdown(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("ending-countdown");
        this.borderManager = UHCRun.plugin.getBorderManager();
    }

    @Override
    public void run() {
        if (count <= 0) {
            cancel();
            borderManager.resetBorder();
            gameManager.resetGame();
            return;
        }
        count--;
    }
}
