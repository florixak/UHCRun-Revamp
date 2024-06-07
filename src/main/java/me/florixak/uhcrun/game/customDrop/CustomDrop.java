package me.florixak.uhcrun.game.customDrop;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.RandomUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomDrop {

    private final Material material;
    private final EntityType entityType;
    private List<Material> drops;
    private final int minAmount;
    private final int maxAmount;
    private final int exp;

    public CustomDrop(Material material,
                      List<Material> drops,
                      int minAmount,
                      int maxAmount,
                      int exp) {
        this.material = material;
        this.entityType = null;
        this.drops = drops;

        this.minAmount = minAmount;
        this.maxAmount = maxAmount;

        this.exp = exp;
    }

    public CustomDrop(EntityType entityType,
                      List<Material> drops,
                      int minAmount,
                      int maxAmount,
                      int exp) {
        this.entityType = entityType;
        this.material = null;
        this.drops = drops;

        this.minAmount = minAmount;
        this.maxAmount = maxAmount;

        this.exp = exp;
    }

    public Material getMaterial() {
        return material;
    }

    public EntityType getEntityType() {
        return entityType;
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

    public void dropBlockItem(BlockBreakEvent breakEvent) {
        if (breakEvent != null) {
            Player p = breakEvent.getPlayer();
            Block block = breakEvent.getBlock();
            Location loc = block.getLocation();

            int exp = getExp();
            if (exp > 0) {
                p.giveExp(exp);
                GameManager.getGameManager().getSoundManager().playOreDestroySound(p);
            }

            if (hasDrops()) {
                block.setType(XMaterial.AIR.parseMaterial());
                breakEvent.setDropItems(false);
                breakEvent.setExpToDrop(0);

                Material drop = getDrops().get(RandomUtils.getRandom().nextInt(getDrops().size()));
                if (drop != XMaterial.AIR.parseMaterial()) {

                    // int amount = getMinAmount() == getMaxAmount() ? getMinAmount() : getMinAmount() + (int)(Math.random() * ((getMaxAmount()-getMinAmount())+1));
                    int amount = getMinAmount() == getMaxAmount() ? getMinAmount() : RandomUtils.randomInteger(getMinAmount(), getMaxAmount());
                    ItemStack dropItem = new ItemStack(drop, amount);

                    Location location = loc.add(0.5, 0.5, 0.5);
                    Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, dropItem);
                }
            }
        }
    }

    public void dropBlockItem(Block block) {
        if (block != null) {
            Location loc = block.getLocation();

            if (hasDrops()) {
                block.setType(XMaterial.AIR.parseMaterial());

                Material drop = getDrops().get(RandomUtils.getRandom().nextInt(getDrops().size()));
                if (drop != XMaterial.AIR.parseMaterial()) {

                    // int amount = getMinAmount() == getMaxAmount() ? getMinAmount() : getMinAmount() + (int)(Math.random() * ((getMaxAmount()-getMinAmount())+1));
                    int amount = getMinAmount() == getMaxAmount() ? getMinAmount() : RandomUtils.randomInteger(getMinAmount(), getMaxAmount());
                    ItemStack dropItem = new ItemStack(drop, amount);

                    Location location = loc.add(0.5, 0.5, 0.5);
                    Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, dropItem);
                }
            }
        }
    }

    public void dropMobItem(EntityDeathEvent deathEvent) {
        if (deathEvent != null) {
            Player p = deathEvent.getEntity().getKiller();
            Entity entity = deathEvent.getEntity();
            Location loc = entity.getLocation();

            if (hasDrops()) {
                deathEvent.getDrops().clear();
                deathEvent.setDroppedExp(0);
                p.giveExp(getExp());

                Material drop = getDrops().get(RandomUtils.getRandom().nextInt(getDrops().size()));
                if (drop != XMaterial.AIR.parseMaterial()) {

                    //int amount = getMinAmount() == getMaxAmount() ? getMinAmount() : getMinAmount() + (int)(Math.random() * ((getMaxAmount()-getMinAmount())+1));
                    int amount = getMinAmount() == getMaxAmount() ? getMinAmount() : RandomUtils.randomInteger(getMinAmount(), getMaxAmount());
                    ItemStack dropItem = new ItemStack(drop, amount);

                    Location location = loc.add(0.5, 0.5, 0.5);
                    Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, dropItem);
                }
            }
        }
    }
}
