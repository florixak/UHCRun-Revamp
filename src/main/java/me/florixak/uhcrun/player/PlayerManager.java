package me.florixak.uhcrun.player;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.lobby.LobbyType;
import me.florixak.uhcrun.teams.UHCTeam;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager {

    private final GameManager gameManager;
    private List<UHCPlayer> players;

    public PlayerManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.players = new ArrayList<>();
    }

    public void addPlayer(UHCPlayer p) {
        if (players.contains(p)) return;
        this.players.add(p);
    }

    public void removePlayer(UHCPlayer p) {
        if (!players.contains(p)) return;

        if (p.hasTeam()) {
            p.getTeam().leave(p);
        }
        if (p.hasKit()) {
            p.setKit(null);
        }
        if (p.hasPerk()) {
            p.setPerk(null);
        }

        this.players.remove(p);
    }

    public List<UHCPlayer> getPlayers() {
        return this.players;
    }

    public List<UHCPlayer> getAlivePlayers() {
        return getPlayers().stream().filter(uhcPlayer -> uhcPlayer.isAlive()).collect(Collectors.toList());
    }

    public List<UHCPlayer> getDeadPlayers() {
        return getPlayers().stream().filter(uhcPlayer -> uhcPlayer.isDead()).collect(Collectors.toList());
    }

    public List<UHCPlayer> getSpectators() {
        return getPlayers().stream().filter(uhcPlayer -> uhcPlayer.isSpectator()).collect(Collectors.toList());
    }

    public UHCPlayer getUHCPlayer(UUID uuid) {
        for (UHCPlayer uhcPlayer : getPlayers()) {
            if (uhcPlayer.getUUID().equals(uuid)) {
                return uhcPlayer;
            }
        }
        return null;
    }

    public UHCPlayer getUHCPlayer(String name) {
        for (UHCPlayer uhcPlayer : getPlayers()) {
            if (uhcPlayer.getName().equals(name)) {
                return uhcPlayer;
            }
        }
        return null;
    }

    public UHCPlayer getOrCreateHOCPlayer(UUID uuid) {
        for (UHCPlayer hocPlayer : getPlayers()) {
            if (hocPlayer.getUUID().equals(uuid)) {
                return hocPlayer;
            }
        }
        return new UHCPlayer(uuid, Bukkit.getPlayer(uuid).getName());
    }

    public UHCPlayer getWinnerPlayer() {
        for (UHCPlayer uhcPlayer : getAlivePlayers()) {
            if (uhcPlayer.isWinner()) return uhcPlayer;
        }
        return null;
    }

    private List<UHCPlayer> findTopKillers(List<UHCPlayer> players) {
        Collections.sort(players, (uhcPlayer1, uhcPlayer2) -> Integer.compare(uhcPlayer2.getKills(), uhcPlayer1.getKills()));
        return players;
    }

    public List<UHCPlayer> getTopKillers() {
        List<UHCPlayer> topKillers = findTopKillers(getPlayers());
        return topKillers;
    }

    public void readyPlayerForGame(UHCPlayer uhcPlayer) {
        Player p = uhcPlayer.getPlayer();

        uhcPlayer.getData().withdrawMoney(uhcPlayer.getKit().getCost());

        uhcPlayer.setState(PlayerState.ALIVE);

        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);

        clearPlayerInventory(p);

        if (gameManager.isTeamMode() && !uhcPlayer.hasTeam()) {
            gameManager.getTeamManager().joinRandomTeam(uhcPlayer);
        } else if (!gameManager.isTeamMode()){
            UHCTeam uhcTeam = new UHCTeam(null, "", "&f", 1);
            gameManager.getTeamManager().addTeam(uhcTeam);
            uhcPlayer.setTeam(uhcTeam);
        }

        if (uhcPlayer.hasKit()) {
            uhcPlayer.getKit().getKit(uhcPlayer);
        }
    }

    public void setSpectator(UHCPlayer uhcPlayer) {
        Player p = uhcPlayer.getPlayer();
        Location playerLoc = p.getLocation();

        uhcPlayer.setState(PlayerState.DEAD);

        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        clearPlayerInventory(p);

        p.setGameMode(GameMode.SPECTATOR);

        p.teleport(new Location(
                Bukkit.getWorld(playerLoc.getWorld().getName()),
                playerLoc.getX()+0,
                playerLoc.getY()+10,
                playerLoc.getZ()+0));
    }

    public void clearPlayerInventory(Player p) {
        p.getInventory().clear();

        //clear player armor
        ItemStack[] emptyArmor = new ItemStack[4];
        for(int i=0 ; i<emptyArmor.length ; i++){
            emptyArmor[i] = new ItemStack(Material.AIR);
        }
        p.getInventory().setArmorContents(emptyArmor);
    }

    public void onDisable() {
        this.players.clear();
    }
}