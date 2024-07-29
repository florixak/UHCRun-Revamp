package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathmatchPhaseTask extends BukkitRunnable {

    private final GameManager gameManager;
    private static int countdown;

    public DeathmatchPhaseTask(GameManager gameManager) {
        this.gameManager = gameManager;
        countdown = GameValues.GAME.DEATHMATCH_COUNTDOWN;
    }

    public static int getCountdown() {
        return countdown;
    }

    @Override
    public void run() {

        if (countdown <= 0) {
            cancel();
            gameManager.setGameState(GameState.ENDING);
            return;
        }
        gameManager.getBorderManager().shrinkBorder();
        countdown--;
    }
}
