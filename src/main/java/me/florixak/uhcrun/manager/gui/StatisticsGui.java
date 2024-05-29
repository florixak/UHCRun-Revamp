package me.florixak.uhcrun.manager.gui;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.text.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StatisticsGui extends Gui {

    enum TopType {
        TOP_KILLS,
        TOP_DEATHS,
        TOP_WINS,
        TOP_LOSSES
    }

    public StatisticsGui(GameManager gameManager) {
        super(gameManager, 9, "Statistics");
    }

    @Override
    public void init() {
        super.init();
        Player p = getWhoOpen();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

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
                ? config.getString("settings.statistics.top-stats.custom-name", uhcPlayer.getName())
                : uhcPlayer.getName();

        List<String> topStatsLore = new ArrayList<>();

        for (int i = 0; i < GameValues.STATS_TOP_STATS_LORE.size(); i++) {
            String lore = TextUtils.color(GameValues.STATS_TOP_STATS_LORE.get(i));
            for (int j = 0; j < GameValues.STATS_TOP_STATS_LORE.size(); j++) {
                if (gameManager.getPlayerManager().getTotalTopWinners() == null) {
                    lore = lore.replace("%top-" + (j+1) + "%", "NONE");
                } else if (gameManager.getPlayerManager().getTotalTopWinners().get(j) == null) {
                    lore = lore.replace("%top-" + (j+1) + "%", "NONE");
                } else {
                    String name = gameManager.getPlayerManager().getTotalTopWinners().get(j);
                    lore = lore.replace("%top-" + (j+1) + "%", name);
                }
            }
            topStatsLore.add(lore);
        }

        getInventory().setItem(8, ItemUtils.createItem(topStatsItem, topStatsName, 1, topStatsLore));
    }

    @Override
    public void openInv(Player p) {
        super.openInv(p);
    }

}
