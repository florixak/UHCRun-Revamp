package me.florixak.uhcrun.manager.gui;

import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Gui implements InventoryHolder {

    private Player whoOpen;
    private Inventory inventory;
    private int size;
    private String title;

    public Gui(int size, String title) {
        this.size = size;
        this.title = title;
        this.inventory = Bukkit.createInventory(null, size, TextUtils.color(title));
    }

    public Player getWhoOpen() {
        return this.whoOpen;
    }

    public void setSize(int slots) {
        this.size = slots;
    }

    public int getSize() {
        return this.size;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void init() {
        ItemStack empty = createItem(XMaterial.AIR.parseMaterial(), " ", null);

        for (int i = 0; i < getSize(); i++) {
            getInventory().setItem(i, empty);
        }
    }

    public ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) meta.setDisplayName(TextUtils.color(name));
        if (lore != null) {
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openInv(Player p) {
        this.whoOpen = p;
        init();
        p.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }


}
