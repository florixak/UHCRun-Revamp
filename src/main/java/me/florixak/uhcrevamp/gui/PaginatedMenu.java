package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public abstract class PaginatedMenu extends Menu {

    protected int currentPage = 0;

    protected String title = "Inventory";
    protected int slots = 54;

    protected int index = 0;

    public PaginatedMenu(MenuUtils menuUtils, String title) {
        super(menuUtils);
        this.title = title;
    }

    @Override
    public String getMenuName() {
        return formattedMenuName();
    }

    private String formattedMenuName() {
        if (getMaxPages() == 1) {
            return TextUtils.color(title);
        } else {
            return TextUtils.color(title + " - " + getMaxPages());
        }
    }

    public int getMaxPages() {
        return (int) Math.ceil((double) getMaxItemsPerPage() / (double) getMaxItemsPerPage());
    }

    @Override
    public int getSlots() {
        return slots;
    }

    //Set the border and menu buttons for the menu
    public void addMenuBorder() {
        inventory.setItem(getSlots() - 6, ItemUtils.createItem(XMaterial.DARK_OAK_BUTTON.parseMaterial(), TextUtils.color("&6Previous"), 1, null));

        inventory.setItem(getSlots() - 5, ItemUtils.createItem(XMaterial.BARRIER.parseMaterial(), TextUtils.color("&cClose"), 1, null));

        inventory.setItem(getSlots() - 4, ItemUtils.createItem(XMaterial.DARK_OAK_BUTTON.parseMaterial(), TextUtils.color("&6Next"), 1, null));

//        for (int i = 0; i < 10; i++) {
//            if (inventory.getItem(i) == null) {
//                inventory.setItem(i, super.FILLER);
//            }
//        }
//
//        inventory.setItem(17, super.FILLER);
//        inventory.setItem(18, super.FILLER);
//        inventory.setItem(26, super.FILLER);
//        inventory.setItem(27, super.FILLER);
//        inventory.setItem(35, super.FILLER);
//        inventory.setItem(36, super.FILLER);
//
//        for (int i = slots - 10; i < slots; i++) {
//            if (inventory.getItem(i) == null) {
//                inventory.setItem(i, super.FILLER);
//            }
//        }
    }

    public int getMaxItemsPerPage() {
        return getSlots() - 9;
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
