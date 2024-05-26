package me.florixak.uhcrun.game.kits;

import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {

    private final String name;
    private List<ItemStack> items;
    private final Material display_item;
    private final double cost;

    public Kit(String name, Material display_item, double cost, List<ItemStack> items) {
        this.name = name;
        this.display_item = display_item;
        this.cost = cost;
        this.items = items;
    }

    public String getName() {
        return this.name;
    }

    public Material getDisplayItem() {
        return this.display_item;
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
