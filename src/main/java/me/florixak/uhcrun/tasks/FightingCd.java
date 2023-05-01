package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class FightingCd extends BukkitRunnable {

    private GameManager gameManager;
    private FileConfiguration config;
    public static int count;

    public FightingCd(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.count = config.getInt("settings.game.countdowns.fighting");
    }

    @Override
    public void run() {

        if (count <= 0) {
            cancel();
            if (gameManager.isDeathmatchEnable()) {
                gameManager.setGameState(GameState.DEATHMATCH);
            } else {
                gameManager.setGameState(GameState.ENDING);
            }
            return;
        }

        if (count <= 10) {
            Utils.broadcast(Messages.DEATHMATCH_STARTING.toString()
                    .replace("%countdown%", "" + TimeUtils.getFormattedTime(count)));

            gameManager.getPlayerManager().getPlayers().stream().filter(UHCPlayer::isAlive)
                    .forEach(uhcPlayer -> gameManager.getSoundManager().playDMStarts(uhcPlayer.getPlayer()));
        }
        gameManager.getBorderManager().setSize(gameManager.getBorderManager().getSize()-gameManager.getBorderManager().getSpeed());
        count--;
    }
}