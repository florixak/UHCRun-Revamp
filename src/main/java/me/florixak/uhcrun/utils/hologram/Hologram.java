package me.florixak.uhcrun.utils.hologram;

import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {

    private ArmorStand hologram;
    private String[] lines;
    private Location loc;

    public Hologram(String... lines) {
        this.lines = lines;
    }

    public void spawn(Location loc) {
        for (String line : lines) {
            this.hologram = loc.getWorld().spawn(loc, ArmorStand.class);

            this.hologram.setVisible(false);
            this.hologram.setCollidable(false);
            this.hologram.setCanPickupItems(false);
            this.hologram.setGravity(false);
            this.hologram.setBasePlate(false);
            this.hologram.setArms(false);

            this.hologram.setInvulnerable(true);
            this.hologram.setCustomNameVisible(true);

            this.hologram.setCustomName(TextUtils.color(line));

            loc.subtract(0, 0.25, 0);
        }
    }

    public String getText() {
        return this.hologram.getCustomName();
    }

    public void setText(String... lines) {
        this.lines = lines;
        for (String line : lines) {
            this.hologram.setCustomName(TextUtils.color(line));
        }
    }

    public Location getLocation() {
        return this.loc;
    }

    public void setLocation(Location loc) {
        remove();
        this.loc = loc;
        spawn(loc);
    }

    public void remove() {
        this.hologram.remove();
    }
}
