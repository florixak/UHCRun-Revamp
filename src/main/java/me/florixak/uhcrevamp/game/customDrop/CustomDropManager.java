package me.florixak.uhcrevamp.game.customDrop;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.MathUtils;
import me.florixak.uhcrevamp.utils.XSeries.XEntityType;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustomDropManager {

	private final FileConfiguration customDropConfig;

	private final List<CustomDrop> blockDrops;
	private final List<CustomDrop> mobDrops;
	private final Map<Material, Integer> appleChanceMap;

	public CustomDropManager(GameManager gameManager) {
		this.customDropConfig = gameManager.getConfigManager().getFile(ConfigType.CUSTOM_DROPS).getConfig();

		this.blockDrops = new ArrayList<>();
		this.mobDrops = new ArrayList<>();
		this.appleChanceMap = new HashMap<>();
	}

	public void loadDrops() {
		loadOreDrops();
		loadMobDrops();
		loadAppleDropper();
	}

	public void loadAppleDropper() {
		ConfigurationSection appleSection = customDropConfig.getConfigurationSection("custom-drops.apples");
		if (appleSection == null) return;
		int chances = 0;

		for (String appleName : appleSection.getKeys(false)) {
			Optional<XMaterial> materialOpt = XMaterial.matchXMaterial(appleName.toUpperCase());
			if (!materialOpt.isPresent()) continue; // Skip if material is not present

			Material material = materialOpt.get().parseMaterial();
			if (material == null) continue; // Skip if material cannot be parsed (not supported in 1.8.8)

			int chance = customDropConfig.getInt("custom-drops.apples." + appleName + ".chance");
			chances += chance;
			this.appleChanceMap.put(material, chance);
		}
		if (chances < 100)
			this.appleChanceMap.put(XMaterial.AIR.parseMaterial(), 100 - chances);
	}

	public void loadOreDrops() {
		if (!GameValues.GAME.CUSTOM_DROPS_ENABLED) return;

		ConfigurationSection blocksSection = customDropConfig.getConfigurationSection("custom-drops.blocks");
		if (blocksSection == null) return;

		for (String blockName : blocksSection.getKeys(false)) {
			Optional<XMaterial> materialOpt = XMaterial.matchXMaterial(blockName.toUpperCase());
			if (!materialOpt.isPresent()) continue; // Skip if material is not present

			Material material = materialOpt.get().parseMaterial();
			if (material == null) continue; // Skip if material cannot be parsed (not supported in 1.8.8)

			List<String> drops = new ArrayList<>();
			Map<String, Integer> durability = new HashMap<>();
			int minAmount = 1;
			int maxAmount = 1;

			ConfigurationSection blockSection = blocksSection.getConfigurationSection(blockName + ".drops");
			if (blockSection != null && blockSection.getKeys(false) != null) {
				for (String drop : blockSection.getKeys(false)) {
					ItemStack itemStack = new ItemStack(XMaterial.matchXMaterial(drop).get().parseMaterial());
					List<Integer> amountList = blockSection.getIntegerList(drop);
					if (blockSection.getConfigurationSection(drop) != null) {
						durability.put(drop, blockSection.getInt(drop + ".durability", itemStack.getDurability()));
//						Bukkit.getLogger().info("Durability: " + blockSection.getInt(drop + ".durability", itemStack.getDurability()));
						amountList = blockSection.getIntegerList(drop + ".amount");
					}
					drops.add(itemStack.getType().name());
					int[] minMax = calculateMinMaxAmount(amountList);
					minAmount = minMax[0];
					maxAmount = minMax[1];
//                    Bukkit.getLogger().info("Loaded custom drop for block: " + blockName + " min: " + minAmount + " max: " + maxAmount);
				}
			}
			int xp = customDropConfig.getInt("custom-drops.blocks." + blockName + ".exp");

			CustomDrop customDrop = new CustomDrop(material, drops, durability, minAmount, maxAmount, xp);
			this.blockDrops.add(customDrop);
		}
	}

	public void loadMobDrops() {
		if (!GameValues.GAME.CUSTOM_DROPS_ENABLED) return;

		for (String entityName : customDropConfig.getConfigurationSection("custom-drops.mobs").getKeys(false)) {
			EntityType entityType = XEntityType.valueOf(entityName.toUpperCase()).get();

			List<String> drops = new ArrayList<>();
			int minAmount = 1;
			int maxAmount = 1;

			ConfigurationSection section = customDropConfig.getConfigurationSection("custom-drops.mobs." + entityName + ".drops");

			if (section != null && section.getKeys(false) != null) {
				for (String drop : section.getKeys(false)) {
					ItemStack itemStack = new ItemStack(XMaterial.matchXMaterial(drop).get().parseMaterial());
					List<Integer> amountList = customDropConfig.getIntegerList("custom-drops.mobs." + entityName + ".drops." + drop);
					if (section.getConfigurationSection(drop) != null) {
						itemStack.setDurability((short) section.getInt(drop + ".durability", itemStack.getDurability()));
						amountList = section.getIntegerList(drop + ".amount");
					}
					drops.add(itemStack.getType().name());
					int[] minMax = calculateMinMaxAmount(amountList);
					minAmount = minMax[0];
					maxAmount = minMax[1];
//                    Bukkit.getLogger().info("Loaded custom drop for mob: " + entityName + " min: " + minAmount + " max: " + maxAmount);
				}
			}
			int xp = customDropConfig.getInt("custom-drops.mobs." + entityName + ".exp");

			CustomDrop customDrop = new CustomDrop(entityType, drops, minAmount, maxAmount, xp);
			this.mobDrops.add(customDrop);
		}
	}

	private int[] calculateMinMaxAmount(List<Integer> amountList) {
		int minAmount = 1;
		int maxAmount = 1;

		if (amountList.size() == 1) {
			int amount = amountList.get(0);
			minAmount = maxAmount = Math.max(amount, 0);
		} else if (amountList.size() >= 2) {
			minAmount = Math.max(Collections.min(amountList), 0);
			maxAmount = Math.max(Collections.max(amountList), 0);
		}

		return new int[]{minAmount, maxAmount};
	}

	public Material pickAppleToDrop(Map<Material, Integer> applesWithChances) {
		int totalChance = applesWithChances.values().stream().mapToInt(Integer::intValue).sum();
		int randomChance = MathUtils.getRandom().nextInt(totalChance);

		int cumulativeChance = 0;
		for (Map.Entry<Material, Integer> entry : applesWithChances.entrySet()) {
			cumulativeChance += entry.getValue();
			if (randomChance < cumulativeChance) {
				return entry.getKey();
			}
		}
		return null; // In case no apple is selected
	}

	public Map<Material, Integer> getAppleChanceMap() {
		return appleChanceMap;
	}


	public CustomDrop getCustomMobDrop(EntityType entityType) {
		if (entityType != null) {
			for (CustomDrop customDrop : mobDrops) {
				if (customDrop.getEntityType().equals(entityType)) {
					return customDrop;
				}
			}
		}
		return null;
	}

	public CustomDrop getCustomBlockDrop(Material material) {
		if (material != null) {
			for (CustomDrop customDrop : blockDrops) {
				if (customDrop.getMaterial().equals(material)) {
					return customDrop;
				}
			}
		}
		return null;
	}

	public CustomDrop getCustomBlockDrop(String material, boolean ore) {
		for (CustomDrop customDrop : blockDrops) {
			if (ore) {
				if (customDrop.getMaterial().name().toUpperCase().contains(material.toUpperCase()) && customDrop.getMaterial().name().contains("ORE")) {
//					Bukkit.getLogger().info(customDrop.getMaterial().name() + " was found in custom drops");
					return customDrop;
				}
			} else {
				if (customDrop.getMaterial().name().toUpperCase().contains(material.toUpperCase())) {
					return customDrop;
				}
			}
		}
		return null;
	}

	public boolean hasMobCustomDrop(EntityType entityType) {
		if (entityType == null) return false;
		return getCustomMobDrop(entityType) != null;
	}

	public boolean hasBlockCustomDrop(Material material) {
		if (material == null) return false;
		return getCustomBlockDrop(material) != null;
	}

	public void onDisable() {
		blockDrops.clear();
		mobDrops.clear();
		appleChanceMap.clear();
	}
}
