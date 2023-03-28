package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.manager.gameManager.GameState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class MiningCd extends BukkitRunnable {

    private UHCRun plugin;
    private FileConfiguration config;
    public static int count;

    public MiningCd(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("mining-countdown");
    }

    @Override
    public void run() {

        if (count <= 0) {
            cancel();
            plugin.getGame().setGameState(GameState.FIGHTING);
            return;
        }
        plugin.getGame().checkGame();
        count--;
    }

}
