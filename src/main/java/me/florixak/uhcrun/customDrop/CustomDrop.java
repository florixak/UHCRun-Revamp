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

    private Material block;
    private List<Material> drops;
    private HashMap<Material, Integer> amount;
    private int exp;

    public CustomDrop(Material block, List<Material> drops, HashMap<Material, Integer> amount, int exp) {
        this.block = block;
        this.drops = drops;
        this.amount = amount;
        this.exp = exp;
    }

    public Material getBlock() {
        return block;
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
        Block block = event.getBlock();
        Location loc = block.getLocation();
        CustomDropManager customDropManager = GameManager.getGameManager().getCustomDropManager();
        Random ran = new Random();

        if (customDropManager.hasCustomDrop(block.getType())) {

            event.setDropItems(false);
            event.setExpToDrop(0);

            CustomDrop customDrop = customDropManager.getCustomDrop(block.getType());
            Material drop = customDrop.getDrops().get(ran.nextInt(customDrop.getDrops().size()));
            int amount = ran.nextInt(getAmount(drop))+1;
            int exp = customDrop.getExp();

            if (exp > 0) {
                p.giveExp(exp);
                GameManager.getGameManager().getSoundManager().playOreDestroySound(p);
            }

            if (amount > 0 && drop != null && drop != Material.AIR) {
                Bukkit.getWorld(loc.getWorld().getName()).dropItemNaturally(loc, new ItemStack(drop, amount));
            }
        }
    }
}
