package me.florixak.uhcrun.customDrop;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class CustomDropManager {

    private GameManager gameManager;
    private FileConfiguration custom_drop_cfg;

    private List<CustomDrop> custom_drops;

    public CustomDropManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.custom_drop_cfg = gameManager.getConfigManager().getFile(ConfigType.CUSTOM_DROPS).getConfig();

        this.custom_drops = new ArrayList<>();
    }

    public void loadCustomDrops() {
        for (String block : custom_drop_cfg.getConfigurationSection("custom-drops").getKeys(false)) {
            Material material = XMaterial.matchXMaterial(block.toUpperCase()).get().parseMaterial();

            List<Material> drops = new ArrayList<>();
            HashMap<Material, Integer> amount_map = new HashMap<>();
            if (custom_drop_cfg.getConfigurationSection("custom-drops." + block + ".drops") != null) {
                for (String drop : custom_drop_cfg.getConfigurationSection("custom-drops." + block + ".drops").getKeys(false)) {
                    Material b = XMaterial.matchXMaterial(drop).get().parseMaterial();
                    int amount = custom_drop_cfg.getInt("custom-drops." + block + ".drops." + drop);
                    drops.add(b);
                    amount_map.put(b, amount);
                }
            }
            int xp = custom_drop_cfg.getInt("custom-drops." + block + ".exp");

            CustomDrop customDrop = new CustomDrop(material, drops, amount_map, xp);
            this.custom_drops.add(customDrop);
            System.out.println(customDrop.getMaterial().toString());
            System.out.println(customDrop.getDrops().toString());
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
