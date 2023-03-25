package me.florixak.uhcrun.manager;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    public static HashMap<UUID, Integer> kills = new HashMap<>();

    public static ArrayList<UUID> online = new ArrayList<>();
    public static ArrayList<UUID> alive = new ArrayList<>();
    public static ArrayList<UUID> spectators = new ArrayList<>();

    public static ArrayList<UUID> creator = new ArrayList<>();

    public static boolean isOnline(Player p) {return online.contains(p.getUniqueId());}
    public static boolean isAlive(Player p) {return alive.contains(p.getUniqueId());}
    public static boolean isSpectator(Player p) {return spectators.contains(p.getUniqueId());}

    public static boolean isCreator(Player p) {
        return creator.contains(p.getUniqueId());
    }


}