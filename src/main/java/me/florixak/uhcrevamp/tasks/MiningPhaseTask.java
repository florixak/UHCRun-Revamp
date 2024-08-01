package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class MiningPhaseTask extends BukkitRunnable {

	private final GameManager gameManager;

	private int countdown;

	public MiningPhaseTask(GameManager gameManager) {
		this.gameManager = gameManager;
		countdown = GameValues.GAME.MINING_COUNTDOWN;
	}

	public int getCountdown() {
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
