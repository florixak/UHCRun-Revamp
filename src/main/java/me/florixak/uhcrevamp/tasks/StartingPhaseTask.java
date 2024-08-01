package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingPhaseTask extends BukkitRunnable {

	private final GameManager gameManager;
	private int countdown;
	private final int startingMessageAt;

	public StartingPhaseTask(GameManager gameManager) {
		this.gameManager = gameManager;
		this.startingMessageAt = GameValues.GAME.STARTING_MESSAGE_AT;
		countdown = GameValues.GAME.STARTING_COUNTDOWN;
	}

	public int getCountdown() {
		return countdown;
	}

	@Override
	public void run() {

		if (countdown <= 0) {
			cancel();
			Utils.broadcast(Messages.GAME_STARTED.toString());
			gameManager.getPlayerManager().getPlayers().forEach(player -> gameManager.getSoundManager().playGameStartedSound(player.getPlayer()));
			gameManager.setGameState(GameState.MINING);
			return;
		}

		if (countdown <= startingMessageAt) {
			Utils.broadcast(PlaceholderUtil.setPlaceholders(Messages.GAME_STARTING.toString(), null));
			gameManager.getPlayerManager().getPlayers().forEach(player -> gameManager.getSoundManager().playGameStartingSound(player.getPlayer()));
		}

		countdown--;
	}
}