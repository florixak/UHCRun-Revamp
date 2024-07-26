package me.florixak.uhcrevamp.utils.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.manager.BorderManager;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlaceholderUtil {

    public static boolean PAPI;

    public static String setPlaceholders(String text, Player p) {

        GameManager gameManager = GameManager.getGameManager();
        BorderManager borderManager = gameManager.getBorderManager();

        if (p != null) {
            UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

            if (text.contains("%player%"))
                text = text.replace("%player%", uhcPlayer.getName());

            if (text.contains("%uhc-level%"))
                text = text.replace("%uhc-level%", String.valueOf(uhcPlayer.getData().getUHCLevel()));

            if (text.contains("%uhc-exp%"))
                text = text.replace("%uhc-exp%", String.valueOf(uhcPlayer.getData().getUHCExp()));

            if (text.contains("%required-uhc-exp%")) {
                DecimalFormat format = new DecimalFormat("#######0");
                text = text.replace("%required-uhc-exp%", format.format(uhcPlayer.getData().getRequiredUHCExp()));
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

            if (text.contains("%ping%")) {
                try {
                    text = text.replace("%ping%", String.valueOf(p.getPing()));
                } catch (Exception e) {
                    text = text.replace("%ping%", "0");
                }
            }

            if (text.contains("%kills%")) {
                text = text.replace("%kills%", String.valueOf(uhcPlayer.getKills()));
            }

            if (text.contains("%assists%")) {
                text = text.replace("%assists%", String.valueOf(uhcPlayer.getAssists()));
            }

            if (text.contains("%kit%")) {
                if (GameValues.KITS.ENABLED) {
                    if (uhcPlayer.hasKit()) {
                        text = text.replace("%kit%", uhcPlayer.getKit().getDisplayName());
                    } else {
                        text = text.replace("%kit%", Messages.KITS_SELECTED_NONE.toString());
                    }
                } else {
                    text = text.replace("%kit%", Messages.KITS_SB_DISABLED.toString());
                }
            }

            if (text.contains("%perk%")) {
                if (GameValues.PERKS.ENABLED) {
                    if (uhcPlayer.hasPerk()) {
                        text = text.replace("%perk%", uhcPlayer.getPerk().getName());
                    } else {
                        text = text.replace("%perk%", Messages.PERKS_SELECTED_NONE.toString());
                    }
                } else {
                    text = text.replace("%perk%", Messages.PERKS_SB_DISABLED.toString());
                }
            }

            if (text.contains("%team%")) {
                if (!GameValues.TEAM.TEAM_MODE) {
                    text = text.replace("%team%", Messages.TEAM_SOLO.toString());
                } else {
                    if (uhcPlayer.hasTeam()) {
                        text = text.replace("%team%", TextUtils.color(uhcPlayer.getTeam().getDisplayName()));
                    } else {
                        text = text.replace("%team%", Messages.TEAM_NONE.toString());
                    }
                }
            }

            if (text.contains("%money-for-game%")) {
                text = text.replace("%money-for-game%", String.valueOf(uhcPlayer.getData().getMoneyForGameResult()));
            }
            if (text.contains("%money-for-kills%")) {
                text = text.replace("%money-for-kills%", String.valueOf(uhcPlayer.getData().getMoneyForKills()));
            }
            if (text.contains("%money-for-assists%")) {
                text = text.replace("%money-for-assists%", String.valueOf(uhcPlayer.getData().getMoneyForAssists()));
            }
            if (text.contains("%money-for-activity%")) {
                text = text.replace("%money-for-activity%", String.valueOf(uhcPlayer.getData().getMoneyForActivity()));
            }
            if (text.contains("%uhc-exp-for-game%")) {
                text = text.replace("%uhc-exp-for-game%", String.valueOf(uhcPlayer.getData().getUhcExpForGameResult()));
            }
            if (text.contains("%uhc-exp-for-kills%")) {
                text = text.replace("%uhc-exp-for-kills%", String.valueOf(uhcPlayer.getData().getUhcExpForKills()));
            }
            if (text.contains("%uhc-exp-for-assists%")) {
                text = text.replace("%uhc-exp-for-assists%", String.valueOf(uhcPlayer.getData().getUhcExpForAssists()));
            }
            if (text.contains("%uhc-exp-for-activity%")) {
                text = text.replace("%uhc-exp-for-activity%", String.valueOf(uhcPlayer.getData().getUhcExpForActivity()));
            }
        }

        if (text.contains("%online%"))
            text = text.replace("%online%", String.valueOf(gameManager.getPlayerManager().getOnlineList().size()));

        if (text.contains("%max-online%"))
            text = text.replace("%max-online%", String.valueOf(gameManager.getPlayerManager().getMaxPlayers()));

        if (text.contains("%min-online%"))
            text = text.replace("%min_online%", String.valueOf(GameValues.GAME.MIN_PLAYERS));

        if (text.contains("%currency%")) {
            text = text.replace("%currency%", Messages.CURRENCY.toString());
        }

        if (text.contains("%countdown%")) {
            text = text.replace("%countdown%", TimeUtils.getFormattedTime(gameManager.getCurrentCountdown()));
        }

        if (text.contains("%border%")) {
            DecimalFormat format = new DecimalFormat("#######0");
            text = text.replace("%border%", "+" + format.format(borderManager.getSize()) + " -" + format.format(borderManager.getSize()));
        }

        if (text.contains("%alive%")) {
            text = text.replace("%alive%", String.valueOf(gameManager.getPlayerManager().getAliveList().size()));
        }

        if (text.contains("%dead%")) {
            text = text.replace("%dead%", String.valueOf(gameManager.getPlayerManager().getDeadList().size()));
        }

        if (text.contains("%spectators%")) {
            text = text.replace("%spectators%", String.valueOf(gameManager.getPlayerManager().getSpectatorList().size()));
        }

        // TODO were alive
        // if (text.contains("%were-alive%")) text = text.replace("%were-alive%", "" + plugin.getGame().getWereAlive());


        if (text.contains("%winner%")) {
            text = text.replace("%winner%", gameManager.getPlayerManager().getUHCWinner());
        }

        if (text.contains("%sb-footer%")) {
            text = text.replace("%sb-footer%", TextUtils.color(gameManager.getScoreboardManager().getFooter()));
        }

        if (text.contains("%game-mode%")) {
            if (!GameValues.TEAM.TEAM_MODE) {
                text = text.replace("%game-mode%", Messages.GAME_SOLO.toString());
            } else {
                text = text.replace("%game-mode%", Messages.GAME_TEAMS.toString());
            }
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

        if (PAPI && p != null) text = PlaceholderAPI.setPlaceholders(p, text);

        return text;

    }
}