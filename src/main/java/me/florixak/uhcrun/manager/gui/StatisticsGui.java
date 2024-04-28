package me.florixak.uhcrun.manager.gui;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StatisticsGui extends Gui {

    public StatisticsGui(GameManager gameManager) {
        super(gameManager, 9, "Statistics");
    }

    @Override
    public void init() {
        super.init();
        Player p = getWhoOpen();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        List<String> lore = new ArrayList<>();

        ItemStack itemStack = XMaterial.matchXMaterial(config.getString("settings.statistics.player-stats.display-item").toUpperCase())
                .get().parseItem() == null || config.getString("settings.statistics.player-stats.display-item").equalsIgnoreCase("PLAYER_HEAD")
                ? Utils.getPlayerHead(p, uhcPlayer.getName())
                : XMaterial.matchXMaterial(config.getString("settings.statistics.player-stats.display-item", "STONE").toUpperCase())
                .get().parseItem();

        String name = config.getString("settings.statistics.player-stats.custom-name") != null
                ? config.getString("settings.statistics.player-stats.custom-name", uhcPlayer.getName())
                : uhcPlayer.getName();

        for (String text : config.getStringList("settings.statistics.player-stats.lore")) {
            lore.add(TextUtils.color(text
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
                itemStack,
                name.replace("%player%", uhcPlayer.getName()),
                1,
                lore));

        getInventory().setItem(8, ItemUtils.createItem(new ItemStack(XMaterial.OAK_SIGN.parseMaterial()), "TOP", 1, null));
    }

    @Override
    public void openInv(Player p) {
        super.openInv(p);
    }

}
