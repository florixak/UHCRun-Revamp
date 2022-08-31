package me.florixak.uhcrun.manager;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class VoteManager {

    public static ArrayList<UUID> hp = new ArrayList<UUID>();
    public static ArrayList<UUID> ran_drops = new ArrayList<UUID>();
    public static ArrayList<UUID> no_ench = new ArrayList<UUID>();
    public static ArrayList<UUID> no_pots = new ArrayList<UUID>();

    public static boolean votedHP(Player p) {
        return hp.contains(p.getUniqueId());
    }
    public static boolean votedRanDrops(Player p) {
        return ran_drops.contains(p.getUniqueId());
    }
    public static boolean votedNoEnch(Player p) {
        return no_ench.contains(p.getUniqueId());
    }
    public static boolean votedNoPots(Player p) {
        return no_pots.contains(p.getUniqueId());
    }
}
