package me.florixak.uhcrun.customDrop;

import org.bukkit.Material;

import java.util.List;

public class CustomDrop {

    private Material block;
    private List<Material> drops;
    private int max_amount;
    private int exp;

    public CustomDrop(Material block, List<Material> drops, int max_amount, int exp) {
        this.block = block;
        this.drops = drops;
        this.max_amount = max_amount;
        this.exp = exp;
    }

    public Material getBlock() {
        return block;
    }

    public List<Material> getDrops() {
        return drops;
    }

    public int getExp() {
        return exp;
    }

    public int getMaxAmount() {
        return max_amount;
    }
}
