package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.manager.gameManager.GameManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class MiningCd  {

    private UHCRun plugin;
    private FileConfiguration config;
    public static int count;

    public MiningCd(UHCRun plugin) {
        this.config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("mining-countdown");
    }
    public void startCountdown() {
        new BukkitRunnable() {
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
        }.runTaskTimer(plugin, 20L, 20L)
    }

}
