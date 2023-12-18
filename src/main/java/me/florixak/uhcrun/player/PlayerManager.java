package me.florixak.uhcrun.player;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.teams.UHCTeam;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
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
        this.players.remove(p);
    }

    public List<UHCPlayer> getPlayers() {
        return this.players;
    }
    public List<UHCPlayer> getOnlinePlayers() {
        return getPlayers().stream().filter(UHCPlayer::isOnline).collect(Collectors.toList());
    }
    public List<UHCPlayer> getAlivePlayers() {
        return getPlayers().stream().filter(UHCPlayer::isAlive).collect(Collectors.toList());
    }
    public List<UHCPlayer> getDeadPlayers() {
        return getPlayers().stream().filter(UHCPlayer::isDead).collect(Collectors.toList());
    }
    public List<UHCPlayer> getSpectators() {
        return getPlayers().stream().filter(UHCPlayer::isSpectator).collect(Collectors.toList());
    }

    public UHCPlayer getUHCPlayer(UUID uuid) {
        return getPlayers().stream().filter(uhcPlayer -> uhcPlayer.getUUID().equals(uuid)).findFirst().orElse(null);
    }
    public UHCPlayer getUHCPlayer(String name) {
        return getPlayers().stream().filter(uhcPlayer -> uhcPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    public UHCPlayer getOrCreateUHCPlayer(UUID uuid) {
        return getPlayers().stream().filter(uhcPlayer -> uhcPlayer.getUUID().equals(uuid)).findFirst().orElse(new UHCPlayer(uuid, Bukkit.getPlayer(uuid).getName()));
    }

    public UHCPlayer getWinnerPlayer() {
        return getAlivePlayers().stream().filter(UHCPlayer::isWinner).findFirst().orElse(null);
    }
    private List<UHCPlayer> findTopKillers(List<UHCPlayer> players) {
        Collections.sort(players, (uhcPlayer1, uhcPlayer2) -> Integer.compare(uhcPlayer2.getKills(), uhcPlayer1.getKills()));
        return players;
    }
    public List<UHCPlayer> getTopKillers() {
        return findTopKillers(getPlayers());
    }

    public void readyPlayer(UHCPlayer uhcPlayer) {
        Player p = uhcPlayer.getPlayer();

        if (uhcPlayer.hasKit()) {
            uhcPlayer.getData().withdrawMoney(uhcPlayer.getKit().getCost());
        }

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
    public void setSpectator(UHCPlayer uhcPlayer, PlayerState pState) {
        Player p = uhcPlayer.getPlayer();
        Location playerLoc = p.getLocation();

        uhcPlayer.setState(pState);

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
        for(int i = 0; i < emptyArmor.length; i++){
            emptyArmor[i] = new ItemStack(Material.AIR);
        }
        p.getInventory().setArmorContents(emptyArmor);
    }

    public void onDisable() {
        this.players.clear();
    }
}