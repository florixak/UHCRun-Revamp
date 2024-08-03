package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class PlayerTimeTask extends BukkitRunnable {

	private final GameManager gameManager;

	private final long rewardInterval;
	private final HashMap<UUID, Long> lastRewardTimes;

	public PlayerTimeTask(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.lastRewardTimes = new HashMap<>();
		this.rewardInterval = 1 * 60 * 1000;

	}

	@Override
	public void run() {
		if (gameManager.isPlaying()) return;
		rewardActivePlayers();
	}

	public HashMap<UUID, Long> getLastRewardTimes() {
		return lastRewardTimes;
	}

	private void rewardActivePlayers() {
		final long currentTime = System.currentTimeMillis();
		for (final UHCPlayer uhcPlayer : gameManager.getPlayerManager().getAlivePlayers()) {
			final long lastRewardTime = lastRewardTimes.getOrDefault(uhcPlayer.getUUID(), 0L);

			if (currentTime - lastRewardTime >= rewardInterval) {
				lastRewardTimes.put(uhcPlayer.getUUID(), currentTime);
			}
		}
	}
}
