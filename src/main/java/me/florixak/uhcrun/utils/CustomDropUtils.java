package me.florixak.uhcrun.utils;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.manager.SoundManager;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CustomDropUtils {

    public static void dropItem(Player p, BlockBreakEvent event) {
        FileConfiguration config = UHCRun.getInstance().getConfigManager().getFile(ConfigType.CUSTOM_DROPS).getConfig();
        Location loc = event.getBlock().getLocation();

        ItemStack drop;
        Material drop_item;
        Random ran = new Random();
        int size;
        int xp;
        int amount;

        for (String block : config.getConfigurationSection("custom-drops").getKeys(false)) {
            Material material = XMaterial.matchXMaterial(config.getConfigurationSection("custom-drops." + block).getString("material").toUpperCase()).get().parseMaterial();
            if (event.getBlock().getType() == material) {

                event.setDropItems(false);
                event.setExpToDrop(0);

                size = config.getStringList("custom-drops." + block + ".drops").size();

                amount = config.getInt("custom-drops." + block + ".max-drop");
                drop_item = XMaterial.matchXMaterial(config.getConfigurationSection("custom-drops." + block).getStringList("drops").get(ran.nextInt(size)).toUpperCase()).get().parseMaterial();
                if (drop_item != null && amount != 0) {
                    drop = new ItemStack(drop_item, ran.nextInt(amount)+1);
                    loc.getWorld().dropItemNaturally(loc, drop);
                }
                xp = config.getInt("custom-drops." + block + ".xp");
                p.giveExp(xp);

                SoundManager.playOreDestroySound(p);
                break;
            }
        }
    }
}

