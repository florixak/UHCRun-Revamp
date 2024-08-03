package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCheckTask extends BukkitRunnable {

	private final GameManager gameManager;

	public GameCheckTask(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public boolean isGameEnd() {
//		return GameValues.TEAM.TEAM_MODE ? gameManager.getTeamManager().getLivingTeams().size() < 2 : gameManager.getPlayerManager().getAlivePlayers().size() < 2;
		return gameManager.getPlayerManager().getAlivePlayers().isEmpty();
	}

	public boolean canStart() {
		final int minPlayers = GameValues.GAME.PLAYERS_TO_START;
		final int playersOnline = gameManager.getPlayerManager().getOnlinePlayers().size();
		final boolean teamMode = GameValues.TEAM.TEAM_MODE;

//		if (teamMode) return areTeamsValid();
		return playersOnline >= minPlayers;
	}

	private boolean areTeamsValid() {
		final int maxPlayersPerTeam = GameValues.TEAM.TEAM_SIZE;
		final int minPlayersToStart = GameValues.GAME.PLAYERS_TO_START;
		final int totalPlayers = gameManager.getPlayerManager().getOnlinePlayers().size();
		final int totalTeams = gameManager.getTeamManager().getTeamsList().size();

		// Check if there are enough players to start
		if (totalPlayers < minPlayersToStart) {
			return false;
		}

		// Ensure not all players are in the same team
		if (totalPlayers > maxPlayersPerTeam) {
			return false;
		}

		// Check if any team exceeds the maximum allowed size
		for (final UHCTeam team : gameManager.getTeamManager().getTeamsList()) {
			if (team.getMembers().size() > maxPlayersPerTeam) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void run() {
		final boolean gameEnd = isGameEnd();
		final boolean canStart = canStart();

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
					gameManager.getTaskManager().cancelStartingTask();
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