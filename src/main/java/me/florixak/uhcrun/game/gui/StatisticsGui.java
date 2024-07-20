package me.florixak.uhcrun.game.gui;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.player.UHCPlayer;
import me.florixak.uhcrun.game.statistics.TopStatistic;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StatisticsGui extends Gui {

    public StatisticsGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 3 * GameValues.COLUMNS, TextUtils.color(GameValues.INVENTORY.STATS_TITLE));
    }

    @Override
    public void init() {
        super.init();
        getInventory().setItem(GameValues.STATISTICS.PLAYER_STATS_SLOT, getPlayerStats());
        getInventory().setItem(GameValues.STATISTICS.TOP_SLOT, getTopStats());
    }

    public ItemStack getPlayerStats() {
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
            playerStatsLore.add(TextUtils.color(text
                    .replace("%player%", uhcPlayer.getName())
                    .replace("%uhc-level%", String.valueOf(uhcPlayer.getData().getUHCLevel()))
                    .replace("%uhc-exp%", String.valueOf(uhcPlayer.getData().getUHCExp()))
                    .replace("%required-uhc-exp%", String.valueOf(uhcPlayer.getData().getRequiredUHCExp()))
                    .replace("%money%", String.valueOf(uhcPlayer.getData().getMoney()))
                    .replace("%uhc-wins%", String.valueOf(uhcPlayer.getData().getWins()))
                    .replace("%uhc-losses%", String.valueOf(uhcPlayer.getData().getLosses()))
                    .replace("%uhc-kills%", String.valueOf(uhcPlayer.getData().getKills()))
                    .replace("%uhc-deaths%", String.valueOf(uhcPlayer.getData().getDeaths()))
            ));
        }
        return ItemUtils.createItem(
                playerStatsItem.getType(),
                playerStatsName.replace("%player%", uhcPlayer.getName()),
                1,
                playerStatsLore);
    }

    public ItemStack getTopStats() {
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
        List<TopStatistic> totalTopList = gameManager.getPlayerManager().getTotalTop(playerDisplayedTop);

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

    @Override
    public void open() {
        super.open();
    }
}
