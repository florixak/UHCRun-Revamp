package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingCd extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;
    public static int count;

    public EndingCd(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("ending-countdown");
    }

    @Override
    public void run() {

        if (count <= 0) {
            cancel();
            Bukkit.shutdown();
            return;
        }
        count--;
    }
}