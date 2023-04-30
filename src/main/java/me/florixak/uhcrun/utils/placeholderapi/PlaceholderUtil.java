package me.florixak.uhcrun.utils.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.*;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.tasks.DeathMCd;
import me.florixak.uhcrun.tasks.FightingCd;
import me.florixak.uhcrun.tasks.MiningCd;
import me.florixak.uhcrun.tasks.StartingCd;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
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

        /*if (text.contains("%ping%"))
            text = text.replace("%ping%", "" + player.getPing() + " ms");*/

        if (text.contains("%online%"))
            text = text.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));

        if (text.contains("%max-online%"))
            text = text.replace("%max-online%", String.valueOf(Bukkit.getServer().getMaxPlayers()));

        if (text.contains("%min-online%"))
            text = text.replace("%min_online%", String.valueOf(config.getInt("min-players")));

//        if (text.contains("%money%")) {
//            String formatted = Utils.formatMoney(uhcPlayer.getUUID());
//            text = text.replace("%money%", String.valueOf(formatted));
//        }

        if (text.contains("%kills%")) {
            text = text.replace("%kills%", String.valueOf(uhcPlayer.getKills()));
        }

        if (text.contains("%time%")) {
            if (gameManager.getGameState() == GameState.STARTING)
                text = text.replace("%time%", TimeUtils.getFormattedTime(StartingCd.count));
            if (gameManager.getGameState() == GameState.MINING)
                text = text.replace("%time%", TimeUtils.getFormattedTime(MiningCd.count));
            if (gameManager.getGameState() == GameState.FIGHTING)
                text = text.replace("%time%", TimeUtils.getFormattedTime(FightingCd.count));
            if (gameManager.getGameState() == GameState.DEATHMATCH)
                text = text.replace("%time%", TimeUtils.getFormattedTime(DeathMCd.count));
        }

        if (text.contains("%border%")) {
            DecimalFormat format = new DecimalFormat("#######0");
            text = text.replace("%border%", "+" + format.format(borderManager.getSize()) + " -" + format.format(borderManager.getSize()));
        }

        if (text.contains("%alive%")) {
            text = text.replace("%alive%", String.valueOf(gameManager.getPlayerManager().getAlivePlayers().size()));
        }

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
            text = text.replace("%stats-wins%", String.valueOf(uhcPlayer.getData().getWins()));
        }
        if (text.contains("%stats-kills%")) {
            text = text.replace("%stats-kills%", String.valueOf(uhcPlayer.getData().getKills()));
        }
        if (text.contains("%stats-deaths%")) {
            text = text.replace("%stats-deaths%", String.valueOf(uhcPlayer.getData().getDeaths()));
        }
        if (text.contains("%stats-level%")) {
            text = text.replace("%stats-level%", String.valueOf(uhcPlayer.getData().getLevel()));
        }
        if (text.contains("%requiredXP%")) {
            DecimalFormat format = new DecimalFormat("#######0");
            text = text.replace("%requiredXP%", format.format(uhcPlayer.getData().getRequiredExp()));
        }

        if (text.contains("%winner%")) {
            text = text.replace("%winner%", gameManager.getPlayerManager().getUHCWinner().getName());
        }

        if (text.contains("%team%")) text = text.replace("%team%", uhcPlayer.getTeam().getName());

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