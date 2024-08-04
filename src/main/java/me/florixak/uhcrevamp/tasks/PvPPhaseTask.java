package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class PvPPhaseTask extends BukkitRunnable {

	private final GameManager gameManager;
	private int countdown;

	public PvPPhaseTask(final GameManager gameManager) {
		this.gameManager = gameManager;
		countdown = GameValues.GAME.PVP_COUNTDOWN;
	}

	public int getCountdown() {
		return countdown;
	}

	@Override
	public void run() {

		if (countdown <= 0) {
			cancel();
			gameManager.setGameState(GameState.DEATHMATCH);
			return;
		}

		if (countdown <= 10) {
			Utils.broadcast(Messages.DEATHMATCH_IN.toString()
					.replace("%countdown%", TimeUtils.getFormattedTime(countdown)));

			gameManager.getPlayerManager().getPlayers()
					.forEach(uhcPlayer -> gameManager.getSoundManager().playDeathmatchStartingSound(uhcPlayer.getPlayer()));
		}
//		gameManager.getBorderManager().shrinkBorder();
		countdown--;

		if (countdown > 10 && gameManager.getPlayerManager().getOnlinePlayers().size() <= GameValues.GAME.START_DEATHMATCH_AT) {
			countdown = 10;
		}
	}
}