package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingCd extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;
    public static int count;
    private int startWarning;

    public StartingCd(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("starting-countdown");
        this.startWarning = config.getInt("starting-warning-time");
    }

    @Override
    public void run() {

        if (count <= 0) {
            cancel();
            Utils.broadcast(Messages.GAME_STARTED.toString());
            Bukkit.getOnlinePlayers().forEach(player -> gameManager.getSoundManager().playGameStarted(player));
            gameManager.getGameManager().setGameState(GameState.MINING);
            return;
        }

        if (count <= startWarning) {
            Utils.broadcast(Messages.GAME_STARTING.toString()
                    .replace("%countdown%", "" + TimeUtils.getFormattedTime(count)));
            Bukkit.getOnlinePlayers().forEach(player -> gameManager.getSoundManager().playStartingSound(player));
        }
        count--;
    }

}