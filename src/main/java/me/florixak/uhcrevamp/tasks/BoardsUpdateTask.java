package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.game.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

public class BoardsUpdateTask extends BukkitRunnable {

	private final GameManager gameManager;

	public BoardsUpdateTask(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public void run() {
		gameManager.getPlayerManager().getPlayers().forEach(uhcP -> gameManager.getScoreboardManager().updateScoreboard(uhcP.getUUID()));
		gameManager.getPlayerManager().getPlayers().forEach(uhcP -> gameManager.getTabManager().setPlayerList(uhcP.getPlayer()));
	}
}
