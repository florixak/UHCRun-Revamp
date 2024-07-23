package me.florixak.uhcrun.game.kits;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.player.UHCPlayer;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {

    private final String name;
    private final String displayName;
    private final List<ItemStack> items;
    private final ItemStack displayItem;
    private final double cost;

    public Kit(String name, String displayName, ItemStack displayItem, double cost, List<ItemStack> items) {
        this.name = name;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.cost = cost;
        this.items = items;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return TextUtils.color(this.displayName);
    }

    public ItemStack getDisplayItem() {
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

    public String getFormattedCost() {
        return Messages.KITS_COST.toString().replace("%cost%", String.valueOf(getCost()));
    }

    public void giveKit(UHCPlayer uhcPlayer) {
        Player p = uhcPlayer.getPlayer();

        for (ItemStack item : getItems()) {
            p.getInventory().addItem(item);
        }
    }
}
