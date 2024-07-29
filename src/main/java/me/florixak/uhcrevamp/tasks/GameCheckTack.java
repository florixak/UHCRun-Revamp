package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCheckTack extends BukkitRunnable {

    private final GameManager gameManager;

    public GameCheckTack(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean isGameEnd() {
//        return GameValues.TEAM.TEAM_MODE ? gameManager.getTeamManager().getLivingTeams().size() < 2 : gameManager.getPlayerManager().getAlivePlayers().size() < 2;
        return gameManager.getPlayerManager().getAlivePlayers().isEmpty();
    }

    public boolean canStart() {
//        return GameValues.TEAM.TEAM_MODE ?
        return !gameManager.getPlayerManager().getOnlinePlayers().isEmpty();
    }

    @Override
    public void run() {
        boolean gameEnd = isGameEnd();
        boolean canStart = canStart();

        switch (gameManager.getGameState()) {
            case LOBBY:
                if (canStart) {
                    gameManager.setGameState(GameState.STARTING);
                }
//                if (!gameManager.getPlayerManager().getPlayers().isEmpty()) {
//                    Bukkit.getLogger().info(gameManager.getPlayerManager().getPlayers().size() + ". Name: "
//                                    + gameManager.getPlayerManager().getPlayers().get(0).getName() + " Activity: "
//                                    + gameManager.getPlayerManager().getPlayers().get(0).getUHCExpForActivity() + " Kills: "
//                                    + gameManager.getPlayerManager().getPlayers().get(0).getKills() + " Assists: "
//                                    + gameManager.getPlayerManager().getPlayers().get(0).getAssists() + " Perk: "
//                                    + gameManager.getPlayerManager().getPlayers().get(0).getKit().getName() + " Money for kills:"
//                                    + gameManager.getPlayerManager().getPlayers().get(0).getMoneyForKills() + " "
//                            // team nelze použít
//                            // lze kity, perky, uuid, name,
//                    );
//
//                }
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