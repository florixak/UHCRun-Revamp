package me.florixak.uhcrun.game.customDrop;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class CustomDrop {

    private final Material material;
    private final Entity entity;
    private List<Material> drops;
    private final int minAmount;
    private final int maxAmount;
    private final int exp;

    public CustomDrop(Material material,
                      Entity entity,
                      List<Material> drops,
                      int minAmount,
                      int maxAmount,
                      int exp) {
        this.material = material;
        this.entity = entity;
        this.drops = drops;

        this.minAmount = minAmount;
        this.maxAmount = maxAmount;

        this.exp = exp;
    }

    public Material getMaterial() {
        return material;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean hasDrops() {
        return !getDrops().isEmpty() && getDrops() != null;
    }

    public List<Material> getDrops() {
        return this.drops;
    }

    public int getExp() {
        return this.exp;
    }

    public int getMinAmount() {
        return this.minAmount;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public void dropItem(BlockBreakEvent block_event, EntityDeathEvent death_event) {

        if (block_event != null) {
            Player p = block_event.getPlayer();
            Block block = block_event.getBlock();
            Location loc = block.getLocation();

            Random ran = new Random();

            int exp = getExp();
            if (exp > 0) {
                p.giveExp(exp);
                GameManager.getGameManager().getSoundManager().playOreDestroySound(p);
            }

            if (hasDrops()) {
                block.setType(XMaterial.AIR.parseMaterial());
                block_event.setDropItems(false);
                block_event.setExpToDrop(0);

                Material drop = getDrops().get(ran.nextInt(getDrops().size()));
                if (drop != XMaterial.AIR.parseMaterial()) {
                    int amount = getMinAmount() + (int)(Math.random() * ((getMaxAmount()-getMinAmount())+1));
                    ItemStack dropItem = new ItemStack(drop, amount);

                    Location location = loc.add(0.5, 0.5, 0.5);
                    Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, dropItem);
                }
            }
        }
    }
}
