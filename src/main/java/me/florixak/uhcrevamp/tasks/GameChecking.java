package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameChecking extends BukkitRunnable {

    private final GameManager gameManager;

    private final int minPlayers;

    public GameChecking(GameManager gameManager) {
        this.gameManager = gameManager;

        this.minPlayers = GameValues.GAME.MIN_PLAYERS;
    }

    @Override
    public void run() {

        switch (gameManager.getGameState()) {
            case LOBBY:
                if (Bukkit.getOnlinePlayers().size() >= minPlayers) {
                    gameManager.setGameState(GameState.STARTING);
                }
                break;
            case STARTING:
//                if (gameManager.isForceStarted()) {
//                    return;
//                }
                if (Bukkit.getOnlinePlayers().size() < minPlayers) {
                    gameManager.getTaskManager().stopStartingCD();
                    Utils.broadcast(Messages.GAME_STARTING_CANCELED.toString());
                    gameManager.setGameState(GameState.LOBBY);
                }
                break;
            case MINING:
            case PVP:
            case DEATHMATCH:
//                if (gameManager.isForceStarted()) {
//                    return;
//                }
                if (gameManager.getPlayerManager().getAliveList().size() < minPlayers) {
                    gameManager.setGameState(GameState.ENDING);
                }
                break;
            case ENDING:
                break;
        }
    }
}