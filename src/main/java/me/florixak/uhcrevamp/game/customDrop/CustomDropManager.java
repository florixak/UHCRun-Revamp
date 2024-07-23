package me.florixak.uhcrevamp.game.customDrop;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.XSeries.XEntityType;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CustomDropManager {

    private final FileConfiguration customDropConfig;

    private final List<CustomDrop> blockDrops;
    private final List<CustomDrop> mobDrops;

    public CustomDropManager(GameManager gameManager) {
        this.customDropConfig = gameManager.getConfigManager().getFile(ConfigType.CUSTOM_DROPS).getConfig();

        this.blockDrops = new ArrayList<>();
        this.mobDrops = new ArrayList<>();
    }

    public void loadDrops() {
        loadOreDrops();
        loadMobDrops();
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

            List<Material> drops = new ArrayList<>();
            int minAmount = 1;
            int maxAmount = 1;

            ConfigurationSection section = customDropConfig.getConfigurationSection("custom-drops.blocks." + blockName + ".drops");
            if (section != null) {
                for (String dropName : section.getKeys(false)) {
                    Optional<XMaterial> dropMaterialOpt = XMaterial.matchXMaterial(dropName);
                    if (!dropMaterialOpt.isPresent()) continue; // Skip if drop material is not present

                    Material drop = dropMaterialOpt.get().parseMaterial();
                    if (drop == null) continue; // Skip if drop material cannot be parsed

                    List<Integer> amountList = customDropConfig.getIntegerList("custom-drops.blocks." + blockName + ".drops." + dropName);
                    drops.add(drop);
                    // Simplify amount handling
                    if (!amountList.isEmpty()) {
                        minAmount = Collections.min(amountList);
                        maxAmount = Collections.max(amountList);
                    }
                }
            }
            int xp = customDropConfig.getInt("custom-drops.blocks." + blockName + ".exp");

            CustomDrop customDrop = new CustomDrop(material, drops, minAmount, maxAmount, xp);
            this.blockDrops.add(customDrop);
            Bukkit.getLogger().info("Loaded custom drop for block: " + blockName);
        }
    }


    public void loadMobDrops() {
        if (!GameValues.GAME.CUSTOM_DROPS_ENABLED) return;

        for (String entity : customDropConfig.getConfigurationSection("custom-drops.mobs").getKeys(false)) {
            EntityType entityType = XEntityType.valueOf(entity.toUpperCase()).get();

            List<Material> drops = new ArrayList<>();
            int minAmount = 1;
            int maxAmount = 1;

            ConfigurationSection section = customDropConfig.getConfigurationSection("custom-drops.mobs." + entity + ".drops");

            if (section != null && section.getKeys(false) != null) {
                for (String drop : section.getKeys(false)) {
                    Material b = XMaterial.matchXMaterial(drop).get().parseMaterial();
                    List<Integer> amountList = customDropConfig.getIntegerList("custom-drops.mobs." + entity + ".drops." + drop);
                    drops.add(b);
                    if (amountList.size() == 1 && amountList.get(0) <= 0) {
                        minAmount = 0;
                        maxAmount = 0;
                    } else if (amountList.size() == 1 && amountList.get(0) > 0) {
                        minAmount = amountList.get(0);
                        maxAmount = amountList.get(0);
                    } else if (amountList.size() >= 2) {
                        int min = Collections.min(amountList);
                        int max = Collections.max(amountList);
                        if (min < 0 || max < 0) {
                            minAmount = 0;
                            maxAmount = 0;
                        } else {
                            minAmount = min;
                            maxAmount = max;
                        }
                    }
                }
            }
            int xp = customDropConfig.getInt("custom-drops.mobs." + entity + ".exp");

            CustomDrop customDrop = new CustomDrop(entityType, drops, minAmount, maxAmount, xp);
            this.mobDrops.add(customDrop);
        }
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
    }
}
