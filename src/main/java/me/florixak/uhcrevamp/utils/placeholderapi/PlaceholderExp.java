package me.florixak.uhcrevamp.utils.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlaceholderExp extends PlaceholderExpansion {

	private final GameManager gameManager;

	public PlaceholderExp(final UHCRevamp plugin) {
		gameManager = plugin.getGameManager();
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
		final String placeholder = params.toLowerCase();

		if (p != null) {

			final UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

			if (placeholder.equals("uhc-player")) {
				return uhcPlayer.getName();
			}

			if (placeholder.equals("uhc-team")) {
				if (!GameValues.TEAM.TEAM_MODE) return "";
				if (!uhcPlayer.hasTeam()) return Messages.TEAM_NONE.toString();
				return TextUtils.color(uhcPlayer.getTeam().getDisplayName());
			}

			if (placeholder.equals("uhc-kills")) {
				return String.valueOf(uhcPlayer.getKills());
			}

			if (placeholder.equals("uhc-killstreak")) {
				return String.valueOf(uhcPlayer.getData().getKillstreak());
			}

			if (placeholder.equals("uhc-assists")) {
				return String.valueOf(uhcPlayer.getAssists());
			}

			if (placeholder.equals("uhc-level")) {
				return String.valueOf(uhcPlayer.getData().getUHCLevel());
			}

			if (placeholder.equals("uhc-exp")) {
				return String.valueOf(uhcPlayer.getData().getUHCExp());
			}

			if (placeholder.equals("uhc-required-exp")) {
				return String.valueOf(uhcPlayer.getData().getRequiredUHCExp());
			}

			if (placeholder.equals("uhc-ping")) {
				return String.valueOf(uhcPlayer.getPing());
			}

			if (placeholder.equals("uhc-money")) {
				return String.valueOf(uhcPlayer.getData().getMoney());
			}

			if (placeholder.equals("uhc-total-kills")) {
				return String.valueOf(uhcPlayer.getData().getKills());
			}

			if (placeholder.equals("uhc-total-deaths")) {
				return String.valueOf(uhcPlayer.getData().getDeaths());
			}

			if (placeholder.equals("uhc-total-wins")) {
				return String.valueOf(uhcPlayer.getData().getWins());
			}

			if (placeholder.equals("uhc-total-losses")) {
				return String.valueOf(uhcPlayer.getData().getLosses());
			}

			if (placeholder.equals("uhc-games-played")) {
				return String.valueOf(uhcPlayer.getData().getGamesPlayed());
			}
		}

		if (placeholder.equals("uhc-author")) {
			return getAuthor();
		}

		if (placeholder.equals("uhc-version")) {
			return getVersion();
		}

		if (placeholder.equals("uhc-prefix")) {
			return Messages.PREFIX.toString();
		}

		if (placeholder.equals("uhc-currency")) {
			return Messages.CURRENCY.toString();
		}

		if (placeholder.equals("uhc-world")) {
			return GameValues.WORLD_NAME;
		}

		if (placeholder.equals("uhc-border-size")) {
			final DecimalFormat format = new DecimalFormat("#######0");
			return "+" + format.format(gameManager.getBorderManager().getSize()) +
					" -" + format.format(gameManager.getBorderManager().getSize());
		}

		if (placeholder.equals("uhc-countdown")) {
			return TimeUtils.getFormattedTime(gameManager.getCurrentCountdown());
		}

		if (placeholder.equals("uhc-players")) {
			return String.valueOf(gameManager.getPlayerManager().getOnlinePlayers().size());
		}

		if (placeholder.equals("uhc-max-players")) {
			return String.valueOf(gameManager.getPlayerManager().getMaxPlayers());
		}

		if (placeholder.equals("uhc-players-to-start")) {
			return String.valueOf(GameValues.GAME.PLAYERS_TO_START);
		}

		if (placeholder.equals("uhc-alive-players")) {
			return String.valueOf(gameManager.getPlayerManager().getAlivePlayers().size());
		}

		if (placeholder.equals("uhc-dead-players")) {
			return String.valueOf(gameManager.getPlayerManager().getDeadPlayers().size());
		}

		if (placeholder.equals("uhc-spectators")) {
			return String.valueOf(gameManager.getPlayerManager().getSpectatorPlayers().size());
		}

		if (placeholder.equals("uhc-winner")) {
			return gameManager.getPlayerManager().getUHCWinner();
		}

		if (placeholder.equals("uhc-mode")) {
			if (GameValues.TEAM.TEAM_MODE) return Messages.GAME_TEAMS.toString();
			return Messages.GAME_SOLO.toString();
		}

		return null;
	}
}
