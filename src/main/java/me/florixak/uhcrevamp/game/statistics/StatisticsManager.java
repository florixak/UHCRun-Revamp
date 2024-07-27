package me.florixak.uhcrevamp.game.statistics;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StatisticsManager {

    public static ItemStack getPlayerStats(UHCPlayer uhcPlayer) {
        ItemStack playerStatsItem = XMaterial.matchXMaterial(GameValues.STATISTICS.PLAYER_STATS_DIS_ITEM.toUpperCase())
                .get().parseItem() == null || GameValues.STATISTICS.PLAYER_STATS_DIS_ITEM.equalsIgnoreCase("PLAYER_HEAD")
                ? Utils.getPlayerHead(uhcPlayer.getPlayer(), uhcPlayer.getName())
                : XMaterial.matchXMaterial(GameValues.STATISTICS.PLAYER_STATS_DIS_ITEM.toUpperCase())
                .get().parseItem();

        String playerStatsName = GameValues.STATISTICS.PLAYER_STATS_CUST_NAME != null
                ? GameValues.STATISTICS.PLAYER_STATS_CUST_NAME
                : uhcPlayer.getName();

        List<String> playerStatsLore = new ArrayList<>();

        for (String text : GameValues.STATISTICS.PLAYER_STATS_LORE) {
            text = PlaceholderUtil.setPlaceholders(text, uhcPlayer.getPlayer());
            playerStatsLore.add(TextUtils.color(text));
        }
        return ItemUtils.createItem(
                playerStatsItem.getType(),
                playerStatsName.replace("%player%", uhcPlayer.getName()),
                1,
                playerStatsLore);
    }

    public static ItemStack getTopStats(UHCPlayer uhcPlayer) {
        ItemStack topStatsItem = XMaterial.matchXMaterial(GameValues.STATISTICS.TOP_STATS_DIS_ITEM.toUpperCase())
                .get().parseItem() == null || GameValues.STATISTICS.TOP_STATS_DIS_ITEM.equalsIgnoreCase("PLAYER_HEAD")
                ? Utils.getPlayerHead(uhcPlayer.getPlayer(), uhcPlayer.getName())
                : XMaterial.matchXMaterial(GameValues.STATISTICS.TOP_STATS_DIS_ITEM.toUpperCase())
                .get().parseItem();

        String topStatsName = GameValues.STATISTICS.TOP_STATS_CUST_NAME != null
                ? GameValues.STATISTICS.TOP_STATS_CUST_NAME
                : "TOP STATS";

        List<String> topStatsLore = new ArrayList<>();
        String playerDisplayedTop = uhcPlayer.getData().getDisplayedTop();
        List<TopStatistics> totalTopList = GameManager.getGameManager().getPlayerManager().getTotalTop(playerDisplayedTop);

        if (totalTopList != null) {
            for (String lore : GameValues.STATISTICS.TOP_STATS_LORE) {
                for (int j = 0; j < GameValues.STATISTICS.TOP_STATS_LORE.size(); j++) {
                    if (totalTopList.size() > j) {
                        String name = totalTopList.get(j).getName();
                        int value = totalTopList.get(j).getValue();
                        lore = lore
                                .replace("%uhc-top-" + (j + 1) + "%", name != null ? name : "None")
                                .replace("%uhc-top-" + (j + 1) + "-value%", name != null ? String.valueOf(value) : String.valueOf(0));
                    } else {
                        lore = lore
                                .replace("%uhc-top-" + (j + 1) + "%", "None")
                                .replace("%uhc-top-" + (j + 1) + "-value%", String.valueOf(0));
                    }
                }
                topStatsLore.add(TextUtils.color(lore));
            }
        }
        return ItemUtils.createItem(topStatsItem.getType(),
                topStatsName.replace("%top-stats-mode%", TextUtils.color(TextUtils.toNormalCamelText(playerDisplayedTop.replace("-", " ")))),
                1,
                topStatsLore);
    }
}
