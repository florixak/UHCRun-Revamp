package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCheckTack extends BukkitRunnable {

    private final GameManager gameManager;
    private final boolean canStart;
    private final boolean gameEnd;

    public GameCheckTack(GameManager gameManager) {
        this.gameManager = gameManager;

        this.canStart = canStart();
        this.gameEnd = isGameEnd();
    }

    public boolean isGameEnd() {
        return GameValues.TEAM.TEAM_MODE ? gameManager.getTeamManager().getLivingTeams().size() < 2 : gameManager.getPlayerManager().getAlivePlayers().size() < 2;
    }

    public boolean canStart() {
        return gameManager.getPlayerManager().getPlayers().size() >= GameValues.GAME.PLAYERS_TO_START;
    }

    @Override
    public void run() {

        switch (gameManager.getGameState()) {
            case LOBBY:
                if (canStart) {
                    gameManager.setGameState(GameState.STARTING);
                }
                break;
            case STARTING:
                if (!canStart) {
                    gameManager.getTaskManager().stopStartingTask();
                    Utils.broadcast(Messages.GAME_STARTING_CANCELED.toString());
                    gameManager.setGameState(GameState.LOBBY);
                }
                break;
            case MINING:
            case PVP:
            case DEATHMATCH:
                if (gameEnd) {
                    gameManager.setGameState(GameState.ENDING);
                }
                break;
            case ENDING:
                break;
        }
    }
}