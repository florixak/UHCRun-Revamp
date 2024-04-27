package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameConstants;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class PvPCD extends BukkitRunnable {

    private final GameManager gameManager;
    private static int countdown;

    public PvPCD(GameManager gameManager) {
        this.gameManager = gameManager;
        countdown = GameConstants.PVP_COUNTDOWN;
    }

    public static int getCountdown() {
        return countdown;
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
                    .replace("%countdown%", TimeUtils.getFormattedTime(countdown)));

            gameManager.getPlayerManager().getAliveList()
                    .forEach(uhcPlayer -> gameManager.getSoundManager().playDMStarts(uhcPlayer.getPlayer()));
        }
        gameManager.getBorderManager().setSize(gameManager.getBorderManager().getSize()-gameManager.getBorderManager().getSpeed());
        countdown--;
    }
}