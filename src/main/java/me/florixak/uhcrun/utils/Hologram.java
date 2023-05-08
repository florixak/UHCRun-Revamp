package me.florixak.uhcrun.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {

    private ArmorStand hologram;
    private String text;
    private Location loc;

    public Hologram(String text, Location loc) {
        this.text = text;
        this.loc = loc;
    }

    public void createHologram() {

        this.hologram = (ArmorStand) Bukkit.getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.ARMOR_STAND);
        this.hologram.setVisible(false);
        this.hologram.setCollidable(false);
        this.hologram.setInvulnerable(true);
        this.hologram.setCanPickupItems(false);
        this.hologram.setCustomNameVisible(true);
        this.hologram.setCustomName(TextUtils.color(text));
        this.hologram.setGravity(false);
        this.hologram.setBasePlate(false);
        this.hologram.setArms(false);
    }

    public void removeHologram() {
        this.hologram.remove();
    }
}
