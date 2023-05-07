package me.florixak.uhcrun.deathchest;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.tasks.DeathChestExpire;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathChest {

    private Chest chest;
    private Location loc;
    private String title;
    private List<ItemStack> contents;

    private DeathChestExpire deathChestExpire;

    public DeathChest(Location loc, String title, List<ItemStack> contents) {

        this.loc = loc;
        this.title = title;
        this.contents = contents;

        if (this.loc == null) {
            return;
        }

        if (this.title.isEmpty() || this.title == null) {
            this.title = "Death Chest";
        }

        createChest();
    }

    public void createChest() {

        Bukkit.getWorld(loc.getWorld().getName()).getBlockAt(loc).setType(XMaterial.CHEST.parseMaterial());
        BlockState state = loc.getBlock().getState();

        this.chest = (Chest) state;

        chest.setCustomName(TextUtils.color(this.title));
        if (!getContents().isEmpty()) {
            for (ItemStack item : getContents()) {
                chest.getInventory().addItem(item);
            }
        }
        if (GameManager.getGameManager().getDeathChestManager().willExpire()) {
            startExpire();
        }

    }

    public void startExpire() {
        this.deathChestExpire = new DeathChestExpire(this);
        this.deathChestExpire.runTaskTimer(UHCRun.getInstance(), 20L, 20L);
    }

    public List<ItemStack> getContents() {
        return this.contents;
    }

    public void remove() {
        Bukkit.getWorld(loc.getWorld().getName()).getBlockAt(loc).setType(XMaterial.AIR.parseMaterial());
    }
}
