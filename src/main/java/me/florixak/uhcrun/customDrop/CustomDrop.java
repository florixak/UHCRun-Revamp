package me.florixak.uhcrun.customDrop;

import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CustomDrop {

    private Material material;
    private List<Material> drops;
    private HashMap<Material, Integer> amount;
    private int exp;

    public CustomDrop(Material material, List<Material> drops, HashMap<Material, Integer> amount, int exp) {
        this.material = material;
        this.drops = drops;
        this.amount = amount;
        this.exp = exp;
    }

    public Material getMaterial() {
        return material;
    }

    public List<Material> getDrops() {
        return drops;
    }

    public int getExp() {
        return exp;
    }

    public int getAmount(Material material) {
        for (Material material1 : getDrops()) {
            if (material1.equals(material)) {
                return amount.get(material);
            }
        }
        return 1;
    }

    public void dropItem(Player p, BlockBreakEvent event) {

        CustomDropManager customDropManager = GameManager.getGameManager().getCustomDropManager();
        Block block = event.getBlock();
        Material material = block.getType();
        Location loc = block.getLocation();

        Random ran = new Random();

        event.setDropItems(false);
        event.setExpToDrop(0);

        CustomDrop customDrop = customDropManager.getCustomDrop(material);
        Material drop = customDrop.getDrops().get(ran.nextInt(customDrop.getDrops().size()));

        int exp = customDrop.getExp();
        if (exp > 0) {
            p.giveExp(exp);
            GameManager.getGameManager().getSoundManager().playOreDestroySound(p);
        }

        if (!getDrops().isEmpty() && getAmount(drop) > 0 && drop != null && !drop.equals(Material.AIR)) {
            int amount = getAmount(drop) != 0 ? ran.nextInt(getAmount(drop)) : 1;
            Bukkit.getWorld(loc.getWorld().getName()).dropItemNaturally(loc, new ItemStack(drop, amount));
        }

    }
}
