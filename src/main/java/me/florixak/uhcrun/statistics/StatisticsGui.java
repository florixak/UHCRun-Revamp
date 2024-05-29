package me.florixak.uhcrun.statistics;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.manager.gui.Gui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.text.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StatisticsGui extends Gui {

    private static String totalTopMode = "Kills";

    public StatisticsGui(GameManager gameManager) {
        super(gameManager, 9, "Statistics");
    }

    @Override
    public void init() {
        super.init();
        preLoad();
    }

    private void preLoad() {
        Player p = getWhoOpen();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

        ItemStack playerStatsItem = XMaterial.matchXMaterial(GameValues.STATS_PLAYER_STATS_DIS_ITEM.toUpperCase())
                .get().parseItem() == null || GameValues.STATS_PLAYER_STATS_DIS_ITEM.equalsIgnoreCase("PLAYER_HEAD")
                ? Utils.getPlayerHead(p, uhcPlayer.getName())
                : XMaterial.matchXMaterial(GameValues.STATS_PLAYER_STATS_DIS_ITEM.toUpperCase())
                .get().parseItem();

        String playerStatsName = GameValues.STATS_PLAYER_STATS_CUST_NAME != null
                ? GameValues.STATS_PLAYER_STATS_CUST_NAME
                : uhcPlayer.getName();

        List<String> playerStatsLore = new ArrayList<>();

        for (String text : GameValues.STATS_PLAYER_STATS_LORE) {
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

        getInventory().setItem(4, ItemUtils.createItem(
                playerStatsItem,
                playerStatsName.replace("%player%", uhcPlayer.getName()),
                1,
                playerStatsLore));


        ItemStack topStatsItem = XMaterial.matchXMaterial(GameValues.STATS_TOP_STATS_DIS_ITEM.toUpperCase())
                .get().parseItem() == null || GameValues.STATS_TOP_STATS_DIS_ITEM.equalsIgnoreCase("PLAYER_HEAD")
                ? Utils.getPlayerHead(p, uhcPlayer.getName())
                : XMaterial.matchXMaterial(GameValues.STATS_TOP_STATS_DIS_ITEM.toUpperCase())
                .get().parseItem();

        String topStatsName = GameValues.STATS_TOP_STATS_CUST_NAME != null
                ? GameValues.STATS_TOP_STATS_CUST_NAME
                : "TOP STATS";

        List<String> topStatsLore = new ArrayList<>();
        List<TopStatistic> totalTopList = gameManager.getPlayerManager().getTotalTop("kills");

        for (String lore : GameValues.STATS_TOP_STATS_LORE) {
            for (int j = 0; j < GameValues.STATS_TOP_STATS_LORE.size(); j++) {
                if (totalTopList.size() > j) {
                    String name = totalTopList.get(j).getName();
                    int value = totalTopList.get(j).getValue();
                    lore = lore
                            .replace("%top-" + (j + 1) + "%", name != null ? name : "None")
                            .replace("%top-" + (j + 1) + "-value%", name != null ? String.valueOf(value) : String.valueOf(0));
                } else {
                    lore = lore
                            .replace("%top-" + (j + 1) + "%", "None")
                            .replace("%top-" + (j + 1) + "-value%", String.valueOf(0));
                }
            }
            topStatsLore.add(TextUtils.color(lore));
        }

        getInventory().setItem(8, ItemUtils.createItem(topStatsItem, topStatsName.replace("%top-stats-mode%", totalTopMode), 1, topStatsLore));
    }

    @Override
    public void openInv(Player p) {
        super.openInv(p);
    }

}
