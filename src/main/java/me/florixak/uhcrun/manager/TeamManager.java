package me.florixak.uhcrun.manager;

import org.bukkit.entity.Player;

import java.util.*;

public class TeamManager {

    char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
    char[] colors = "123456789abcdefl".toCharArray();

    public HashMap<UUID, String> teams = new HashMap<>();

    public void addToTeam() {
        UUID uuid = null;
        char team = 0;
        String color;
        shuffleTeamColor(colors);
        for (int i = 0; i<PlayerManager.online.size(); i++) {
            uuid = PlayerManager.online.get(i);
            team = alphabet[i];
            color = "&" + colors[i];
            teams.put(uuid, color+team);
        }

    }

    public String getTeam(Player p) {
        if (!teams.containsKey(p.getUniqueId())) return "#";
        return teams.get(p.getUniqueId());
    }

    public void shuffleTeamColor(char[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private void swap(char[] a, int i, int change) {
        char helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }
}
