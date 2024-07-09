package me.florixak.uhcrun.game.worldGenerator;

import org.bukkit.Material;

public class OreGen {

    private final Material material;
    private final int minVein;
    private final int maxVein;
    private final int spawnAmount;

    public OreGen(Material material, int minVein, int maxVein, int spawnAmount) {
        this.material = material;
        this.minVein = minVein;
        this.maxVein = maxVein;
        this.spawnAmount = spawnAmount;
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getMinVein() {
        return this.minVein;
    }

    public int getMaxVein() {
        return this.maxVein;
    }

    public int getSpawnAmount() {
        return this.spawnAmount;
    }
}
