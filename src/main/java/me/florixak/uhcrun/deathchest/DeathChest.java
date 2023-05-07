package me.florixak.uhcrun.deathchest;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.tasks.DeathChestExpire;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class DeathChest {

    private Chest chest;
    private Location loc;
    private String title;
    private ItemStack[] contents;
    boolean empty;

    private DeathChestExpire deathChestExpire;

    public DeathChest(Location loc, String title, ItemStack[] contents) {

        this.loc = loc;
        this.title = title;
        this.contents = contents;

        if (this.loc == null) {
            return;
        }

        if (this.title.isEmpty() || this.title == null) {
            this.title = "Death Chest";
        }

        if (this.contents.length <= 0 || this.contents == null) {
            this.empty = true;
            return;
        }

        createChest();
    }

    public void createChest() {

        Bukkit.getWorld(loc.getWorld().getName()).getBlockAt(loc).setType(XMaterial.CHEST.parseMaterial());
        BlockState state = loc.getBlock().getState();

        this.chest = (Chest) state;

        chest.setCustomName(TextUtils.color(this.title));
        for (ItemStack item : getContents()) {
            chest.getBlockInventory().addItem(item);
        }
        startExpire();
    }

    public void startExpire() {
        this.deathChestExpire = new DeathChestExpire(this);
        this.deathChestExpire.runTaskTimer(UHCRun.getInstance(), 20L, 20L);
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public void remove() {
        Bukkit.getWorld(loc.getWorld().getName()).getBlockAt(loc).setType(XMaterial.AIR.parseMaterial());
    }

    public boolean isEmpty() {
        return this.chest.getBlockInventory().isEmpty();
    }
}
