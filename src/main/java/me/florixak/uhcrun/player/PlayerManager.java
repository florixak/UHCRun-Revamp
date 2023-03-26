package me.florixak.uhcrun.player;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private List<UHCPlayer> players;
    private List<UHCPlayer> alive;
    private List<UHCPlayer> dead;
    private List<UHCPlayer> creators; // TODO creator

    public PlayerManager() {
        this.players = new ArrayList<>();
        this.alive = new ArrayList<>();
        this.dead = new ArrayList<>();
        this.creators = new ArrayList<>();
    }

    public void addPlayer(UHCPlayer p) {
        if (players.contains(p)) return;
        this.players.add(p);
    }

    public void removePlayer(UHCPlayer p) {
        if (!players.contains(p)) return;
        this.players.remove(p);
    }

    public List<UHCPlayer> getPlayers() {
        return this.players;
    }

    public void addAlive(UHCPlayer p) {
        if (alive.contains(p)) return;
        alive.add(p);
    }

    public void removeAlive(UHCPlayer p) {
        if (!alive.contains(p)) return;
        alive.remove(p);
    }

    public List<UHCPlayer> getAlive() {
        return this.alive;
    }

    public void addDead(UHCPlayer p) {
        if (dead.contains(p)) return;
        dead.add(p);
    }

    public List<UHCPlayer> getDead() {
        return this.alive;
    }

    public UHCPlayer newUHCPlayer(Player p) {
        return new UHCPlayer(p.getUniqueId(), p.getName());
    }

    public UHCPlayer getUHCPlayer(UUID uuid) {
        for (UHCPlayer uhcPlayer : getPlayers()) {
            if (uhcPlayer.getUUID().equals(uuid)) {
                return uhcPlayer;
            }
        }
        return null;
    }

    public void clearAlive() {
        this.alive.clear();
    }
    public void clearPlayers() {
        this.players.clear();
    }
    public void clearDead() {
        this.dead.clear();
    }

}