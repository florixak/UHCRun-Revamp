package me.florixak.uhcrun.utils.placeholderapi;

import me.florixak.uhcrun.UHCRun;
import me.clip.placeholderapi.PlaceholderAPI;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.kits.Kits;
import me.florixak.uhcrun.kits.KitsManager;
import me.florixak.uhcrun.manager.*;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.perks.PerksManager;
import me.florixak.uhcrun.task.DeathMCd;
import me.florixak.uhcrun.task.FightingCd;
import me.florixak.uhcrun.task.MiningCd;
import me.florixak.uhcrun.task.StartingCd;
import me.florixak.uhcrun.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlaceholderUtil {

    public static boolean PAPI;

    public static String setPlaceholders(String text, Player player) {

        UHCRun plugin = UHCRun.plugin;
        BorderManager borderManager = plugin.getBorderManager();
        StatisticsManager statisticManager = plugin.getStatisticManager();
        LevelManager levelManager = plugin.getLevelManager();


        FileConfiguration config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        FileConfiguration kits = plugin.getConfigManager().getFile(ConfigType.KITS).getConfig();
        FileConfiguration perks = plugin.getConfigManager().getFile(ConfigType.PERKS).getConfig();

        if (text.contains("%player%") && player != null)
            text = text.replace("%player%", player.getDisplayName());

        /*if (text.contains("%ping%"))
            text = text.replace("%ping%", "" + player.getPing() + " ms");*/

        if (text.contains("%online%"))
            text = text.replace("%online%", String.valueOf(PlayerManager.online.size()));

        if (text.contains("%max-online%"))
            text = text.replace("%max-online%", String.valueOf(Bukkit.getServer().getMaxPlayers()));

        if (text.contains("%min-online%"))
            text = text.replace("%min_online%", String.valueOf(config.getInt("min-players")));

        if (text.contains("%money%")) {
            DecimalFormat format = new DecimalFormat("#######0.0");
            String formatted = format.format(statisticManager.getMoney(player.getUniqueId()));
            text = text.replace("%money%", String.valueOf(formatted));
        }

        if (text.contains("%kills%")) {
            if (PlayerManager.kills.get(player.getUniqueId()) != null)
                text = text.replace("%kills%", String.valueOf(PlayerManager.kills.get(player.getUniqueId())));
            else text = text.replace("%kills%", "ERROR");
        }

        if (text.contains("%time%")) {
            if (UHCRun.plugin.getGame().gameState == GameState.STARTING)
                text = text.replace("%time%", TimeUtils.convertCountdown(StartingCd.count));
            if (UHCRun.plugin.getGame().gameState == GameState.MINING)
                text = text.replace("%time%", TimeUtils.convertCountdown(MiningCd.count));
            if (UHCRun.plugin.getGame().gameState == GameState.FIGHTING)
                text = text.replace("%time%", TimeUtils.convertCountdown(FightingCd.count));
            if (UHCRun.plugin.getGame().gameState == GameState.DEATHMATCH)
                text = text.replace("%time%", TimeUtils.convertCountdown(DeathMCd.count));
        }

        if (text.contains("%border%")) {
            DecimalFormat format = new DecimalFormat("#######0");
            text = text.replace("%border%", "+" + format.format(borderManager.getSize()) + " -" + format.format(borderManager.getSize()));
        }

        if (text.contains("%kit%")) {
            if (config.getBoolean("lobby-items.kits.enabled", true)) {
                text = text.replace("%kit%", String.valueOf(KitsManager.getKit(player.getUniqueId())));
            }
            else {
                text = text.replace("%perk%", "DISABLED");
            }
        }

        if (text.contains("%perk%")) {
            if (config.getBoolean("lobby-items.perks.enabled", true)) {
                text = text.replace("%perk%", String.valueOf(PerksManager.getPerk(player.getUniqueId())));
            }
            else {
                text = text.replace("%perk%", "DISABLED");
            }
        }

        if (text.contains("%alive%")) text = text.replace("%alive%", "" + PlayerManager.alive.size());

        if (text.contains("%were-alive%")) text = text.replace("%were-alive%", "" + plugin.getGame().getWereAlive());

        if (config.getBoolean("lobby-items.kits.enabled", true)) {
            if (text.contains("%kits-none%")) {
                if (KitsManager.getKit(player.getUniqueId()) == Kits.NONE) {
                    text = text.replace("%kits-none%", Messages.SELECTED_INV.toString());
                } else {
                    text = text.replace("%kits-none%", Messages.CLICK_SELECT_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.none.price")));
                }
            }
            /*
            if (text.contains("%kits-starter%")) {
                if (KitsManager.getKit(player.getUniqueId()) == Kits.STARTER) {
                    text = text.replace("%kits-starter%", Messages.SELECTED_INV.toString());
                } else if (plugin.getStatisticManager().haveStarter(player.getUniqueId()) == true
                        || kits.getDouble("items.starter.price") == 0) {
                    text = text.replace("%kits-starter%", Messages.CLICK_SELECT_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.starter.price")));
                } else {
                    text = text.replace("%kits-starter%", Messages.CLICK_BUY_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.starter.price")));
                }
            }
            if (text.contains("%kits-miner%")) {
                if (KitsManager.getKit(player.getUniqueId()) == Kits.MINER) {
                    text = text.replace("%kits-miner%", Messages.SELECTED_INV.toString());

                } else if (plugin.getStatisticManager().haveMiner(player.getUniqueId()) == true
                        || kits.getDouble("items.miner.price") == 0) {
                    text = text.replace("%kits-miner%", Messages.CLICK_SELECT_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.miner.price")));
                } else {
                    text = text.replace("%kits-miner%", Messages.CLICK_BUY_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.miner.price")));
                }
            }
            if (text.contains("%kits-enchanter%")) {
                if (KitsManager.getKit(player.getUniqueId()) == Kits.ENCHANTER) {
                    text = text.replace("%kits-enchanter%", Messages.SELECTED_INV.toString());
                } else if (plugin.getStatisticManager().haveEnchanter(player.getUniqueId()) == true
                        || kits.getDouble("items.enchanter.price") == 0) {
                    text = text.replace("%kits-enchanter%", Messages.CLICK_SELECT_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.enchanter.price")));
                } else {
                    text = text.replace("%kits-enchanter%", Messages.CLICK_BUY_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.enchanter.price")));
                }
            }
            if (text.contains("%kits-healer%")) {
                if (KitsManager.getKit(player.getUniqueId()) == Kits.HEALER) {
                    text = text.replace("%kits-healer%", Messages.SELECTED_INV.toString());
                } else if (plugin.getStatisticManager().haveHealer(player.getUniqueId()) == true
                        || kits.getDouble("items.healer.price") == 0) {
                    text = text.replace("%kits-healer%", Messages.CLICK_SELECT_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.healer.price")));
                } else {
                    text = text.replace("%kits-healer%", Messages.CLICK_BUY_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.healer.price")));
                }
            }
            if (text.contains("%kits-horse_rider%")) {
                if (KitsManager.getKit(player.getUniqueId()) == Kits.HORSE_RIDER) {
                    text = text.replace("%kits-horse_rider%", Messages.SELECTED_INV.toString());
                } else if (plugin.getStatisticManager().haveHorseRider(player.getUniqueId()) == true
                        || kits.getDouble("items.horse_rider.price") == 0) {
                    text = text.replace("%kits-horse_rider%", Messages.CLICK_SELECT_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.horse_rider.price")));
                } else {
                    text = text.replace("%kits-horse_rider%", Messages.CLICK_BUY_INV.toString()
                            .replace("%price%", "" + kits.getDouble("items.horse_rider.price")));
                }
            }*/
        }
        else {
            if (text.contains("%kits-none%")) text = text.replace("%kits-none%", Messages.DISABLED.toString());
            if (text.contains("%kits-starter%")) text = text.replace("%kits-starter%", Messages.DISABLED.toString());
            if (text.contains("%kits-miner%")) text = text.replace("%kits-miner%", Messages.DISABLED.toString());
            if (text.contains("%kits-enchanter%")) text = text.replace("%kits-enchanter%", Messages.DISABLED.toString());
            if (text.contains("%kits-healer%")) text = text.replace("%kits-healer%", Messages.DISABLED.toString());
            if (text.contains("%kits-horse_rider%")) text = text.replace("%kits-horse_rider%", Messages.DISABLED.toString());
        }

        if (config.getBoolean("lobby-items.perks.enabled")) {
//            if (text.contains("%perks-none%")) {
//                if (PerksManager.haveNoPerk(player)) {
//                    text = text.replace("%perks-none%", Messages.SELECTED_INV.toString());
//                } else {
//                    text = text.replace("%perks-none%", Messages.CLICK_SELECT_INV.toString()
//                            .replace("%price%", "" + kits.getDouble("items.none.price")));
//                }
//            }
//            if (text.contains("%perks-strength%")) {
//                if (PerksManager.haveStrength(player)) {
//                    text = text.replace("%perks-strength%", Messages.SELECTED_INV.toString());
//                } else {
//                    text = text.replace("%perks-strength%", Messages.CLICK_SELECT_INV.toString()
//                            .replace("%price%", "" + kits.getDouble("items.strength.price")));
//                }
//            }
//            if (text.contains("%perks-speed%")) {
//                if (PerksManager.haveSpeed(player)) {
//                    text = text.replace("%perks-speed%", Messages.SELECTED_INV.toString());
//                } else {
//                    text = text.replace("%perks-speed%", Messages.CLICK_SELECT_INV.toString()
//                            .replace("%price%", "" + kits.getDouble("items.speed.price")));
//                }
//            }
        }
        else {
            if (text.contains("%perks-none%")) text = text.replace("%perks-none%", Messages.DISABLED.toString());
            if (text.contains("%perks-strength%")) text = text.replace("%perks-strength%", Messages.DISABLED.toString());
            if (text.contains("%perks-regeneration%")) text = text.replace("%perks-regeneration%", Messages.DISABLED.toString());
            if (text.contains("%perks-speed%")) text = text.replace("%perks-speed%", Messages.DISABLED.toString());
            if (text.contains("%perks-resistance%")) text = text.replace("%perks-resistance%", Messages.DISABLED.toString());
            if (text.contains("%perks-fire_resistance%")) text = text.replace("%perks-fire_resistance%", Messages.DISABLED.toString());
        }

        if (text.contains("%stats-wins%")) {
            text = text.replace("%stats-wins%", String.valueOf(statisticManager.getWins(player.getUniqueId())));
        }
        if (text.contains("%stats-kills%")) {
            text = text.replace("%stats-kills%", String.valueOf(statisticManager.getKills(player.getUniqueId())));
        }
        if (text.contains("%stats-deaths%")) {
            text = text.replace("%stats-deaths%", String.valueOf(statisticManager.getDeaths(player.getUniqueId())));
        }
        if (text.contains("%stats-level%")) {
            text = text.replace("%stats-level%", String.valueOf(levelManager.getPlayerLevel(player.getUniqueId())));
        }
        if (text.contains("%requiredXP%")) {
            DecimalFormat format = new DecimalFormat("#######0");
            text = text.replace("%requiredXP%", format.format(levelManager.getRequiredExp(player.getUniqueId())));
        }

        if (text.contains("%winner%")) {
            text = text.replace("%winner%", UHCRun.plugin.getGame().getWinnerName());
        }

        /*if (text.contains("%voted-hp%")) {
            if (VoteManager.votedHP(player)) text = text.replace("%voted-hp%", voted);
            else text = text.replace("%voted-hp%", didnt_vote);
        }
        if (text.contains("%voted-ran-drops%")) {
            if (VoteManager.votedRanDrops(player)) text = text.replace("%voted-ran-drops%", voted);
            else text = text.replace("%voted-ran-drops%", didnt_vote);
        }
        if (text.contains("%voted-no-ench%")) {
            if (VoteManager.votedNoEnch(player)) text = text.replace("%voted-no-ench%", voted);
            else text = text.replace("%voted-no-ench%", didnt_vote);
        }
        if (text.contains("%voted-no-pots%")) {
            if (VoteManager.votedNoPots(player)) text = text.replace("%voted-no-pots%", voted);
            else text = text.replace("%voted-no-pots%", didnt_vote);
        }*/

        /*
        if (text.contains("%votes-hp%")) {
            text = text.replace("%votes-hp%", String.valueOf(VoteManager.hp.size()));
        }
        if (text.contains("%votes-ran-drops%")) {
            text = text.replace("%votes-ran-drops%", String.valueOf(VoteManager.ran_drops.size()));
        }
        if (text.contains("%votes-no-ench%")) {
            text = text.replace("%votes-no-ench%", String.valueOf(VoteManager.no_ench.size()));
        }
        if (text.contains("%votes-no-pots%")) {
            text = text.replace("%votes-no-pots%", String.valueOf(VoteManager.no_pots.size()));
        }*/

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

        if (PAPI && player != null) text = PlaceholderAPI.setPlaceholders(player, text);

        return text;

    }

    public static void replace(Player player, String text, String late, String now) {
        if (text.contains(late)) {
            text.replace(late, now);
        }
    }
}