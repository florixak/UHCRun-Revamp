package me.florixak.uhcrun.perks;

import me.florixak.uhcrun.utils.XSeries.XPotion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class PerksManager {

    public static HashMap<UUID, PerkType> perks = new HashMap<>();

    public static void givePerk(Player p) {
        p.sendMessage("Received " + getPerk(p.getUniqueId()) + " kit");

        switch (perks.get(p.getUniqueId())) {
            case NONE:
                break;
            case STRENGTH:
                p.addPotionEffect(XPotion.INCREASE_DAMAGE.buildPotionEffect(100, 1));
                break;
            case JUMP_BOOST:
                p.addPotionEffect(XPotion.JUMP.buildPotionEffect(200, 2));
                break;
            case REGENERATION:
                p.addPotionEffect(XPotion.REGENERATION.buildPotionEffect(100, 2));
                break;
            case SPEED:
                p.addPotionEffect(XPotion.SPEED.buildPotionEffect(100, 2));
                break;
            case FIRE_RESISTANCE:
                p.addPotionEffect(XPotion.FIRE_RESISTANCE.buildPotionEffect(100, 2));
                break;
            case ENDER_PEARL:
                p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                break;
            case RESISTANCE:
                p.addPotionEffect(XPotion.DAMAGE_RESISTANCE.buildPotionEffect(100, 2));
                break;
            case INVISIBLE:
                p.addPotionEffect(XPotion.INVISIBILITY.buildPotionEffect(100, 2));
                break;
            default:
                p.sendMessage("ERROR with perks");
                break;
        }
    }

    public static void registerPerk(Player p, PerkType perk) {
        // add security for reregister perk

        perks.remove(p.getUniqueId());
        perks.put(p.getUniqueId(), perk);
    }

    public static PerkType getPerk(UUID uuid) {
        return perks.get(uuid);
    }

    private static void getStrength(Player p) {

    }


}