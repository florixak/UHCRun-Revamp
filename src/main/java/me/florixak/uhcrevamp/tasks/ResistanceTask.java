package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class ResistanceTask extends BukkitRunnable {

	private final GameManager gameManager;
	private int countdown;

	public ResistanceTask(GameManager gameManager) {
		this.gameManager = gameManager;
		this.countdown = GameValues.GAME.RESISTANCE_COUNTDOWN;
	}

	public int getCountdown() {
		return countdown;
	}

	@Override
	public void run() {
		if (countdown <= 0) {
			cancel();
			gameManager.setResistance(false);
			Utils.broadcast(Messages.PVP.toString());
			return;
		}

		if (countdown <= 10) {
			Utils.broadcast(Messages.PVP_IN.toString()
					.replace("%countdown%", TimeUtils.getFormattedTime(countdown)));
		}
		countdown--;
	}
}
