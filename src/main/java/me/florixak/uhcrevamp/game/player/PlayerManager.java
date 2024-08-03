package me.florixak.uhcrevamp.game.player;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import me.florixak.uhcrevamp.utils.MathUtils;
import me.florixak.uhcrevamp.utils.TeleportUtils;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerManager {

	private final GameManager gameManager;
	private final List<UHCPlayer> players;

	private int maxPlayersWhenTeams;

	public PlayerManager(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.players = new ArrayList<>();
	}

	public boolean doesPlayerExist(final Player player) {
		if (getUHCPlayer(player.getUniqueId()) != null) return true;
		return false;
	}

	public UHCPlayer getUHCPlayer(final UUID uuid) {
		for (final UHCPlayer uhcPlayer : getPlayersList()) {
			if (uhcPlayer.getUUID().equals(uuid)) {
				return uhcPlayer;
			}
		}
		return null;
	}

	public UHCPlayer getUhcPlayer(final String name) {
		for (final UHCPlayer uhcPlayer : getPlayersList()) {
			if (uhcPlayer.getName().equals(name)) {
				return uhcPlayer;
			}
		}
		return null;
	}


	public UHCPlayer getUHCPlayer(final Player player) {
		return getUHCPlayer(player.getUniqueId());
	}

	public UHCPlayer getOrCreateUHCPlayer(final Player player) {
		if (doesPlayerExist(player)) {
			return getUHCPlayer(player);
		} else {
			return newUHCPlayer(player);
		}
	}

	public synchronized UHCPlayer newUHCPlayer(final Player player) {
		return newUHCPlayer(player.getUniqueId(), player.getName());
	}

	public synchronized UHCPlayer newUHCPlayer(final UUID uuid, final String name) {
		final UHCPlayer newPlayer = new UHCPlayer(uuid, name);
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
		return getPlayers().get(MathUtils.getRandom().nextInt(getPlayers().size()));
	}

	public UHCPlayer getUHCPlayerWithoutPerm(final String perm) {
		final List<UHCPlayer> onlineListWithoutPerm = getPlayers().stream().filter(uhcPlayer -> !uhcPlayer.hasPermission(perm)).collect(Collectors.toList());
		return onlineListWithoutPerm.get(MathUtils.getRandom().nextInt(onlineListWithoutPerm.size()));
	}

	public UHCPlayer getWinnerPlayer() {
		return players.stream().filter(UHCPlayer::isWinner).filter(UHCPlayer::isOnline).findFirst().orElse(null);
	}

	public void setUHCWinner() {
		final UHCPlayer winner = getAlivePlayers().stream()
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
			final UHCTeam winnerTeam = gameManager.getTeamManager().getWinnerTeam();
			return winnerTeam != null ? (winnerTeam.getMembers().size() == 1 ? winnerTeam.getMembers().get(0).getName() : winnerTeam.getName()) : "None";
		}
		return getWinnerPlayer() != null ? getWinnerPlayer().getName() : "None";
	}

	private List<UHCPlayer> findTopKillers(final List<UHCPlayer> players) {
		players.sort((uhcPlayer1, uhcPlayer2) -> Integer.compare(uhcPlayer2.getKills(), uhcPlayer1.getKills()));
		return players;
	}

	public List<UHCPlayer> getTopKillers() {
		return findTopKillers(getPlayers());
	}

	public void showTopKillers() {
		final List<UHCPlayer> topKillers = getTopKillers();
		for (int i = 0; i < 10; i++) {
			if (topKillers.size() <= i) break;
			final UHCPlayer uhcPlayer = topKillers.get(i);
			Bukkit.broadcastMessage("§7" + (i + 1) + ". §e" + uhcPlayer.getName() + " §7- §e" + uhcPlayer.getKills() + " kills");
		}
	}

	public void setMaxPlayers() {
		this.maxPlayersWhenTeams = Math.min(gameManager.getTeamManager().getTeamsList().size() * GameValues.TEAM.TEAM_SIZE, Bukkit.getMaxPlayers());
	}

	public int getMaxPlayers() {
		if (GameValues.TEAM.TEAM_MODE) return maxPlayersWhenTeams;
		return Bukkit.getMaxPlayers();
	}

	public void setPlayerWaitsAtLobby(final UHCPlayer uhcPlayer) {
		final Player p = uhcPlayer.getPlayer();
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setExhaustion(0);
		p.setExp(0);
		p.setLevel(0);
		p.setFireTicks(0);
		p.setGameMode(GameMode.ADVENTURE);

		p.teleport(gameManager.getLobbyManager().getWaitingLobbyLocation());

		uhcPlayer.clearPotions();
		uhcPlayer.clearInventory();
		gameManager.getKitsManager().giveLobbyKit(uhcPlayer);
	}

	public void setPlayerForGame(final UHCPlayer uhcPlayer) {
		uhcPlayer.setState(PlayerState.ALIVE);

		uhcPlayer.setGameMode(GameMode.SURVIVAL);
		uhcPlayer.getPlayer().setHealth(uhcPlayer.getPlayer().getMaxHealth());
		uhcPlayer.getPlayer().setFoodLevel(20);
		uhcPlayer.getPlayer().setExhaustion(0);

		uhcPlayer.clearPotions();
		uhcPlayer.clearInventory();

		if (GameValues.TEAM.TEAM_MODE && !uhcPlayer.hasTeam()) {
			gameManager.getTeamManager().joinRandomTeam(uhcPlayer);
		}

		if (uhcPlayer.hasKit()) {
			if (!GameValues.KITS.BOUGHT_FOREVER) {
				uhcPlayer.getData().withdrawMoney(uhcPlayer.getKit().getCost());
				uhcPlayer.sendMessage(PlaceholderUtil.setPlaceholders(Messages.KITS_MONEY_DEDUCT.toString()
						.replace("%previous-money%", String.valueOf((uhcPlayer.getData().getMoney() + uhcPlayer.getKit().getCost())))
						.replace("%current-money%", String.valueOf(uhcPlayer.getData().getMoney())
						), uhcPlayer.getPlayer())
				);
			}
			uhcPlayer.getKit().giveKit(uhcPlayer);
		}
	}

//	public void revivePlayer(UHCPlayer uhcPlayer) {
//		uhcPlayer.revive();
//		uhcPlayer.setKit(null);
//		uhcPlayer.setPerk(null);
//		setPlayerForGame(uhcPlayer);
//	}

	public void teleportInToGame() {
		for (final UHCPlayer uhcPlayer : getAlivePlayers()) {
			final Location location = TeleportUtils.getSafeLocation();
			uhcPlayer.setSpawnLocation(location);
			uhcPlayer.teleport(location);
		}
	}

	public void teleportAfterMining() {
		for (final UHCPlayer uhcPlayer : getAlivePlayers()) {
			final Location location = uhcPlayer.getPlayer().getLocation();

			final double y = location.getWorld().getHighestBlockYAt(location);
			location.setY(y);

			uhcPlayer.teleport(location);
		}
	}

	public void onDisable() {
		for (final UHCPlayer uhcPlayer : getPlayersList()) {
			uhcPlayer.reset();
		}
		this.players.clear();
	}
}