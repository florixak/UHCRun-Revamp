package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class MiningCD extends BukkitRunnable {

    private final GameManager gameManager;

    private static int countdown;

    public MiningCD(GameManager gameManager) {
        this.gameManager = gameManager;
        countdown = GameValues.GAME.MINING_COUNTDOWN;
    }

    public static int getCountdown() {
        return countdown;
    }

    @Override
    public void run() {

        if (countdown <= 0) {
            cancel();
            gameManager.setGameState(GameState.PVP);
            return;
        }

        if (countdown <= 10) {
            Utils.broadcast(Messages.PVP_IN.toString()
                    .replace("%countdown%", TimeUtils.getFormattedTime(countdown)));
        }
        countdown--;
    }

}
