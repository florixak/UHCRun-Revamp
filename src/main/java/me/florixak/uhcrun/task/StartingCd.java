package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.TitleAction;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.SoundManager;
import me.florixak.uhcrun.manager.gameManager.GameManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class StartingCd {

    private UHCRun plugin;
    private FileConfiguration config;
    public static int count;
    private int startWarning;
    private TitleAction titleAction;

    public StartingCd(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("starting-countdown");
        this.startWarning = config.getInt("starting-warning-time");
        this.titleAction = new TitleAction();
    }

    public void startCountdown() {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (count <= 0) {
                    cancel();
                    plugin.getGame().setGameState(GameState.MINING);
                    return;
                }

                if (count <= startWarning) {
                    Utils.broadcast(Messages.GAME_STARTING.toString()
                            .replace("%countdown%", "" + TimeUtils.convertCountdown(count)));
                    for (UUID uuid : PlayerManager.online) {
                        SoundManager.playStartingSound(Bukkit.getPlayer(uuid));
                    }
                }

                plugin.getGame().checkGame();

                count--;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

}