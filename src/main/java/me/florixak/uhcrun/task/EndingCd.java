package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingCd extends BukkitRunnable {

    private UHCRun plugin;
    private FileConfiguration config;
    public static int count;

    public EndingCd(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("ending-countdown");
    }

    @Override
    public void run() {
        if (count <= 0) {
            cancel();
            plugin.getBorderManager().resetBorder();
            plugin.getGame().resetGame();
            return;
        }
        count--;
    }
}