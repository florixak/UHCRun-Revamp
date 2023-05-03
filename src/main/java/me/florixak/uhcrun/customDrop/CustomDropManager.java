package me.florixak.uhcrun.customDrop;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            for (String drop : custom_drop_cfg.getStringList("custom-drops." + block + ".drops")) {
                drops.add(XMaterial.matchXMaterial(drop).get().parseMaterial());
            }
            int amount = custom_drop_cfg.getInt("custom-drops." + block + ".max-drop");
            int xp = custom_drop_cfg.getInt("custom-drops." + block + ".exp");

            this.custom_drops.add(new CustomDrop(material, drops, amount, xp));
        }
    }

    public CustomDrop getCustomDrop(Material material) {
        for (CustomDrop customDrop : custom_drops) {
            if (customDrop.getBlock().equals(material)) {
                return customDrop;
            }
        }
        return null;
    }

    public boolean hasCustomDrop(Material material) {
        return custom_drops.contains(getCustomDrop(material));
    }

    public void dropItem(Player p, BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = event.getBlock().getLocation();
        Random ran = new Random();

        if (hasCustomDrop(block.getType())) {
            event.setExpToDrop(0);
            event.setDropItems(false);

            CustomDrop customDrop = getCustomDrop(block.getType());
            int drop = ran.nextInt(customDrop.getDrops().size());
            int amount = ran.nextInt(customDrop.getMaxAmount())+1;
            if (amount > 0 && drop > 0) {
                Bukkit.getWorld(loc.getWorld().getName()).dropItemNaturally(loc, new ItemStack(customDrop.getDrops().get(drop), amount));
            }
            p.giveExp(customDrop.getExp());
            gameManager.getSoundManager().playOreDestroySound(p);
        }
    }
}
