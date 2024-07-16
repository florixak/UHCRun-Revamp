package me.florixak.uhcrun.game.gui;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Gui {

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

    public Inventory getInventory() {
        return this.inventory;
    }


}
