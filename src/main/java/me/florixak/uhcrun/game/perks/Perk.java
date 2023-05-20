package me.florixak.uhcrun.game.perks;

import org.bukkit.Material;

import java.util.List;

public class Perk {

    private String name;
    private List<String> options;
    private Material display_item;
    private double cost;

    public Perk(String name, Material display_item, double cost, List<String> options) {
        this.name = name;
        this.display_item = display_item;
        this.cost = cost;
        this.options = options;
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

    public List<String> getOptions() {
        return this.options;
    }
}
