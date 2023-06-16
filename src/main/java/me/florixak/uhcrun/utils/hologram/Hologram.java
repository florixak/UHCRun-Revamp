package me.florixak.uhcrun.utils.hologram;

import me.florixak.uhcrun.utils.TextUtils;
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

        createHologram();
    }

    public void createHologram() {

        this.hologram = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        this.hologram.setVisible(false);
        this.hologram.setCollidable(false);
        this.hologram.setInvulnerable(true);
        this.hologram.setCanPickupItems(false);
        this.hologram.setGravity(false);
        this.hologram.setBasePlate(false);
        this.hologram.setArms(false);

        this.hologram.setCustomNameVisible(true);
        this.hologram.setCustomName(TextUtils.color(text));
    }

    public String getText() {
        return this.hologram.getCustomName();
    }

    public void setText(String text) {
        this.text = text;
        this.hologram.setCustomName(TextUtils.color(this.text));
    }

    public Location getLocation() {
        return this.loc;
    }

    public void setLocation(Location loc) {
        remove();
        this.loc = loc;
        createHologram();
    }

    public void remove() {
        this.hologram.remove();
    }
}
