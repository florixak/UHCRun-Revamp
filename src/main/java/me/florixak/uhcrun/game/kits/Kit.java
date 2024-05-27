package me.florixak.uhcrun.game.kits;

import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {

    private final String name;
    private List<ItemStack> items;
    private final Material displayItem;
    private final double cost;

    public Kit(String name, Material displayItem, double cost, List<ItemStack> items) {
        this.name = name;
        this.displayItem = displayItem;
        this.cost = cost;
        this.items = items;
    }

    public String getName() {
        return this.name;
    }

    public Material getDisplayItem() {
        return this.displayItem;
    }

    public double getCost() {
        return this.cost;
    }

    public boolean isFree() {
        return getCost() == 0;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public void giveKit(UHCPlayer uhcPlayer) {
        Player p = uhcPlayer.getPlayer();

        for (ItemStack item : getItems()) {
            p.getInventory().addItem(item);
        }
    }
}
