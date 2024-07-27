package me.florixak.uhcrevamp.game.player;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.statistics.TopStatistics;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import me.florixak.uhcrevamp.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager {

    private final GameManager gameManager;
    private final List<UHCPlayer> players;

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

    public UHCPlayer getUHCPlayerWithoutPerm(String perm) {
        List<UHCPlayer> onlineListWithoutPerm = getOnlineList().stream().filter(uhcPlayer -> !uhcPlayer.hasPermission(perm)).collect(Collectors.toList());
        return onlineListWithoutPerm.get(RandomUtils.getRandom().nextInt(onlineListWithoutPerm.size()));
    }

    public UHCPlayer getWinnerPlayer() {
        return getAliveList().stream().filter(UHCPlayer::isWinner).findFirst().orElse(null);
    }

    public void setUHCWinner() {
        UHCPlayer winner = getAliveList().stream()
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

    public void onDisable() {
        this.players.clear();
    }
}