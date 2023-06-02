package me.florixak.uhcrun.utils.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.*;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.tasks.DeathmatchCD;
import me.florixak.uhcrun.tasks.FightingCD;
import me.florixak.uhcrun.tasks.MiningCD;
import me.florixak.uhcrun.tasks.StartingCD;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlaceholderUtil {

    public static boolean PAPI;

    public static String setPlaceholders(String text, Player p) {

        GameManager gameManager = GameManager.getGameManager();
        BorderManager borderManager = gameManager.getBorderManager();

        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

        if (text.contains("%player%") && uhcPlayer != null)
            text = text.replace("%player%", uhcPlayer.getName());

        if (text.contains("%ping%"))
            text = text.replace("%ping%", p.getPing() + " ms");

        if (text.contains("%online%"))
            text = text.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));

        if (text.contains("%max-online%"))
            if (!gameManager.isTeamMode()) {
                text = text.replace("%max-online%", String.valueOf(Bukkit.getServer().getMaxPlayers()));
            } else {
                text = text.replace("%max-online%", String.valueOf(
                        gameManager.getTeamManager().getTeams().size()*config.getInt("settings.teams.max-size"))
                );
            }


        if (text.contains("%min-online%"))
            text = text.replace("%min_online%", String.valueOf(config.getInt("min-players")));

//        if (text.contains("%money%")) {
//            String formatted = Utils.formatMoney(uhcPlayer.getUUID());
//            text = text.replace("%money%", String.valueOf(formatted));
//        }

        if (text.contains("%kills%")) {
            text = text.replace("%kills%", String.valueOf(uhcPlayer.getKills()));
        }

        if (text.contains("%assists%")) {
            text = text.replace("%kills%", String.valueOf(uhcPlayer.getAssists()));
        }

        if (text.contains("%time%")) {
            if (gameManager.getGameState() == GameState.STARTING)
                text = text.replace("%time%", TimeUtils.getFormattedTime(StartingCD.countdown));
            if (gameManager.getGameState() == GameState.MINING)
                text = text.replace("%time%", TimeUtils.getFormattedTime(MiningCD.countdown));
            if (gameManager.getGameState() == GameState.FIGHTING)
                text = text.replace("%time%", TimeUtils.getFormattedTime(FightingCD.countdown));
            if (gameManager.getGameState() == GameState.DEATHMATCH)
                text = text.replace("%time%", TimeUtils.getFormattedTime(DeathmatchCD.countdown));
        }

        if (text.contains("%border%")) {
            DecimalFormat format = new DecimalFormat("#######0");
            text = text.replace("%border%", "+" + format.format(borderManager.getSize()) + " -" + format.format(borderManager.getSize()));
        }

        if (text.contains("%alive%")) {
            text = text.replace("%alive%", String.valueOf(gameManager.getPlayerManager().getAlivePlayers().size()));
        }

        if (text.contains("%dead%")) {
            text = text.replace("%dead%", String.valueOf(gameManager.getPlayerManager().getDeadPlayers().size()));
        }

        if (text.contains("%spectators%")) {
            text = text.replace("%spectators%", String.valueOf(gameManager.getPlayerManager().getSpectators().size()));
        }

        if (text.contains("%kit%")) {
            if (config.getBoolean("settings.kits.enabled")) {
                if (uhcPlayer.hasKit()) {
                    text = text.replace("%kit%", uhcPlayer.getKit().getName());
                } else {
                    text = text.replace("%kit%", "None");
                }
            } else {
                text = text.replace("%kit%", "Disabled");
            }
        }

        if (text.contains("%perk%")) {
            if (config.getBoolean("settings.perks.enabled")) {
                if (uhcPlayer.hasPerk()) {
                    text = text.replace("%perk%", uhcPlayer.getPerk().getName());
                } else {
                    text = text.replace("%perk%", "None");
                }
            } else {
                text = text.replace("%perk%", "Disabled");
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
            if (!gameManager.isTeamMode()) {
                text = text.replace("%team%", "Solo");
            } else {
                if (uhcPlayer.hasTeam()) {
                    text = text.replace("%team%", TextUtils.color(uhcPlayer.getTeam().getName()));
                } else {
                    text = text.replace("%team%", "None");
                }
            }
        }

        if (text.contains("%game-mode%")) {
            if (!gameManager.isTeamMode()) {
                text = text.replace("%team%", "SOLO");
            } else {
                text = text.replace("%team%", "TEAM");
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

    public static void replace(UHCPlayer player, String text, String late, String now) {
        if (text.contains(late)) {
            text.replace(late, now);
        }
    }
}