package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public abstract class PaginatedMenu extends Menu {

    protected int currentPage = 0;

    // 28 empty slots per page
    protected int maxItemsPerPage = 28;

    protected int index = 0;

    public PaginatedMenu(MenuUtils menuUtils) {
        super(menuUtils);
    }

    //Set the border and menu buttons for the menu
    public void addMenuBorder() {
        inventory.setItem(48, ItemUtils.createItem(XMaterial.DARK_OAK_BUTTON.parseMaterial(), TextUtils.color("&6Previous"), 1, null));

        inventory.setItem(49, ItemUtils.createItem(XMaterial.BARRIER.parseMaterial(), TextUtils.color("&cClose"), 1, null));

        inventory.setItem(50, ItemUtils.createItem(XMaterial.DARK_OAK_BUTTON.parseMaterial(), TextUtils.color("&6Next"), 1, null));

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER);
            }
        }

        inventory.setItem(17, super.FILLER);
        inventory.setItem(18, super.FILLER);
        inventory.setItem(26, super.FILLER);
        inventory.setItem(27, super.FILLER);
        inventory.setItem(35, super.FILLER);
        inventory.setItem(36, super.FILLER);

        for (int i = 44; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER);
            }
        }
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    public void handlePaging(InventoryClickEvent event, List<?> recipesList) {
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Previous")) {
            handlePrevious();
        } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Next")) {
            handleNext(recipesList);
        }
    }

    private void handlePrevious() {
        if (currentPage == 0) {
            menuUtils.getUHCPlayer().sendMessage(ChatColor.GRAY + "You are already on the first page.");
        } else {
            currentPage -= 1;
            super.open();
        }
    }

    private void handleNext(List<?> list) {
        if (!((index + 1) >= list.size())) {
            currentPage += 1;
            super.open();
        } else {
            menuUtils.getUHCPlayer().sendMessage(ChatColor.GRAY + "You are on the last page.");
        }
    }
}
