package me.florixak.uhcrevamp.game.deathchest;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.hologram.Hologram;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.tasks.DeathChestExpire;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathChest {

    private final UHCPlayer uhcPlayer;
    private final Location loc;
    private List<ItemStack> contents;

    private Hologram hologram;

    private final boolean expire;
    private final DeathChestExpire deathChestExpire;

    public DeathChest(UHCPlayer uhcPlayer, Location loc, List<ItemStack> contents, boolean expire) {
        this.uhcPlayer = uhcPlayer;
        this.loc = loc;
        this.contents = contents;

        this.deathChestExpire = new DeathChestExpire(this);
        this.expire = expire;
        createChest();
    }

    public void createChest() {
        World world = loc.getWorld();
        world.getBlockAt(loc).setType(XMaterial.CHEST.parseMaterial());
        BlockState state = loc.getBlock().getState();

        Chest chest = (Chest) state;
        //chest.setCustomName(TextUtils.color(this.title));
        setHologram();

        if (getContents() != null && !getContents().isEmpty()) {
            for (ItemStack item : getContents()) {
                chest.getInventory().addItem(item);
            }
        }

        if (canExpire()) {
            startExpiring();
        }
    }

    public UHCPlayer getPlayer() {
        return this.uhcPlayer;
    }

    public boolean canExpire() {
        return this.expire;
    }

    public void startExpiring() {
        this.deathChestExpire.runTaskTimer(UHCRevamp.getInstance(), 0L, 20L);
    }

    public DeathChestExpire getExpireTask() {
        return this.deathChestExpire;
    }

    public String getFormattedExpireTime() {
        return TimeUtils.getFormattedTime(getExpireTask().getExpireTime());
    }

    public int getExpireTime() {
        return getExpireTask().getExpireTime();
    }

    public List<ItemStack> getContents() {
        return this.contents;
    }

    public void setHologram() {
        String text = GameValues.DEATH_CHEST.HOLOGRAM_TEXT
                .replace("%player%", uhcPlayer.getName())
                .replace("%countdown%", getFormattedExpireTime());

        this.hologram = new Hologram(text);
        this.hologram.spawn(loc);
    }

    public Hologram getHologram() {
        return this.hologram;
    }

    public void removeChest() {
        if (getExpireTask() != null) {
            getExpireTask().cancel();
        }

        getHologram().remove();

        Block block = Bukkit.getWorld(loc.getWorld().getName()).getBlockAt(loc.add(0.5, 1, 0.5));
        block.setType(XMaterial.AIR.parseMaterial());


        // TODO create explode

        /*for (ItemStack itemStack : getContents()) {
            if (itemStack == null || itemStack.equals(XMaterial.AIR.parseMaterial())) return;
            Location location = loc.add(0.5, 0.5, 0.5);
            Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, itemStack);
        }*/
    }
}