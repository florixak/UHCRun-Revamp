package me.florixak.uhcrun.manager;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PerksManager {


    public static ArrayList<UUID> noPerk = new ArrayList<UUID>();
    public static ArrayList<UUID> strength = new ArrayList<UUID>();
    public static ArrayList<UUID> regeneration = new ArrayList<UUID>();
    public static ArrayList<UUID> speed = new ArrayList<UUID>();
    public static ArrayList<UUID> invisible = new ArrayList<UUID>();
    public static ArrayList<UUID> fire_resistance = new ArrayList<UUID>();
    public static ArrayList<UUID> ender_pearl = new ArrayList<UUID>();
    public static ArrayList<UUID> resistance = new ArrayList<UUID>();

    public static boolean haveNoPerk(Player p) {
        return noPerk.contains(p.getUniqueId());
    }
    public static boolean haveStrength(Player p) {
        return strength.contains(p.getUniqueId());
    }
    public static boolean haveRegeneration(Player p) {
        return regeneration.contains(p.getUniqueId());
    }
    public static boolean haveSpeed(Player p) {
        return speed.contains(p.getUniqueId());
    }
    public static boolean haveInvisible(Player p) {
        return invisible.contains(p.getUniqueId());
    }
    public static boolean haveFireResistance(Player p) {
        return fire_resistance.contains(p.getUniqueId());
    }
    public static boolean haveEnderPearl(Player p) {
        return ender_pearl.contains(p.getUniqueId());
    }
    public static boolean haveResistance(Player p) {
        return resistance.contains(p.getUniqueId());
    }

    public static void disbandPerks(Player p) {
        if (haveStrength(p)) strength.remove(p.getUniqueId());
        if (haveRegeneration(p)) regeneration.remove(p.getUniqueId());
        if (haveSpeed(p)) speed.remove(p.getUniqueId());
        if (haveInvisible(p)) invisible.remove(p.getUniqueId());
        if (haveFireResistance(p)) fire_resistance.remove(p.getUniqueId());
        if (haveEnderPearl(p)) ender_pearl.remove(p.getUniqueId());
        if (haveResistance(p)) resistance.remove(p.getUniqueId());
        if (!haveNoPerk(p)) noPerk.add(p.getUniqueId());
    }



}