package me.florixak.uhcrun.player;

import me.florixak.uhcrun.utils.TeleportUtils;
import org.bukkit.*;
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

    public UHCPlayer getWinner() {
        for (UHCPlayer p : getAliveList()) {
            if (!p.isOnline()) return null;
            return p;
        }
        return null;
    }
    public String getWinnerName() {
        if (getWinner() == null) return "NONE";
        return getWinner().getName();
    }

    public void setPlayersForGame(UHCPlayer uhcPlayer) {

        uhcPlayer.setState(PlayerState.PLAYING);
        addAlive(uhcPlayer);

        uhcPlayer.getPlayer().getInventory().clear();
        uhcPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
        uhcPlayer.getPlayer().setHealth(uhcPlayer.getPlayer().getHealthScale());
        uhcPlayer.getPlayer().setFoodLevel(20);

        // wereAlive = PlayerManager.alive.size();
    }

    public void setSpectator(UHCPlayer uhcPlayer) {

        Location playerLoc = uhcPlayer.getPlayer().getLocation();

        uhcPlayer.setState(PlayerState.DEAD);

        uhcPlayer.getPlayer().setAllowFlight(true);
        uhcPlayer.getPlayer().setFlying(true);

        uhcPlayer.getPlayer().setGameMode(GameMode.SPECTATOR);

        uhcPlayer.getPlayer().teleport(new Location(
                Bukkit.getWorld(playerLoc.getWorld().getName()),
                playerLoc.getX()+0,
                playerLoc.getY()+10,
                playerLoc.getZ()+0));

    }

    public void teleportPlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(TeleportUtils.teleportToSafeLocation()));
    }

    public void teleportPlayersAfterMining(UHCPlayer uhcPlayer) {
        Player p = uhcPlayer.getPlayer();
        double x, y, z;

        World world = Bukkit.getWorld(p.getLocation().getWorld().getName());
        x = p.getLocation().getX();
        y = 150;
        z = p.getLocation().getZ();

        Location location = new Location(world, x, y, z);
        y = location.getWorld().getHighestBlockYAt(location);
        location.setY(y);

        p.teleport(location);
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