package me.florixak.uhcrun.game.perks;

import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.XSeries.XPotion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Perk {

    private final String name;
    private List<String> actions;
    private final ItemStack displayItem;
    private final double cost;

    public Perk(String name, ItemStack displayItem, double cost, List<String> actions) {
        this.name = name;
        this.displayItem = displayItem;
        this.cost = cost;
        this.actions = actions;
    }

    public String getName() {
        return this.name;
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

    public List<String> getActions() {
        return this.actions;
    }

    public void getAction(Player p) {
        for (String action : getActions()) {
            if (action.contains("[INVENTORY]")) {
                String itemN = action.split(" ")[1];
                int amount = Integer.parseInt(action.split(" ")[2]);
                Material item = XMaterial.matchXMaterial(itemN.toUpperCase()).get().parseMaterial();
                p.getInventory().addItem(new ItemStack(item, amount));
            }
            if (action.contains("[EFFECT]")) {
                String effectN = action.split(" ")[1].toUpperCase();
                int duration = Integer.parseInt(action.split(" ")[2]);
                int amplifier = Integer.parseInt(action.split(" ")[3]);
                PotionEffectType potionEffectType = XPotion.matchXPotion(effectN).get().getPotionEffectType();
                p.addPotionEffect(potionEffectType.createEffect(duration*20, amplifier));
            }
        }
    }
}
