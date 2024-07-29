package me.florixak.uhcrevamp.game.player;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.statistics.TopStatistics;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import me.florixak.uhcrevamp.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerManager {

    private final GameManager gameManager;
    private final List<UHCPlayer> players;

    public PlayerManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.players = new ArrayList<>();
    }

    public boolean doesPlayerExist(Player player) {
        if (getUHCPlayer(player.getUniqueId()) != null) return true;
        return false;
    }

    public UHCPlayer getUHCPlayer(UUID uuid) {
        for (UHCPlayer uhcPlayer : getPlayersList()) {
            if (uhcPlayer.getUUID().equals(uuid)) {
                return uhcPlayer;
            }
        }
        return null;
    }

    public UHCPlayer getUhcPlayer(String name) {
        for (UHCPlayer uhcPlayer : getPlayersList()) {
            if (uhcPlayer.getName().equals(name)) {
                return uhcPlayer;
            }
        }
        return null;
    }


    public UHCPlayer getUHCPlayer(Player player) {
        return getUHCPlayer(player.getUniqueId());
    }

    public UHCPlayer getOrCreateUHCPlayer(Player player) {
        if (doesPlayerExist(player)) {
            return getUHCPlayer(player);
        } else {
            return newUHCPlayer(player);
        }
    }

    public synchronized UHCPlayer newUHCPlayer(Player player) {
        return newUHCPlayer(player.getUniqueId(), player.getName());
    }

    public synchronized UHCPlayer newUHCPlayer(UUID uuid, String name) {
        UHCPlayer newPlayer = new UHCPlayer(uuid, name);
        getPlayersList().add(newPlayer);
        return newPlayer;
    }

    public List<UHCPlayer> getPlayers() {
        return this.players;
    }

    public Set<UHCPlayer> getAlivePlayers() {
        return players.stream()
                .filter(UHCPlayer::isAlive)
                .filter(UHCPlayer::isOnline)
                .collect(Collectors.toSet());
    }

    public Set<UHCPlayer> getAllAlivePlayers() {
        return players.stream()
                .filter(UHCPlayer::isAlive)
                .collect(Collectors.toSet());
    }

    public Set<UHCPlayer> getOnlinePlayers() {
        return players.stream()
                .filter(UHCPlayer::isOnline)
                .collect(Collectors.toSet());
    }

    public synchronized List<UHCPlayer> getPlayersList() {
        return players;
    }

    public List<UHCPlayer> getDeadPlayers() {
        return players.stream().filter(UHCPlayer::isDead).filter(UHCPlayer::isOnline).collect(Collectors.toList());
    }

    public List<UHCPlayer> getSpectatorPlayers() {
        return players.stream().filter(UHCPlayer::isSpectator).filter(UHCPlayer::isOnline).collect(Collectors.toList());
    }

    public UHCPlayer getRandomOnlineUHCPlayer() {
        return getPlayers().get(RandomUtils.getRandom().nextInt(getPlayers().size()));
    }

    public UHCPlayer getUHCPlayerWithoutPerm(String perm) {
        List<UHCPlayer> onlineListWithoutPerm = getPlayers().stream().filter(uhcPlayer -> !uhcPlayer.hasPermission(perm)).collect(Collectors.toList());
        return onlineListWithoutPerm.get(RandomUtils.getRandom().nextInt(onlineListWithoutPerm.size()));
    }

    public UHCPlayer getWinnerPlayer() {
        return players.stream().filter(UHCPlayer::isWinner).filter(UHCPlayer::isOnline).findFirst().orElse(null);
    }

    public void setUHCWinner() {
        UHCPlayer winner = getAlivePlayers().stream()
                .filter(UHCPlayer::isOnline)
                .max(Comparator.comparingInt(UHCPlayer::getKills))
                .orElse(null);

        if (winner == null) return;

        if (GameValues.TEAM.TEAM_MODE) {
            winner.getTeam().getMembers().forEach(member -> member.setWinner(true));
        } else {
            winner.setWinner(true);
        }
//        if (getAliveList().isEmpty()) return;
//
//        UHCPlayer winner = getAliveList().get(0);
//        if (winner == null) return;
//
//        for (UHCPlayer uhcPlayer : getAliveList()) {
//            if (!uhcPlayer.isOnline()) return;
//            if (uhcPlayer.getKills() > winner.getKills()) {
//                winner = uhcPlayer;
//            }
//        }
//        if (GameValues.TEAM.TEAM_MODE) {
//            winner.getTeam().getMembers().forEach(member -> member.setWinner(true));
//            return;
//        }
//        winner.setWinner(true);
    }

    public String getUHCWinner() {
        if (GameValues.TEAM.TEAM_MODE) {
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

    public void showTopKillers() {
        List<UHCPlayer> topKillers = getTopKillers();
        for (int i = 0; i < 10; i++) {
            if (topKillers.size() <= i) break;
            UHCPlayer uhcPlayer = topKillers.get(i);
            Bukkit.broadcastMessage("§7" + (i + 1) + ". §e" + uhcPlayer.getName() + " §7- §e" + uhcPlayer.getKills() + " kills");
        }
    }

    public List<TopStatistics> getTotalTop(String type) {
        List<TopStatistics> topTotal = new ArrayList<>();
        FileConfiguration playerData = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
        for (String uuid : playerData.getConfigurationSection("player-data").getKeys(false)) {
            String name = playerData.getString("player-data." + uuid + ".name");
            int value = playerData.getInt("player-data." + uuid + "." + type.toLowerCase());
            topTotal.add(new TopStatistics(name, value));
        }
        topTotal.sort((name1, name2) -> Integer.compare(name2.getValue(), name1.getValue()));
        return topTotal;
    }

    public int getMaxPlayers() {
        return GameValues.TEAM.TEAM_SIZE * gameManager.getTeamManager().getTeamsList().size();
    }

    public void setPlayerWaitsAtLobby(UHCPlayer uhcPlayer) {
        Player p = uhcPlayer.getPlayer();
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.setExhaustion(20);
        p.setExp(0);
        p.setLevel(0);
        p.setFireTicks(0);
        p.setGameMode(GameMode.ADVENTURE);

        p.teleport(gameManager.getLobbyManager().getLocation("waiting"));

        uhcPlayer.clearPotions();
        uhcPlayer.clearInventory();
        gameManager.getKitsManager().giveLobbyKit(uhcPlayer);
    }

    public void onDisable() {
        this.players.clear();
    }
}