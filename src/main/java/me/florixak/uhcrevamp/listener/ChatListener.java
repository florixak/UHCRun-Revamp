package me.florixak.uhcrevamp.listener;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.PlayerManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

	private final GameManager gameManager;

	public ChatListener(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void handlePlayerChat(final AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		final PlayerManager pm = gameManager.getPlayerManager();

		final Player p = event.getPlayer();
		final UHCPlayer uhcPlayer = pm.getUHCPlayer(event.getPlayer().getUniqueId());
		final boolean isTeamMode = GameValues.TEAM.TEAM_MODE;
		final boolean isGlobal = gameManager.isPlaying() && isTeamMode && event.getMessage().startsWith("!");

		String format = GameValues.CHAT.LOBBY_FORMAT;
		if (gameManager.isPlaying()) {
			if (uhcPlayer.isDead() || uhcPlayer.isSpectator()) {
				format = GameValues.CHAT.DEAD_FORMAT;
			} else if (isGlobal) {
				format = GameValues.CHAT.GLOBAL_FORMAT;
			} else if (isTeamMode) {
				format = GameValues.CHAT.TEAM_FORMAT;
			} else {
				format = GameValues.CHAT.SOLO_FORMAT;
			}
		}
		if (format == null || format.isEmpty()) return;
		final String message = event.getMessage();
		format = PlaceholderUtil.setPlaceholders(format.replace("%message%", isGlobal ? message.replace("!", "") : message), p);
		final String finalFormat = TextUtils.color(format);

		switch (uhcPlayer.getState()) {
			case LOBBY:
				if (!gameManager.isPlaying()) {
					for (final UHCPlayer uhcPlayers : pm.getOnlinePlayers()) {
						uhcPlayers.sendMessage(finalFormat);
					}
				}
				break;
			case DEAD:
			case SPECTATOR:
				for (final UHCPlayer spectator : pm.getSpectatorPlayers()) {
					spectator.sendMessage(finalFormat);
				}
				break;
			case ALIVE:
				if (isTeamMode && !isGlobal) {
					uhcPlayer.getTeam().sendMessage(format);
				} else {
					for (final UHCPlayer uhcPlayers : pm.getOnlinePlayers()) {
						uhcPlayers.sendMessage(finalFormat);
					}
				}
				break;
			default:
				UHCRevamp.getInstance().getLogger().info(uhcPlayer.getName() + " is trying to send a message, but has an invalid state: " + uhcPlayer.getState() + "!");
				break;
		}
	}

	@EventHandler
	public void handlePlayerCommand(final PlayerCommandPreprocessEvent event) {
		final Player p = event.getPlayer();
		final String msg = event.getMessage();
		final String[] args = msg.split(" ");

		if (GameValues.CHAT.BLOCKED_COMMANDS.contains(event.getMessage().toLowerCase())) {
			event.setCancelled(true);
			p.sendMessage(Messages.NO_PERM.toString());
		}

		if (Bukkit.getServer().getHelpMap().getHelpTopic(args[0]) == null) {
			event.setCancelled(true);
			p.sendMessage(Messages.INVALID_CMD.toString());
		}
	}

}