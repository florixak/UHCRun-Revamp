package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.TitleAction;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.SoundManager;
import me.florixak.uhcrun.manager.gameManager.GameManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.utility.TimeConvertor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class StartingCountdown extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;
    public static int count;
    private int startWarning;
    private TitleAction titleAction;

    public StartingCountdown(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("starting-countdown");
        this.startWarning = config.getInt("starting-warning-time");
        this.titleAction = new TitleAction();
    }

    @Override
    public void run() {
        if (count <= 0) {
            cancel();
            gameManager.setGameState(GameState.MINING);
//            for (UUID uuid : PlayerManager.online) {
//                UHCRun.plugin.getUtilities().preparePlayers(Bukkit.getPlayer(uuid));
//            }
            return;
        }

        if (count <= startWarning) {
            Bukkit.broadcastMessage(Messages.GAME_STARTING.toString()
                    .replace("%countdown%", "" + TimeConvertor.convertCountdown(count)));
            for (UUID uuid : PlayerManager.online) {
                UHCRun.plugin.getSoundManager().playStartingSound(Bukkit.getPlayer(uuid));
            }
        }

        if (PlayerManager.online.size() < config.getInt("min-players-to-start")) {
            cancel();
            gameManager.setGameState(GameState.WAITING);
            Bukkit.broadcastMessage(Messages.GAME_STARTING_CANCELED.toString());
            return;
        }

        count--;
    }
}