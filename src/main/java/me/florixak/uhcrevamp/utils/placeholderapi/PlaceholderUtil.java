package me.florixak.uhcrevamp.utils.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.manager.BorderManager;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.entity.Player;

public class PlaceholderUtil {

	public static String setPlaceholders(String text, final Player p) {

		final UHCRevamp plugin = UHCRevamp.getInstance();
		final GameManager gameManager = GameManager.getGameManager();
		final BorderManager borderManager = gameManager.getBorderManager();

		if (p != null) {
			final UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

			if (text.contains("%player%"))
				text = text.replace("%player%", uhcPlayer.getName());

			if (text.contains("%ping%")) {
				text = text.replace("%ping%", String.valueOf(uhcPlayer.getPing()));
			}

			if (text.contains("%uhc-level%"))
				text = text.replace("%uhc-level%", String.valueOf(uhcPlayer.getData().getUHCLevel()));

			if (text.contains("%uhc-exp%"))
				text = text.replace("%uhc-exp%", String.valueOf(uhcPlayer.getData().getUHCExp()));

			if (text.contains("%required-uhc-exp%")) {
				text = text.replace("%required-uhc-exp%", TextUtils.formatToOneDecimal(uhcPlayer.getData().getRequiredUHCExp()));
			}

			if (text.contains("%money%")) {
				text = text.replace("%money%", String.valueOf(uhcPlayer.getData().getMoney()));
			}

			if (text.contains("%uhc-wins%")) {
				text = text.replace("%uhc-wins%", String.valueOf(uhcPlayer.getData().getWins()));
			}

			if (text.contains("%uhc-losses%")) {
				text = text.replace("%uhc-losses%", String.valueOf(uhcPlayer.getData().getLosses()));
			}

			if (text.contains("%uhc-kills%")) {
				text = text.replace("%uhc-kills%", String.valueOf(uhcPlayer.getData().getKills()));
			}

			if (text.contains("%uhc-assists%")) {
				text = text.replace("%uhc-assists%", String.valueOf(uhcPlayer.getData().getAssists()));
			}

			if (text.contains("%uhc-deaths%")) {
				text = text.replace("%uhc-deaths%", String.valueOf(uhcPlayer.getData().getDeaths()));
			}

			if (text.contains("%uhc-games-played%")) {
				text = text.replace("%uhc-games-played%", String.valueOf(uhcPlayer.getData().getGamesPlayed()));
			}

			if (text.contains("%uhc-killstreak%")) {
				text = text.replace("%uhc-killstreak%", String.valueOf(uhcPlayer.getData().getKillstreak()));
			}

			if (text.contains("%ping%")) {
				try {
					text = text.replace("%ping%", String.valueOf(p.getPing()));
				} catch (final Exception e) {
					text = text.replace("%ping%", "0");
				}
			}

			if (text.contains("%kills%")) {
				text = text.replace("%kills%", String.valueOf(uhcPlayer.getKills()));
			}

			if (text.contains("%assists%")) {
				text = text.replace("%assists%", String.valueOf(uhcPlayer.getAssists()));
			}

//            if (text.contains("%time-played%")) {
//                text = text.replace("%time-played%", TimeUtils.getFormattedTime(uhcPlayer.getTimePlayed()));
//            }
//
//            if (text.contains("%full-time-played%")) {
//                text = text.replace("%time-played%", TimeUtils.getFormattedTime(uhcPlayer.getData().getTimePlayed()));
//            }

			if (text.contains("%kit%")) {
				if (GameValues.KITS.ENABLED) {
					text = text.replace("%kit%", uhcPlayer.hasKit() ? uhcPlayer.getKit().getDisplayName() : Messages.KITS_SELECTED_NONE.toString());
				} else {
					text = text.replace("%kit%", Messages.KITS_SB_DISABLED.toString());
				}
			}

			if (text.contains("%perk%")) {
				if (GameValues.PERKS.ENABLED) {
					text = text.replace("%perk%", uhcPlayer.hasPerk() ? uhcPlayer.getPerk().getName() : Messages.PERKS_SELECTED_NONE.toString());
				} else {
					text = text.replace("%perk%", Messages.PERKS_SB_DISABLED.toString());
				}
			}

			if (text.contains("%team%")) {
				if (!GameValues.TEAM.TEAM_MODE) {
					text = text.replace("%team%", Messages.TEAM_SOLO.toString());
				} else {
					text = text.replace("%team%", uhcPlayer.hasTeam() ? TextUtils.color(uhcPlayer.getTeam().getDisplayName()) : Messages.TEAM_NONE.toString());
				}
			}

			if (text.contains("%hp%") || text.contains("%health%")) {
				text = text.replace("%hp%", String.valueOf(p.getHealth()));
				text = text.replace("%health%", String.valueOf(p.getHealth()));
			}

			if (text.contains("%luckperms-prefix%")) {
				text = text.replace("%luckperms-prefix%", plugin.getLuckPermsHook().getPrefix(p));
			}

			if (text.contains("%luckperms-suffix%")) {
				text = text.replace("%luckperms-suffix%", plugin.getLuckPermsHook().getSuffix(p));
			}

			if (text.contains("%money-for-game%")) {
				text = text.replace("%money-for-game%", String.valueOf(uhcPlayer.getMoneyForGameResult()));
			}
			if (text.contains("%money-for-kills%")) {
				text = text.replace("%money-for-kills%", String.valueOf(uhcPlayer.getMoneyForKills()));
			}
			if (text.contains("%money-for-assists%")) {
				text = text.replace("%money-for-assists%", String.valueOf(uhcPlayer.getMoneyForAssists()));
			}
			if (text.contains("%money-for-activity%")) {
				text = text.replace("%money-for-activity%", String.valueOf(uhcPlayer.getMoneyForActivity()));
			}
			if (text.contains("%uhc-exp-for-game%")) {
				text = text.replace("%uhc-exp-for-game%", String.valueOf(uhcPlayer.getUHCExpForGameResult()));
			}
			if (text.contains("%uhc-exp-for-kills%")) {
				text = text.replace("%uhc-exp-for-kills%", String.valueOf(uhcPlayer.getUHCExpForKills()));
			}
			if (text.contains("%uhc-exp-for-assists%")) {
				text = text.replace("%uhc-exp-for-assists%", String.valueOf(uhcPlayer.getUHCExpForAssists()));
			}
			if (text.contains("%uhc-exp-for-activity%")) {
				text = text.replace("%uhc-exp-for-activity%", String.valueOf(uhcPlayer.getUHCExpForActivity()));
			}
		}

		if (text.contains("%online%"))
			text = text.replace("%online%", String.valueOf(gameManager.getPlayerManager().getOnlinePlayers().size()));

		if (text.contains("%max-online%"))
			text = text.replace("%max-online%", String.valueOf(gameManager.getPlayerManager().getMaxPlayers()));

		if (text.contains("%players-to-start%"))
			text = text.replace("%players-to-start%", String.valueOf(GameValues.GAME.PLAYERS_TO_START));

		if (text.contains("%currency%")) {
			text = text.replace("%currency%", Messages.CURRENCY.toString());
		}

		if (text.contains("%countdown%")) {
			text = text.replace("%countdown%", TimeUtils.getFormattedTime(gameManager.getCurrentCountdown()));
		}

		if (text.contains("%border%")) {
			text = text.replace("%border%", "+" + TextUtils.formatToZeroDecimal(borderManager.getSize()) + " -" + TextUtils.formatToZeroDecimal(borderManager.getSize()));
		}

		if (text.contains("%alive%")) {
			text = text.replace("%alive%", String.valueOf(gameManager.getPlayerManager().getAlivePlayers().size()));
		}

		if (text.contains("%dead%")) {
			text = text.replace("%dead%", String.valueOf(gameManager.getPlayerManager().getDeadPlayers().size()));
		}

		if (text.contains("%spectators%")) {
			text = text.replace("%spectators%", String.valueOf(gameManager.getPlayerManager().getSpectatorPlayers().size()));
		}

		// TODO were alive
		// if (text.contains("%were-alive%")) text = text.replace("%were-alive%", "" + plugin.getGame().getWereAlive());


		if (text.contains("%winner%")) {
			text = text.replace("%winner%", gameManager.getPlayerManager().getUHCWinner());
		}

		if (text.contains("%scoreboard-footer%")) {
			text = text.replace("%scoreboard-footer%", TextUtils.color(gameManager.getScoreboardManager().getFooter()));
		}

		if (text.contains("%game-mode%")) {
			text = text.replace("%game-mode%", GameValues.TEAM.TEAM_MODE ? Messages.GAME_TEAMS.toString() : Messages.GAME_SOLO.toString());
		}

        /*try {
            final String BUNGEEPATTERN = "%bungeecord(\w+)%";
            Pattern pattern = Pattern.compile(BUNGEE_PATTERN);
            Matcher matcher = pattern.matcher(text);
            while(matcher.find()) {
                text = matcher.replaceAll(String.valueOf(BungeeCord.getServerCount(player, matcher.group(1))));
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }*/
		if (UHCRevamp.getInstance().getPapiHook().hasPlaceholderAPI()
				&& GameValues.ADDONS.CAN_USE_PLACEHOLDERAPI
				&& p != null)
			return PlaceholderAPI.setPlaceholders(p, text);
		return text;
	}
}