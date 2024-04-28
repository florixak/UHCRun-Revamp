package me.florixak.uhcrun.utils.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.*;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.TimeUtils;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlaceholderUtil {

    public static boolean PAPI;

    public static String setPlaceholders(String text, Player p) {

        GameManager gameManager = GameManager.getGameManager();
        BorderManager borderManager = gameManager.getBorderManager();

        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

        if (uhcPlayer == null) return "Error";

        if (text.contains("%player%"))
            text = text.replace("%player%", uhcPlayer.getName());

        if (text.contains("%ping%"))
            text = text.replace("%ping%", String.valueOf(p.getPing()));

        if (text.contains("%online%"))
            text = text.replace("%online%", String.valueOf(gameManager.getPlayerManager().getOnlineList().size()));

        if (text.contains("%max-online%"))
            text = text.replace("%max-online%", String.valueOf(gameManager.getPlayerManager().getMaxPlayers()));

        if (text.contains("%min-online%"))
            text = text.replace("%min_online%", String.valueOf(GameValues.MIN_PLAYERS));

        if (text.contains("%money%")) {
            text = text.replace("%money%", String.valueOf(uhcPlayer.getData().getMoney()));
        }

        if (text.contains("%kills%")) {
            text = text.replace("%kills%", String.valueOf(uhcPlayer.getKills()));
        }

        if (text.contains("%assists%")) {
            text = text.replace("%assists%", String.valueOf(uhcPlayer.getAssists()));
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

        if (text.contains("%kit%")) {
            if (GameValues.KITS_ENABLED) {
                if (uhcPlayer.hasKit()) {
                    text = text.replace("%kit%", uhcPlayer.getKit().getName());
                } else {
                    text = text.replace("%kit%", Messages.KITS_SB_SELECTED_NONE.toString());
                }
            } else {
                text = text.replace("%kit%", Messages.KITS_SB_DISABLED.toString());
            }
        }

        if (text.contains("%perk%")) {
            if (GameValues.PERKS_ENABLED) {
                if (uhcPlayer.hasPerk()) {
                    text = text.replace("%perk%", uhcPlayer.getPerk().getName());
                } else {
                    text = text.replace("%perk%", Messages.PERKS_SB_SELECTED_NONE.toString());
                }
            } else {
                text = text.replace("%perk%", Messages.PERKS_SB_DISABLED.toString());
            }
        }

        // TODO were alive
        // if (text.contains("%were-alive%")) text = text.replace("%were-alive%", "" + plugin.getGame().getWereAlive());

        if (text.contains("%stats-wins%")) {
            text = text.replace("%stats-wins%", String.valueOf(uhcPlayer.getData().getWins()));
        }
        if (text.contains("%stats-kills%")) {
            text = text.replace("%stats-kills%", String.valueOf(uhcPlayer.getData().getKills()));
        }
        if (text.contains("%stats-deaths%")) {
            text = text.replace("%stats-deaths%", String.valueOf(uhcPlayer.getData().getDeaths()));
        }
        if (text.contains("%stats-level%")) {
            text = text.replace("%stats-level%", String.valueOf(uhcPlayer.getData().getUHCLevel()));
        }
        if (text.contains("%requiredXP%")) {
            DecimalFormat format = new DecimalFormat("#######0");
            text = text.replace("%requiredXP%", format.format(uhcPlayer.getData().getRequiredUHCExp()));
        }

        if (text.contains("%winner%")) {
            text = text.replace("%winner%", gameManager.getUHCWinner());
        }

        if (text.contains("%team%")) {
            if (!GameValues.TEAM_MODE) {
                text = text.replace("%team%", Messages.TEAM_SOLO.toString());
            } else {
                if (uhcPlayer.hasTeam()) {
                    text = text.replace("%team%", TextUtils.color(uhcPlayer.getTeam().getName()));
                } else {
                    text = text.replace("%team%", Messages.TEAM_NONE.toString());
                }
            }
        }

        if (text.contains("%game-mode%")) {
            if (!GameValues.TEAM_MODE) {
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

        if (PAPI && uhcPlayer != null) text = PlaceholderAPI.setPlaceholders(uhcPlayer.getPlayer(), text);

        return text;

    }
}