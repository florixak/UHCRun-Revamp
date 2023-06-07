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

public class FightingCD extends BukkitRunnable {

    private final GameManager gameManager;
    public static int countdown;

    public FightingCD(GameManager gameManager) {
        this.gameManager = gameManager;
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        countdown = config.getInt("settings.game.countdowns.fighting");
    }

    @Override
    public void run() {

        if (countdown <= 0) {
            cancel();
            if (gameManager.getDeathmatchManager().isDeathmatchEnabled()) {
                gameManager.setGameState(GameState.DEATHMATCH);
            } else {
                gameManager.setGameState(GameState.ENDING);
            }
            return;
        }

        if (countdown <= 10) {
            Utils.broadcast(Messages.DEATHMATCH_IN.toString()
                    .replace("%countdown%", "" + TimeUtils.getFormattedTime(countdown)));

            gameManager.getPlayerManager().getPlayers().stream().filter(UHCPlayer::isAlive)
                    .forEach(uhcPlayer -> gameManager.getSoundManager().playDMStarts(uhcPlayer.getPlayer()));
        }
        gameManager.getBorderManager().setSize(gameManager.getBorderManager().getSize()-gameManager.getBorderManager().getSpeed());
        countdown--;
    }
}