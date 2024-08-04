package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingPhaseTask extends BukkitRunnable {

	private final GameManager gameManager;
	private int countdown;

	public EndingPhaseTask(final GameManager gameManager) {
		this.gameManager = gameManager;
		countdown = GameValues.GAME.ENDING_COUNTDOWN;
	}

	public int getCountdown() {
		return countdown;
	}

	@Override
	public void run() {

		if (countdown <= 0) {
			cancel();
			if (GameValues.BUNGEECORD.ENABLED) {
				gameManager.getPlayerManager().sendPlayersToBungeeLobby();
			}
			Bukkit.shutdown();
			return;
		}
		countdown--;
	}
}