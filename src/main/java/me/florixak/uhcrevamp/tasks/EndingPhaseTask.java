package me.florixak.uhcrevamp.tasks;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
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
				try {
					for (final UHCPlayer uhcPlayer : gameManager.getPlayerManager().getOnlinePlayers()) {
						final ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("ConnectOther");
						out.writeUTF(uhcPlayer.getName());
						out.writeUTF(GameValues.BUNGEECORD.LOBBY_SERVER);
					}
					Bukkit.getLogger().info("Sending players to the lobby server.");
				} catch (final Exception e) {
					Bukkit.getLogger().info("Error while connecting players to the lobby server.");
				}
			}
			Bukkit.shutdown();
			return;
		}
		countdown--;
	}
}