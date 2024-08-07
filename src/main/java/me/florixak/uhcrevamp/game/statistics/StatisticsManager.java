package me.florixak.uhcrevamp.game.statistics;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.ItemUtils;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsManager {

	private final GameManager gameManager;

	public List<TopStatistics> winTopList;
	public List<TopStatistics> killTopList;
	public List<TopStatistics> assistTopList;
	public List<TopStatistics> deathTopList;
	public List<TopStatistics> lossTopList;
	public List<TopStatistics> killstreakTopList;
	public List<TopStatistics> uhcLevelTopList;
	public List<TopStatistics> gamesPlayedTopList;

	public StatisticsManager(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public void loadTopStatistics() {
		this.winTopList = getTotalTopOf("wins");
		this.killTopList = getTotalTopOf("kills");
		this.assistTopList = getTotalTopOf("assists");
		this.deathTopList = getTotalTopOf("deaths");
		this.lossTopList = getTotalTopOf("losses");
		this.killstreakTopList = getTotalTopOf("killstreak");
		this.uhcLevelTopList = getTotalTopOf("uhc_level");
		this.gamesPlayedTopList = getTotalTopOf("games_played");
	}

	public List<TopStatistics> getTotalTopOf(final String type) {
		final List<TopStatistics> topTotal = new ArrayList<>();

		if (gameManager.isDatabaseConnected()) {
			final Map<String, Integer> topStatistics = gameManager.getDatabase().getTopStatistics(type.toLowerCase());
			if (topStatistics != null) {
				topTotal.addAll(topStatistics.entrySet().stream().map(entry -> new TopStatistics(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
			}
		} else {
			final FileConfiguration playerData = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
			if (playerData.getConfigurationSection("player-data") == null) return topTotal;
			for (final String uuid : playerData.getConfigurationSection("player-data").getKeys(false)) {
				final String name = playerData.getString("player-data." + uuid + ".name");
				final int value = playerData.getInt("player-data." + uuid + "." + type.toLowerCase());
				topTotal.add(new TopStatistics(name, value));
			}
		}

		topTotal.sort((name1, name2) -> Integer.compare(name2.getValue(), name1.getValue()));
		return topTotal;
	}

	public List<TopStatistics> getTopStatistics(final String type) {
		switch (type) {
			case "Wins":
				return winTopList;
			case "Kills":
				return killTopList;
			case "Assists":
				return assistTopList;
			case "Deaths":
				return deathTopList;
			case "Losses":
				return lossTopList;
			case "Killstreak":
				return killstreakTopList;
			case "UHC-Level":
				return uhcLevelTopList;
			case "Games-Played":
				return gamesPlayedTopList;
			default:
				return null;
		}
	}

	public String getTopStatsDisplayItem(final String type) {
		switch (type) {
			case "Wins":
				return GameValues.STATISTICS.TOP_WINS_ITEM;
			case "Kills":
				return GameValues.STATISTICS.TOP_KILLS_ITEM;
			case "Assists":
				return GameValues.STATISTICS.TOP_ASSISTS_ITEM;
			case "Deaths":
				return GameValues.STATISTICS.TOP_DEATHS_ITEM;
			case "Losses":
				return GameValues.STATISTICS.TOP_LOSSES_ITEM;
			case "Killstreak":
				return GameValues.STATISTICS.TOP_KILLSTREAK_ITEM;
			case "UHC-Level":
				return GameValues.STATISTICS.TOP_UHC_LEVEL_ITEM;
			case "Games-Played":
				return GameValues.STATISTICS.TOP_GAMES_PLAYED_ITEM;
			default:
				return "PAPER";
		}
	}

	public String getTopStatsDisplayName(final String type) {
		switch (type) {
			case "Wins":
				return GameValues.STATISTICS.TOP_WINS_NAME;
			case "Kills":
				return GameValues.STATISTICS.TOP_KILLS_NAME;
			case "Assists":
				return GameValues.STATISTICS.TOP_ASSISTS_NAME;
			case "Deaths":
				return GameValues.STATISTICS.TOP_DEATHS_NAME;
			case "Losses":
				return GameValues.STATISTICS.TOP_LOSSES_NAME;
			case "Killstreak":
				return GameValues.STATISTICS.TOP_KILLSTREAK_NAME;
			case "UHC-Level":
				return GameValues.STATISTICS.TOP_UHC_LEVEL_NAME;
			case "Games-Played":
				return GameValues.STATISTICS.TOP_GAMES_PLAYED_NAME;
			default:
				return "TOP STATS";
		}
	}

	public void resetStatistics() {
		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().emptyTable();
		} else {
			gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig().set("player-data", null);
			gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}
	}

	public void resetStatistics(final UHCPlayer uhcPlayer) {
		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().resetPlayer(uhcPlayer.getUUID());
		} else {
			gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig().set("player-data." + uhcPlayer.getUUID(), null);
			gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}
	}

	public List<TopStatistics> getWinTopList() {
		return winTopList;
	}

	public List<TopStatistics> getKillTopList() {
		return killTopList;
	}

	public List<TopStatistics> getAssistTopList() {
		return assistTopList;
	}

	public List<TopStatistics> getDeathTopList() {
		return deathTopList;
	}

	public List<TopStatistics> getLossTopList() {
		return lossTopList;
	}

	public List<TopStatistics> getKillstreakTopList() {
		return killstreakTopList;
	}

	public List<TopStatistics> getUhcLevelTopList() {
		return uhcLevelTopList;
	}

	public List<TopStatistics> getGamesPlayedTopList() {
		return gamesPlayedTopList;
	}

	public static ItemStack getPlayerStatsItem(final UHCPlayer uhcPlayer) {
		final ItemStack playerStatsItem = XMaterial.matchXMaterial(GameValues.STATISTICS.PLAYER_STATS_DIS_ITEM.toUpperCase())
				.get().parseItem() == null || GameValues.STATISTICS.PLAYER_STATS_DIS_ITEM.equalsIgnoreCase("PLAYER_HEAD")
				? Utils.getPlayerHead(uhcPlayer.getPlayer(), uhcPlayer.getName())
				: XMaterial.matchXMaterial(GameValues.STATISTICS.PLAYER_STATS_DIS_ITEM.toUpperCase())
				.get().parseItem();

		final String playerStatsName = GameValues.STATISTICS.PLAYER_STATS_CUST_NAME != null
				? GameValues.STATISTICS.PLAYER_STATS_CUST_NAME
				: uhcPlayer.getName();

		final List<String> playerStatsLore = new ArrayList<>();

		for (String text : GameValues.STATISTICS.PLAYER_STATS_LORE) {
			text = PlaceholderUtil.setPlaceholders(text, uhcPlayer.getPlayer());
			playerStatsLore.add(TextUtils.color(text));
		}
		return ItemUtils.createItem(
				playerStatsItem.getType(),
				playerStatsName.replace("%player%", uhcPlayer.getName()),
				1,
				playerStatsLore);
	}

	public static ItemStack getTopStatsItem(final String topType) {
		final ItemStack topStatsItem = XMaterial.matchXMaterial(GameManager.getGameManager().getStatisticsManager().getTopStatsDisplayItem(topType)).get().parseItem();
		final String topStatsName = GameValues.STATISTICS.TOP_STATS_CUST_NAME != null
				? GameValues.STATISTICS.TOP_STATS_CUST_NAME
				: "TOP STATS";

		final List<String> topStatsLore = new ArrayList<>();
		final List<TopStatistics> totalTopList = GameManager.getGameManager().getStatisticsManager().getTopStatistics(topType);

		if (totalTopList != null) {
			for (String lore : GameValues.STATISTICS.TOP_STATS_LORE) {
				for (int j = 0; j < GameValues.STATISTICS.TOP_STATS_LORE.size(); j++) {
					if (totalTopList.size() > j) {
						final String name = totalTopList.get(j).getName();
						final int value = totalTopList.get(j).getValue();
						lore = lore
								.replace("%uhc-top-" + (j + 1) + "%", name != null ? name : "None")
								.replace("%uhc-top-" + (j + 1) + "-value%", name != null ? String.valueOf(value) : String.valueOf(0));
					} else {
						lore = lore
								.replace("%uhc-top-" + (j + 1) + "%", "None")
								.replace("%uhc-top-" + (j + 1) + "-value%", String.valueOf(0));
					}
				}
				topStatsLore.add(TextUtils.color(lore.replace("%top-stats-mode%", TextUtils.color(GameManager.getGameManager().getStatisticsManager().getTopStatsDisplayName(topType)))));
			}
		}
		return ItemUtils.createItem(topStatsItem.getType(),
				topStatsName.replace("%top-stats-mode%", TextUtils.color(GameManager.getGameManager().getStatisticsManager().getTopStatsDisplayName(topType))),
				1,
				topStatsLore);
	}
}
