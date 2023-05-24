package me.florixak.uhcrun.game.perks;

import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.XSeries.XPotion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Perk {

    private String name;
    private List<String> actions;
    private Material display_item;
    private double cost;

    public Perk(String name, Material display_item, double cost, List<String> actions) {
        this.name = name;
        this.display_item = display_item;
        this.cost = cost;
        this.actions = actions;
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
