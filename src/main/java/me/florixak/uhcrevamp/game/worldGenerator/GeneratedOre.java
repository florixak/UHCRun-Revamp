package me.florixak.uhcrevamp.game.worldGenerator;

import org.bukkit.Material;

public class GeneratedOre {

	private final Material material;
	private final int minVein;
	private final int maxVein;

	public GeneratedOre(final Material material, final int minVein, final int maxVein) {
		this.material = material;
		this.minVein = minVein;
		this.maxVein = maxVein;
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
}
