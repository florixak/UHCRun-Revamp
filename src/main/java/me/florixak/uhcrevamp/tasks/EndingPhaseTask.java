package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingPhaseTask extends BukkitRunnable {

	private int countdown;

	public EndingPhaseTask() {
		countdown = GameValues.GAME.ENDING_COUNTDOWN;
	}

	public int getCountdown() {
		return countdown;
	}

	@Override
	public void run() {

		if (countdown <= 0) {
			cancel();
			Bukkit.shutdown();
			return;
		}
		countdown--;
	}
}