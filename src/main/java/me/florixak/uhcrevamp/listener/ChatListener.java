package me.florixak.uhcrevamp.listener;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.Permissions;
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

	public ChatListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void handlePlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		PlayerManager pm = gameManager.getPlayerManager();

		Player p = event.getPlayer();
		UHCPlayer uhcPlayer = pm.getUHCPlayer(event.getPlayer().getUniqueId());
		boolean isTeamMode = GameValues.TEAM.TEAM_MODE;
		boolean isGlobal = gameManager.isPlaying() && isTeamMode && event.getMessage().startsWith("!");

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

		String message = p.hasPermission(Permissions.COLOR_CHAT.getPerm()) ? TextUtils.color(event.getMessage()) : event.getMessage();
		format = PlaceholderUtil.setPlaceholders(format.replace("%message%", isGlobal ? message.replace("!", "") : message), p);
		final String finalFormat = format;

		switch (uhcPlayer.getState()) {
			case LOBBY:
				if (!gameManager.isPlaying()) {
					pm.getOnlinePlayers().forEach(uhcPlayers -> uhcPlayer.sendMessage(TextUtils.color(finalFormat)));
				}
				break;
			case DEAD:
			case SPECTATOR:
				pm.getSpectatorPlayers().forEach(uhcPlayers -> uhcPlayer.sendMessage(TextUtils.color(finalFormat)));
				break;
			case ALIVE:
				if (isTeamMode && !isGlobal) {
					uhcPlayer.getTeam().sendMessage(TextUtils.color(format));
				} else {
					pm.getOnlinePlayers().forEach(uhcPlayers -> uhcPlayer.sendMessage(TextUtils.color(finalFormat)));
				}
				break;
			default:
				UHCRevamp.getInstance().getLogger().info(uhcPlayer.getName() + " is trying to send a message, but has an invalid state: " + uhcPlayer.getState() + "!");
				break;
		}
	}

	@EventHandler
	public void handlePlayerCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		String msg = event.getMessage();
		String args[] = msg.split(" ");

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