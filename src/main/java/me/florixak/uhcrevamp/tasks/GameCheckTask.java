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
		return GameValues.TEAM.TEAM_MODE ? gameManager.getTeamManager().getLivingTeams().size() < 2 : gameManager.getPlayerManager().getAlivePlayers().size() < 2;
//		return gameManager.getPlayerManager().getAlivePlayers().isEmpty();
	}

	public boolean canStart() {
		final int minPlayers = GameValues.GAME.PLAYERS_TO_START;
		final int playersOnline = gameManager.getPlayerManager().getOnlinePlayers().size();
		if (GameValues.TEAM.TEAM_MODE) {
			return validTeamsToStartGame();
		}
		return playersOnline >= minPlayers;
	}

	private boolean validTeamsToStartGame() {
		final int minPlayers = GameValues.GAME.PLAYERS_TO_START;
		final int playersOnline = gameManager.getPlayerManager().getOnlinePlayers().size();

		if (playersOnline < minPlayers) {
			return false;
		}
		for (final UHCTeam team : gameManager.getTeamManager().getTeamsList()) {
			if (team.getMembers().size() == playersOnline) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void run() {

		switch (gameManager.getGameState()) {
			case LOBBY:
				if (canStart() && !gameManager.isStarting()) {
					gameManager.setGameState(GameState.STARTING);
				}
				break;
			case STARTING:
				if (!canStart()) {
					gameManager.getTaskManager().cancelStartingTask();
					Utils.broadcast(Messages.GAME_STARTING_CANCELED.toString());
					gameManager.setGameState(GameState.LOBBY);
				}
				break;
			case MINING:
			case PVP:
			case DEATHMATCH:
				if (isGameEnd()) {
					gameManager.setGameState(GameState.ENDING);
				}
				break;
			case ENDING:
				break;
		}
	}
}