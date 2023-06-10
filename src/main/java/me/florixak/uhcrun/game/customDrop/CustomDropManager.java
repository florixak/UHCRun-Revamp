package me.florixak.uhcrun.game.customDrop;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class CustomDropManager {

    private final GameManager gameManager;
    private final FileConfiguration custom_drop_cfg;

    private List<CustomDrop> custom_drops;

    public CustomDropManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.custom_drop_cfg = gameManager.getConfigManager().getFile(ConfigType.CUSTOM_DROPS).getConfig();

        this.custom_drops = new ArrayList<>();
    }

    public void loadCustomDrops() {
        if (!gameManager.areCustomDropsEnabled()) return;

        for (String block : custom_drop_cfg.getConfigurationSection("custom-drops").getKeys(false)) {
            Material material = XMaterial.matchXMaterial(block.toUpperCase()).get().parseMaterial();

            List<Material> drops = new ArrayList<>();
            int minAmount = 1;
            int maxAmount = 1;

            if (custom_drop_cfg.getConfigurationSection("custom-drops." + block + ".drops") != null &&
                    custom_drop_cfg.getConfigurationSection("custom-drops." + block + ".drops").getKeys(false) != null) {

                for (String drop : custom_drop_cfg.getConfigurationSection("custom-drops." + block + ".drops").getKeys(false)) {
                    Material b = XMaterial.matchXMaterial(drop).get().parseMaterial();
                    List<Integer> amount_list = custom_drop_cfg.getIntegerList("custom-drops." + block + ".drops." + drop);
                    drops.add(b);
                    if (amount_list.get(0) >= 0) {
                        minAmount = amount_list.get(0);
                    }
                    if (amount_list.size() > 1) {
                        maxAmount = amount_list.get(1);
                    }
                }
            }
            int xp = custom_drop_cfg.getInt("custom-drops." + block + ".exp");

            CustomDrop customDrop = new CustomDrop(material, null, drops, minAmount, maxAmount, xp);
            this.custom_drops.add(customDrop);
        }
    }

    public CustomDrop getCustomDrop(Material material) {
        for (CustomDrop customDrop : custom_drops) {
            if (customDrop.getMaterial().equals(material)) {
                return customDrop;
            }
        }
        return null;
    }

    public boolean hasCustomDrop(Material material) {
        return getCustomDrop(material) != null;
    }
}
