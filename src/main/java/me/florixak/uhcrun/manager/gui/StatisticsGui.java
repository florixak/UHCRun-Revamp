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

    public StatisticsGui() {
        super(9, "Statistics");
    }

    @Override
    public void init() {
        super.init();
        Player p = getWhoOpen();
        UHCPlayer uhcPlayer = GameManager.getGameManager().getPlayerManager().getUHCPlayer(p.getUniqueId());

        FileConfiguration config = GameManager.getGameManager().getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        List<String> lore = new ArrayList<>();

        ItemStack itemStack = XMaterial.matchXMaterial(config.getString("settings.statistics.player-stats.display-item").toUpperCase())
                    .get().parseItem() == null || config.getString("settings.statistics.player-stats.display-item").equalsIgnoreCase("PLAYER_HEAD")
                ? Utils.getPlayerHead(uhcPlayer.getName(), uhcPlayer.getName())
                : XMaterial.matchXMaterial(config.getString("settings.statistics.player-stats.display-item", "STONE").toUpperCase())
                    .get().parseItem();

        String name = config.getString("settings.statistics.player-stats.custom-name") != null
                ? config.getString("settings.statistics.player-stats.custom-name", uhcPlayer.getName())
                : uhcPlayer.getName();

        for (String text : config.getStringList("settings.statistics.player-stats.lore")) {
            lore.add(TextUtils.color(text
                    .replace("%player%", uhcPlayer.getName())
                    .replace("%uhc-level%", uhcPlayer.getName())
                    .replace("%required-uhc-exp%", uhcPlayer.getName())
                    .replace("%money%", uhcPlayer.getName())
                    .replace("%uhc-wins%", uhcPlayer.getName())
                    .replace("%uhc-losses%", uhcPlayer.getName())
                    .replace("%uhc-kills%", uhcPlayer.getName())
                    .replace("%uhc-deaths%", uhcPlayer.getName())
            ));
        }

        getInventory().setItem(4, ItemUtils.createItem(
                itemStack,
                name.replace("%player%", uhcPlayer.getName()),
                1,
                lore));
    }

    @Override
    public void openInv(Player p) {
        super.openInv(p);
    }

}
