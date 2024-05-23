package me.florixak.uhcrun.manager.gui;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class Gui implements InventoryHolder {

    protected GameManager gameManager;
    private Player whoOpen;
    private Inventory inventory;
    private int size;
    private String title;

    public Gui(GameManager gameManager, int size, String title) {
        this.gameManager = gameManager;
        this.size = size;
        this.title = title;
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
        this.inventory = Bukkit.createInventory(null, size, TextUtils.color(title));
        ItemStack empty = createItem(XMaterial.AIR, " ", null);

        for (int i = 0; i < getSize(); i++) {
            getInventory().setItem(i, empty);
        }
    }

    public ItemStack createItem(XMaterial material, String name, List<String> lore) {
        ItemStack item = new ItemStack(Objects.requireNonNull(material.parseMaterial(), "Cannot create item from null."));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) meta.setDisplayName(TextUtils.color(name));
        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
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
