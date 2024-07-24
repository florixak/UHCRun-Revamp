package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingCD extends BukkitRunnable {

    private final GameManager gameManager;
    private static int countdown;
    private final int startingMessageAt;

    public StartingCD(GameManager gameManager) {
        this.gameManager = gameManager;
        this.startingMessageAt = GameValues.GAME.STARTING_MESSAGE_AT;
        countdown = GameValues.GAME.STARTING_COUNTDOWN;
    }

    public static int getCountdown() {
        return countdown;
    }

    @Override
    public void run() {

        if (countdown <= 0) {
            cancel();
            Utils.broadcast(Messages.GAME_STARTED.toString());
            Bukkit.getOnlinePlayers().forEach(player -> gameManager.getSoundManager().playGameStarted(player));
            gameManager.setGameState(GameState.MINING);
            return;
        }

        if (countdown <= startingMessageAt) {
            Utils.broadcast(Messages.GAME_STARTING.toString()
                    .replace("%countdown%", "" + TimeUtils.getFormattedTime((int) countdown)));
            Bukkit.getOnlinePlayers().forEach(player -> gameManager.getSoundManager().playStartingSound(player));
        }

        countdown--;
    }
}