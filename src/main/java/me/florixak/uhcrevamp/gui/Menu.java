package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class Menu implements InventoryHolder {

    protected MenuUtils menuUtils;
    protected Inventory inventory;
    protected ItemStack FILLER = ItemUtils.createItem(XMaterial.AIR.parseMaterial(), " ", 1, null);

    public Menu(MenuUtils menuUtils) {
        this.menuUtils = menuUtils;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenuClicks(InventoryClickEvent e);

    public abstract void setMenuItems();

    public void open() {
        // The owner of the inventory created is the Menu itself,
        // so we are able to reverse engineer the Menu object from the
        // inventoryHolder in the MenuListener class when handling clicks
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();

        menuUtils.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void close() {
        menuUtils.getUHCPlayer().getPlayer().closeInventory();
    }

    public void setFiller() {
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, FILLER);
            }
        }
    }

}

