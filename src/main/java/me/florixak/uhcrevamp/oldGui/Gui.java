package me.florixak.uhcrevamp.oldGui;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class Gui implements InventoryHolder {

    protected UHCPlayer uhcPlayer;
    protected GameManager gameManager;
    protected Inventory inventory;
    protected int size;
    protected String title;

    public Gui(GameManager gameManager, UHCPlayer uhcPlayer, int size, String title) {
        this.gameManager = gameManager;
        this.uhcPlayer = uhcPlayer;
        this.size = size;
        this.title = title;
    }

    public void init() {
        this.inventory = Bukkit.createInventory(null, size, TextUtils.color(title));
        ItemStack empty = ItemUtils.createItem(XMaterial.AIR.parseMaterial(), " ", 1, null);

        for (int i = 0; i < size; i++) {
            getInventory().setItem(i, empty);
        }
    }

    public void open() {
        init();
        uhcPlayer.openInventory(getInventory());
    }

    public void close() {
        uhcPlayer.getPlayer().closeInventory();
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void setItem(int slot, ItemStack item) {
        getInventory().setItem(slot, item);
    }

    public boolean isNull(ItemStack item) {
        return item == null || item.getType().equals(XMaterial.AIR.parseMaterial());
    }

}
