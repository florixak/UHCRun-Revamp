package me.florixak.uhcrevamp.gui.menu;

import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.game.statistics.StatisticsManager;
import me.florixak.uhcrevamp.gui.Menu;
import me.florixak.uhcrevamp.gui.MenuUtils;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class StatisticsMenu extends Menu {

    private final UHCPlayer uhcPlayer;

    public StatisticsMenu(MenuUtils menuUtils) {
        super(menuUtils);
        this.uhcPlayer = menuUtils.getUHCPlayer();
    }

    @Override
    public String getMenuName() {
        return TextUtils.color(GameValues.INVENTORY.STATS_TITLE);
    }

    @Override
    public int getSlots() {
        return GameValues.INVENTORY.STATS_SLOTS;
    }

    @Override
    public void handleMenuClicks(InventoryClickEvent event) {
        if (event.getCurrentItem().getType().equals(XMaterial.BARRIER.parseMaterial())) {
            close();
        } else if (event.getCurrentItem().getType().equals(XMaterial.DARK_OAK_BUTTON.parseMaterial())) {
            handleStatistics(event);
        }

    }

    @Override
    public void setMenuItems() {
        getInventory().setItem(GameValues.STATISTICS.PLAYER_STATS_SLOT, StatisticsManager.getPlayerStats(uhcPlayer));
        getInventory().setItem(GameValues.STATISTICS.TOP_SLOT, StatisticsManager.getTopStats(uhcPlayer));


        inventory.setItem(getSlots() - 5, ItemUtils.createItem(
                XMaterial.matchXMaterial(GameValues.INVENTORY.CLOSE_ITEM).get().parseMaterial(),
                TextUtils.color(GameValues.INVENTORY.CLOSE_TITLE),
                1,
                null));
    }

    private void handleStatistics(InventoryClickEvent event) {
        if (ItemUtils.hasItemMeta(event.getCurrentItem())) {
            if (event.getRawSlot() == GameValues.STATISTICS.TOP_SLOT) {
                String displayedTop = uhcPlayer.getData().getDisplayedTop();
                List<String> displayedTops = GameValues.STATISTICS.DISPLAYED_TOPS;
                for (int i = 0; i < displayedTops.size(); i++) {
                    if (displayedTop.equalsIgnoreCase(displayedTops.get(i))) {
                        if (displayedTops.get(i).equalsIgnoreCase(displayedTops.get(displayedTops.size() - 1)))
                            uhcPlayer.getData().setDisplayedTop(displayedTops.get(0));
                        else uhcPlayer.getData().setDisplayedTop(displayedTops.get(i + 1));
                    }
                }
                close();
                new StatisticsMenu(menuUtils).open();
            }
        }
    }
}
