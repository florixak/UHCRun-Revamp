package me.florixak.uhcrun.utils.placeholderapi;

import me.florixak.uhcrun.UHCRun;
import me.clip.placeholderapi.PlaceholderAPI;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.manager.*;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.task.DeathMCd;
import me.florixak.uhcrun.task.FightingCd;
import me.florixak.uhcrun.task.MiningCd;
import me.florixak.uhcrun.task.StartingCd;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlaceholderUtil {

    public static boolean PAPI;

    public static String setPlaceholders(String text, Player p) {

        UHCRun plugin = UHCRun.getInstance();
        BorderManager borderManager = plugin.getBorderManager();
        StatisticsManager statisticManager = plugin.getStatistics();
        LevelManager levelManager = plugin.getLevelManager();

        UHCPlayer uhcPlayer = plugin.getPlayerManager().getUHCPlayer(p.getUniqueId());

        FileConfiguration config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        FileConfiguration kits = plugin.getConfigManager().getFile(ConfigType.KITS).getConfig();
        FileConfiguration perks = plugin.getConfigManager().getFile(ConfigType.PERKS).getConfig();

        if (text.contains("%player%") && uhcPlayer != null)
            text = text.replace("%player%", uhcPlayer.getName());

        /*if (text.contains("%ping%"))
            text = text.replace("%ping%", "" + player.getPing() + " ms");*/

        if (text.contains("%online%"))
            text = text.replace("%online%", String.valueOf(plugin.getPlayerManager().getPlayersList().size()));

        if (text.contains("%max-online%"))
            text = text.replace("%max-online%", String.valueOf(Bukkit.getServer().getMaxPlayers()));

        if (text.contains("%min-online%"))
            text = text.replace("%min_online%", String.valueOf(config.getInt("min-players")));

        if (text.contains("%money%")) {
            String formatted = Utils.formatMoney(uhcPlayer.getUUID());
            text = text.replace("%money%", String.valueOf(formatted));
        }

        if (text.contains("%kills%")) {
            text = text.replace("%kills%", String.valueOf(uhcPlayer.getKills()));
        }

        if (text.contains("%time%")) {
            if (plugin.getGame().gameState == GameState.STARTING)
                text = text.replace("%time%", TimeUtils.getFormattedTime(StartingCd.count));
            if (plugin.getGame().gameState == GameState.MINING)
                text = text.replace("%time%", TimeUtils.getFormattedTime(MiningCd.count));
            if (plugin.getGame().gameState == GameState.FIGHTING)
                text = text.replace("%time%", TimeUtils.getFormattedTime(FightingCd.count));
            if (plugin.getGame().gameState == GameState.DEATHMATCH)
                text = text.replace("%time%", TimeUtils.getFormattedTime(DeathMCd.count));
        }

        if (text.contains("%border%")) {
            DecimalFormat format = new DecimalFormat("#######0");
            text = text.replace("%border%", "+" + format.format(borderManager.getSize()) + " -" + format.format(borderManager.getSize()));
        }

        if (text.contains("%alive%")) text = text.replace("%alive%", String.valueOf(plugin.getPlayerManager().getAliveList().size()));

        if (text.contains("%kit%")) {
            if (config.getBoolean("lobby-items.kits.enabled", true)) {
                text = text.replace("%kit%", String.valueOf(uhcPlayer.getKit()));
            }
            else {
                text = text.replace("%perk%", "DISABLED");
            }
        }

        if (text.contains("%perk%")) {
            if (config.getBoolean("lobby-items.perks.enabled", true)) {
                text = text.replace("%perk%", String.valueOf(uhcPlayer.getPerk()));
            }
            else {
                text = text.replace("%perk%", "DISABLED");
            }
        }



        // TODO were alive
        // if (text.contains("%were-alive%")) text = text.replace("%were-alive%", "" + plugin.getGame().getWereAlive());

        // TODO kits
        if (config.getBoolean("lobby-items.kits.enabled", true)) {
        }

        // TODO Perks
        if (config.getBoolean("lobby-items.perks.enabled")) {
        }

        if (text.contains("%stats-wins%")) {
            text = text.replace("%stats-wins%", String.valueOf(statisticManager.getWins(uhcPlayer)));
        }
        if (text.contains("%stats-kills%")) {
            text = text.replace("%stats-kills%", String.valueOf(statisticManager.getKills(uhcPlayer)));
        }
        if (text.contains("%stats-deaths%")) {
            text = text.replace("%stats-deaths%", String.valueOf(statisticManager.getDeaths(uhcPlayer)));
        }
        if (text.contains("%stats-level%")) {
            text = text.replace("%stats-level%", String.valueOf(levelManager.getPlayerLevel(uhcPlayer)));
        }
        if (text.contains("%requiredXP%")) {
            DecimalFormat format = new DecimalFormat("#######0");
            text = text.replace("%requiredXP%", format.format(levelManager.getRequiredExp(uhcPlayer)));
        }

        if (text.contains("%winner%")) {
            text = text.replace("%winner%", plugin.getGame().getWinnerName());
        }

        if (text.contains("%team%")) text = text.replace("%team%", uhcPlayer.getTeam());

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