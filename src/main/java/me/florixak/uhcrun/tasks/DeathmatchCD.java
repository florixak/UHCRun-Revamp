package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathmatchCD extends BukkitRunnable {

    private final GameManager gameManager;
    private static int countdown;

    public DeathmatchCD(GameManager gameManager) {
        this.gameManager = gameManager;
        countdown = GameValues.DEATHMATCH_COUNTDOWN;
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
