package me.florixak.uhcrun.task;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.SoundManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class FightingCd {

    private UHCRun plugin;
    private FileConfiguration config;
    public static int count;

    public FightingCd(UHCRun plugin) {
        this.plugin = plugin;
        this.config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("fighting-countdown");
    }

    public void startCountdown() {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (count <= 0) {
                    cancel();
                    plugin.getGame().setGameState(GameState.DEATHMATCH);
                    return;
                }

                if (count <= 10) {
                    Bukkit.broadcastMessage(Messages.DEATHMATCH_STARTING.toString().replace("%countdown%", "" + TimeUtils.convertCountdown(count)));
                    for (UUID uuid : PlayerManager.online) {
                        SoundManager.playDMStarts(Bukkit.getPlayer(uuid));
                    }
                }


                plugin.getGame().checkGame();
                plugin.getBorderManager().setSize(plugin.getBorderManager().getSize()-plugin.getBorderManager().getSpeed());
                count--;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

}
