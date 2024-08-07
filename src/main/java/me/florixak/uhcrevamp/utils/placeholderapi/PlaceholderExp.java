package me.florixak.uhcrevamp.utils.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlaceholderExp extends PlaceholderExpansion {

	private final UHCRevamp plugin;

	public PlaceholderExp(final UHCRevamp plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getIdentifier() {
		return "uhcrevamp";
	}

	@Override
	public String getAuthor() {
		return "FloriXak";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public boolean persist() {
		return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String onPlaceholderRequest(final Player p, final String params) {
		final UHCPlayer uhcPlayer = plugin.getGameManager().getPlayerManager().getUHCPlayer(p.getUniqueId());

		if (params.equalsIgnoreCase("uhc-author")) {
			return getAuthor();
		}

		if (params.equalsIgnoreCase("uhc-version")) {
			return getVersion();
		}

		if (params.equalsIgnoreCase("uhc-prefix")) {
			return Messages.PREFIX.toString();
		}

		if (params.equalsIgnoreCase("uhc-currency")) {
			return Messages.CURRENCY.toString();
		}

		if (params.equalsIgnoreCase("uhc-world")) {
			return GameValues.WORLD_NAME;
		}

		if (params.equalsIgnoreCase("uhc-border-size")) {
			final DecimalFormat format = new DecimalFormat("#######0");
			final String formattedBorderSize = "+" + format.format(plugin.getGameManager().getBorderManager().getSize()) + " -" + format.format(plugin.getGameManager().getBorderManager().getSize());
			return formattedBorderSize;
		}

		if (params.equalsIgnoreCase("uhc-player")) {
			return uhcPlayer.getName();
		}

		if (params.equalsIgnoreCase("uhc-countdown")) {
			return TimeUtils.getFormattedTime(plugin.getGameManager().getCurrentCountdown());
		}

		if (params.equalsIgnoreCase("uhc-players")) {
			return String.valueOf(plugin.getGameManager().getPlayerManager().getOnlinePlayers().size());
		}

		if (params.equalsIgnoreCase("uhc-max-players")) {
			return String.valueOf(plugin.getGameManager().getPlayerManager().getMaxPlayers());
		}

		if (params.equalsIgnoreCase("uhc-players-to-start")) {
			return String.valueOf(GameValues.GAME.PLAYERS_TO_START);
		}

		if (params.equalsIgnoreCase("uhc-alive-players")) {
			return String.valueOf(plugin.getGameManager().getPlayerManager().getAlivePlayers().size());
		}

		if (params.equalsIgnoreCase("uhc-dead-players")) {
			return String.valueOf(plugin.getGameManager().getPlayerManager().getDeadPlayers().size());
		}

		if (params.equalsIgnoreCase("uhc-spectators")) {
			return String.valueOf(plugin.getGameManager().getPlayerManager().getSpectatorPlayers().size());
		}

		if (params.equalsIgnoreCase("uhc-winner")) {
			return plugin.getGameManager().getPlayerManager().getUHCWinner();
		}

		if (params.equalsIgnoreCase("uhc-team")) {
			if (!GameValues.TEAM.TEAM_MODE) return "";
			if (!uhcPlayer.hasTeam()) return Messages.TEAM_NONE.toString();
			return TextUtils.color(uhcPlayer.getTeam().getDisplayName());
		}

		if (params.equalsIgnoreCase("uhc-mode")) {
			if (GameValues.TEAM.TEAM_MODE) return Messages.GAME_TEAMS.toString();
			return Messages.GAME_SOLO.toString();
		}

		if (params.equalsIgnoreCase("uhc-kills")) {
			return String.valueOf(uhcPlayer.getKills());
		}

		if (params.equalsIgnoreCase("uhc-killstreak")) {
			return String.valueOf(uhcPlayer.getData().getKillstreak());
		}

		if (params.equalsIgnoreCase("uhc-assists")) {
			return String.valueOf(uhcPlayer.getAssists());
		}

		if (params.equalsIgnoreCase("uhc-level")) {
			return String.valueOf(uhcPlayer.getData().getUHCLevel());
		}

		if (params.equalsIgnoreCase("uhc-exp")) {
			return String.valueOf(uhcPlayer.getData().getUHCExp());
		}

		if (params.equalsIgnoreCase("uhc-required-exp")) {
			return String.valueOf(uhcPlayer.getData().getRequiredUHCExp());
		}

		if (params.equalsIgnoreCase("uhc-ping")) {
			return String.valueOf(p.getPing());
		}

		if (params.equalsIgnoreCase("uhc-money")) {
			return String.valueOf(uhcPlayer.getData().getMoney());
		}

		if (params.equalsIgnoreCase("uhc-total-kills")) {
			return String.valueOf(uhcPlayer.getData().getKills());
		}

		if (params.equalsIgnoreCase("uhc-total-deaths")) {
			return String.valueOf(uhcPlayer.getData().getDeaths());
		}

		if (params.equalsIgnoreCase("uhc-total-wins")) {
			return String.valueOf(uhcPlayer.getData().getWins());
		}

		if (params.equalsIgnoreCase("uhc-total-losses")) {
			return String.valueOf(uhcPlayer.getData().getDeaths());
		}

		if (params.equalsIgnoreCase("uhc-games-played")) {
			return String.valueOf(uhcPlayer.getData().getGamesPlayed());
		}
		return null;
	}
}
