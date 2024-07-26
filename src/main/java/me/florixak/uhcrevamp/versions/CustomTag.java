package me.florixak.uhcrevamp.versions;


import org.bukkit.Material;

import java.util.Set;

public interface CustomTag {
    boolean isTagged(Material material);

    Set<Material> getValues();

    Object getKey();
}
