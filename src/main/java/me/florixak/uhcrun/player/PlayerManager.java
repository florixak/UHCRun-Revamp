package me.florixak.uhcrun.player;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.teams.UHCTeam;
import me.florixak.uhcrun.utils.RandomUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

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
    public List<UHCPlayer> getOnlineList() {
        return getPlayers().stream().filter(UHCPlayer::isOnline).collect(Collectors.toList());
    }
    public List<UHCPlayer> getAliveList() {
        return getPlayers().stream().filter(UHCPlayer::isAlive).collect(Collectors.toList());
    }
    public List<UHCPlayer> getDeadList() {
        return getPlayers().stream().filter(UHCPlayer::isDead).collect(Collectors.toList());
    }
    public List<UHCPlayer> getSpectatorList() {
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
    public UHCPlayer getRandomOnlineUHCPlayer() {
        return getOnlineList().get(RandomUtils.getRandom().nextInt(getOnlineList().size()));
    }

    public UHCPlayer getWinnerPlayer() {
        return getAliveList().stream().filter(UHCPlayer::isWinner).findFirst().orElse(null);
    }
    public void setUHCWinner() {

        if (getAliveList().isEmpty()) return;

        UHCPlayer winner = getAliveList().get(0);
        if (winner == null) return;

        for (UHCPlayer uhcPlayer : getAliveList()) {
            if (!uhcPlayer.isOnline()) return;
            if (uhcPlayer.getKills() > winner.getKills()) {
                winner = uhcPlayer;
            }
        }
        if (GameValues.TEAM_MODE) {
            for (UHCPlayer teamMember : winner.getTeam().getMembers()) {
                teamMember.setWinner(true);
            }
            return;
        }
        winner.setWinner(true);
    }
    public String getUHCWinner() {
        if (GameValues.TEAM_MODE) {
            UHCTeam winnerTeam = gameManager.getTeamManager().getWinnerTeam();
            return winnerTeam != null ? (winnerTeam.getMembers().size() == 1 ? winnerTeam.getMembers().get(0).getName() : winnerTeam.getName()) : "None";
        }
        return getWinnerPlayer() != null ? getWinnerPlayer().getName() : "None";
    }

    private List<UHCPlayer> findTopKillers(List<UHCPlayer> players) {
        players.sort((uhcPlayer1, uhcPlayer2) -> Integer.compare(uhcPlayer2.getKills(), uhcPlayer1.getKills()));
        return players;
    }
    public List<UHCPlayer> getTopKillers() {
        return findTopKillers(getPlayers());
    }

    public int getMaxPlayers() {
        return GameValues.TEAM_SIZE * gameManager.getTeamManager().getTeams().size();
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

        uhcPlayer.clearInventory();

        if (GameValues.TEAM_MODE && !uhcPlayer.hasTeam()) {
            gameManager.getTeamManager().joinRandomTeam(uhcPlayer);
        } else if (!GameValues.TEAM_MODE) {
            UHCTeam uhcTeam = new UHCTeam(null, "", "&f", 1);
            gameManager.getTeamManager().addTeam(uhcTeam);
            uhcPlayer.setTeam(uhcTeam);
        }

        if (uhcPlayer.hasKit()) {
            uhcPlayer.getKit().giveKit(uhcPlayer);
        }
    }
    public void setSpectator(UHCPlayer uhcPlayer, PlayerState pState) {
        Player p = uhcPlayer.getPlayer();
        Location playerLoc = p.getLocation();

        uhcPlayer.setState(pState);

        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        uhcPlayer.clearInventory();

        p.setGameMode(GameMode.SPECTATOR);

        p.teleport(new Location(
                Bukkit.getWorld(playerLoc.getWorld().getName()),
                playerLoc.getX()+0,
                playerLoc.getY()+10,
                playerLoc.getZ()+0));
    }

    public void onDisable() {
        this.players.clear();
    }
}