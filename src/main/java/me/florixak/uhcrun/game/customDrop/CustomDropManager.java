package me.florixak.uhcrun.game.customDrop;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.utils.XSeries.XEntityType;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.*;

public class CustomDropManager {

    private final FileConfiguration customDropConfig;

    private List<CustomDrop> blockDrops;
    private List<CustomDrop> mobDrops;

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
        if (!GameValues.CUSTOM_DROPS_ENABLED) return;

        for (String block : customDropConfig.getConfigurationSection("custom-drops.blocks").getKeys(false)) {
            Material material = XMaterial.matchXMaterial(block.toUpperCase()).get().parseMaterial();

            List<Material> drops = new ArrayList<>();
            int minAmount = 1;
            int maxAmount = 1;

            ConfigurationSection section = customDropConfig.getConfigurationSection("custom-drops.blocks." + block + ".drops");

            if (section != null && section.getKeys(false) != null) {
                for (String drop : section.getKeys(false)) {
                    Material b = XMaterial.matchXMaterial(drop).get().parseMaterial();
                    List<Integer> amountList = customDropConfig.getIntegerList("custom-drops.blocks." + block + ".drops." + drop);
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
            int xp = customDropConfig.getInt("custom-drops.blocks." + block + ".exp");

            CustomDrop customDrop = new CustomDrop(material, drops, minAmount, maxAmount, xp);
            this.blockDrops.add(customDrop);
        }
    }

    public void loadMobDrops() {
        if (!GameValues.CUSTOM_DROPS_ENABLED) return;

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
}
