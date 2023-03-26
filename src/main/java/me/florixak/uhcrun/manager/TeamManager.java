package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamManager {

    private UHCRun plugin;

    private char[] alphabet;
    private char[] colors;

    public HashMap<UHCPlayer, String> teams;

    public TeamManager(UHCRun plugin) {
        this.plugin = plugin;

        this.alphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
        this.colors = "123456789abcdefl".toCharArray();
        this.teams = new HashMap<>();
    }

    public void addToTeam() {
        UHCPlayer uhcPlayer = null;
        char team = 0;
        String color;
        shuffleTeamColor(colors);
        for (int i = 0; i < plugin.getPlayerManager().getPlayers().size(); i++) {
            uhcPlayer = plugin.getPlayerManager().getPlayers().get(i);
            team = alphabet[i];
            color = "&" + colors[i];
            teams.put(uhcPlayer, color+team);
            uhcPlayer.setTeam(teams.get(uhcPlayer));
        }
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

    public void onDisable() {
        teams.clear();
    }
}
