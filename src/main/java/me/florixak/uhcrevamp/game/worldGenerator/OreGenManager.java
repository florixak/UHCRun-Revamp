package me.florixak.uhcrevamp.game.worldGenerator;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class OreGenManager {

	private final FileConfiguration oreGenConfig;

	private final List<GeneratedOre> oreGenList;

	public OreGenManager(final GameManager gameManager) {
		this.oreGenConfig = gameManager.getConfigManager().getFile(ConfigType.ORE_GENERATION).getConfig();

		this.oreGenList = new ArrayList<>();
	}

	public void loadOres() {
		final ConfigurationSection section = oreGenConfig.getConfigurationSection("ore-generation");
		if (section == null || section.getKeys(false) == null) return;

		for (final String materialN : section.getKeys(false)) {
			final Material matchMaterial = XMaterial.matchXMaterial(materialN.toUpperCase()).get().parseMaterial();
			final Material material = matchMaterial != null ? matchMaterial : XMaterial.STONE.parseMaterial();

			if (canSkip(material)) return;

//            int spawnAmount = oreGenConfig.getInt("ore-generation." + materialN + ".spawn-amount", 0);
			final int minVein = oreGenConfig.getInt("ore-generation." + materialN + ".min-vein", 0);
			int maxVein = oreGenConfig.getInt("ore-generation." + materialN + ".max-vein", 0);

			if (minVein <= 0 || maxVein <= 0) return;
			if (minVein == maxVein || maxVein < minVein) maxVein = minVein;

			final GeneratedOre oreGen = new GeneratedOre(material, minVein, maxVein);
			oreGenList.add(oreGen);
		}
	}

	public List<GeneratedOre> getOreGeneratorList() {
		return this.oreGenList;
	}

	private boolean canSkip(final Material material) {
		for (final GeneratedOre oreGen : oreGenList) {
			if (oreGen.getMaterial().equals(material)
					|| material.equals(XMaterial.STONE.parseMaterial())) {
				return true;
			}
		}
		return false;
	}
}
