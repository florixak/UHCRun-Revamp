package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.BorderManager;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.gameManager.GameManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.utility.TimeConvertor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class FightingCountdown extends BukkitRunnable {

    private GameManager gameManager;
    private BorderManager borderManager;
    private FileConfiguration config;
    public static int count;

    public FightingCountdown(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("fighting-countdown");
        this.borderManager = UHCRun.plugin.getBorderManager();
    }

    @Override
    public void run() {
        if (count <= 0) {
            cancel();
            gameManager.setGameState(GameState.DEATHMATCH);
            return;
        }

        if (count <= 10) {
            Bukkit.broadcastMessage(Messages.DEATHMATCH.toString().replace("%count%", "" + TimeConvertor.convertCountdown(count)));
        }

        if (PlayerManager.online.size() < config.getInt("min-players-to-start")) {
            cancel();
            gameManager.setGameState(GameState.ENDING);
            return;
        }
        UHCRun.plugin.getUtilities().checkGame();
        borderManager.setSize(borderManager.getSize()-borderManager.getSpeed());
        count--;
    }
}
