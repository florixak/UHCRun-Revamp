package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class PvPPhaseTask extends BukkitRunnable {

    private final GameManager gameManager;
    private static int countdown;

    public PvPPhaseTask(GameManager gameManager) {
        this.gameManager = gameManager;
        countdown = GameValues.GAME.PVP_COUNTDOWN;
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

            gameManager.getPlayerManager().getOnlinePlayers()
                    .forEach(uhcPlayer -> gameManager.getSoundManager().playDeathmatchStartingSound(uhcPlayer.getPlayer()));
        }
        gameManager.getBorderManager().shrinkBorder();
        countdown--;
    }
}