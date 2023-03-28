package me.florixak.uhcrun.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private List<UHCPlayer> players;
    private List<UHCPlayer> alive;
    private List<UHCPlayer> dead; // TODO rožlišoat speactatory of dead hráčů
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

    public List<UHCPlayer> getPlayersList() {
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

    public List<UHCPlayer> getAliveList() {
        return this.alive;
    }

    public void addDead(UHCPlayer p) {
        if (dead.contains(p)) return;
        dead.add(p);
    }

    public List<UHCPlayer> getDeadList() {
        return this.dead;
    }

    public UHCPlayer newUHCPlayer(Player p) {
        return new UHCPlayer(p.getUniqueId(), p.getName());
    }

    public UHCPlayer getUHCPlayer(UUID uuid) {
        for (UHCPlayer uhcPlayer : getPlayersList()) {
            if (uhcPlayer.getUUID().equals(uuid)) {
                return uhcPlayer;
            }
        }
        return null;
    }

    public void clearPlayerInventory(Player player) {
        player.getInventory().clear();

        //clear player armor
        ItemStack[] emptyArmor = new ItemStack[4];
        for(int i=0 ; i<emptyArmor.length ; i++){
            emptyArmor[i] = new ItemStack(Material.AIR);
        }
        player.getInventory().setArmorContents(emptyArmor);

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