package me.florixak.uhcrun.manager;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    public static HashMap<UUID, Integer> kills = new HashMap<UUID, Integer>();

    public static ArrayList<UUID> online = new ArrayList<UUID>();
    public static ArrayList<UUID> alive = new ArrayList<UUID>();
    public static ArrayList<UUID> dead = new ArrayList<UUID>();

    public static ArrayList<UUID> creator = new ArrayList<UUID>();

    public static boolean isOnline(Player p) {return online.contains(p.getUniqueId());}
    public static boolean isAlive(Player p) {return alive.contains(p.getUniqueId());}
    public static boolean isDead(Player p) {return dead.contains(p.getUniqueId());}

    public static boolean isCreator(Player p) {
        return creator.contains(p.getUniqueId());
    }


}