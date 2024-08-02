package me.florixak.uhcrevamp.game.player;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.kits.Kit;
import me.florixak.uhcrevamp.game.perks.Perk;
import me.florixak.uhcrevamp.hook.VaultHook;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerData {

	private final GameManager gameManager = GameManager.getGameManager();
	private final UHCPlayer uhcPlayer;
	private final FileConfiguration playerData;

	private String playerName;
	private double money;
	private int uhcLevel;
	private double uhcExp;
	private double requiredUhcExp;
	private int wins;
	private int losses;
	private int kills;
	private int killstreak;
	private int assists;
	private int deaths;
	private final List<Kit> boughtKitsList = new ArrayList<>();
	private final List<Perk> boughtPerksList = new ArrayList<>();

	public PlayerData(UHCPlayer uhcPlayer) {
		this.uhcPlayer = uhcPlayer;
		this.playerData = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
		initializeData();
	}

	public void initializeData() {
		setInitialData();
		if (gameManager.isDatabaseConnected())
			loadDataFromDatabase();
		else
			loadDataFromConfig();

		if (uhcLevel == 0) {
			uhcLevel = GameValues.STATISTICS.FIRST_UHC_LEVEL;
		}
		if (uhcExp == 0) {
			uhcExp = 0;
		}
		if (requiredUhcExp == 0) {
			requiredUhcExp = GameValues.STATISTICS.FIRST_REQUIRED_EXP;
		}
	}

	public void setInitialData() {
		if (hasData()) return;

		if (VaultHook.hasEconomy()) {
			if (GameValues.STATISTICS.STARTING_MONEY > 0 && !VaultHook.hasAccount(uhcPlayer.getPlayer())) {
				VaultHook.deposit(uhcPlayer.getName(), GameValues.STATISTICS.STARTING_MONEY);
			}
		}

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().createPlayer(uhcPlayer.getPlayer());
			return;
		}

		String path = "player-data." + uhcPlayer.getUUID();

		playerData.set(path + ".name", uhcPlayer.getName());
		playerData.set(path + ".money", GameValues.STATISTICS.STARTING_MONEY);
		playerData.set(path + ".uhc-level", GameValues.STATISTICS.FIRST_UHC_LEVEL);
		playerData.set(path + ".uhc-exp", 0);
		playerData.set(path + ".required-uhc-exp", GameValues.STATISTICS.FIRST_REQUIRED_EXP);
		playerData.set(path + ".games-played", 0);
		playerData.set(path + ".wins", 0);
		playerData.set(path + ".losses", 0);
		playerData.set(path + ".kills", 0);
		playerData.set(path + ".killstreak", 0);
		playerData.set(path + ".assists", 0);
		playerData.set(path + ".deaths", 0);
		playerData.set(path + ".kits", new ArrayList<>());
		playerData.set(path + ".perks", new ArrayList<>());
//        playerData.set(path + ".time-played", 0);

		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private void loadDataFromDatabase() {
		this.playerName = gameManager.getDatabase().getName(uhcPlayer.getUUID());
		this.money = gameManager.getDatabase().getMoney(uhcPlayer.getUUID());
		this.uhcLevel = gameManager.getDatabase().getUHCLevel(uhcPlayer.getUUID());
		this.uhcExp = gameManager.getDatabase().getUHCExp(uhcPlayer.getUUID());
		this.requiredUhcExp = gameManager.getDatabase().getRequiredUHCExp(uhcPlayer.getUUID());
		this.wins = gameManager.getDatabase().getWins(uhcPlayer.getUUID());
		this.losses = gameManager.getDatabase().getLosses(uhcPlayer.getUUID());
		this.kills = gameManager.getDatabase().getKills(uhcPlayer.getUUID());
		this.killstreak = gameManager.getDatabase().getKillstreak(uhcPlayer.getUUID());
		this.assists = gameManager.getDatabase().getAssists(uhcPlayer.getUUID());
		this.deaths = gameManager.getDatabase().getDeaths(uhcPlayer.getUUID());
	}

	private void loadDataFromConfig() {
		String path = "player-data." + uhcPlayer.getUUID();
		this.playerName = playerData.getString(path + ".name");
		this.money = playerData.getDouble(path + ".money", GameValues.STATISTICS.STARTING_MONEY);
		this.uhcLevel = playerData.getInt(path + ".uhc-level", GameValues.ERROR_INT_VALUE);
		this.uhcExp = playerData.getDouble(path + ".uhc-exp", GameValues.ERROR_INT_VALUE);
		this.requiredUhcExp = playerData.getDouble(path + ".required-uhc-exp", GameValues.ERROR_INT_VALUE);
		this.wins = playerData.getInt(path + ".wins", GameValues.ERROR_INT_VALUE);
		this.losses = playerData.getInt(path + ".losses", GameValues.ERROR_INT_VALUE);
		this.kills = playerData.getInt(path + ".kills", GameValues.ERROR_INT_VALUE);
		this.killstreak = playerData.getInt(path + ".killstreak", GameValues.ERROR_INT_VALUE);
		this.assists = playerData.getInt(path + ".assists", GameValues.ERROR_INT_VALUE);
		this.deaths = playerData.getInt(path + ".deaths", GameValues.ERROR_INT_VALUE);

		loadBoughtKits();
		loadBoughtPerks();
	}

	private boolean hasData() {
		if (gameManager.isDatabaseConnected()) {
			return gameManager.getDatabase().exists(uhcPlayer.getUUID());
		}
		return playerData.getConfigurationSection("player-data." + uhcPlayer.getUUID()) != null;
	}

	public String getName() {
		return playerName;
	}

	public double getMoney() {
		if (VaultHook.hasEconomy()) {
			return VaultHook.getBalance(uhcPlayer.getPlayer());
		}
		return money;
	}

	public void depositMoney(double amount) {
		if (VaultHook.hasEconomy()) {
			VaultHook.deposit(uhcPlayer.getName(), amount);
			return;
		}
		money += amount;
		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().setMoney(uhcPlayer.getUUID(), money);
			return;
		}
		playerData.set("player-data." + uhcPlayer.getUUID() + ".money", money);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void withdrawMoney(double amount) {
		if (VaultHook.hasEconomy()) {
			VaultHook.withdraw(uhcPlayer.getName(), amount);
			return;
		}
		money -= amount;
		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().setMoney(uhcPlayer.getUUID(), money);
			return;
		}
		playerData.set("player-data." + uhcPlayer.getUUID() + ".money", money);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	private boolean hasEnoughMoney(double amount) {
		return getMoney() >= amount;
	}

	public int getGamesPlayed() {
		return (getWins() + getLosses());
	}

	public void setGamesPlayed() {

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().setGamesPlayed(uhcPlayer.getUUID(), getGamesPlayed());
			return;
		}
		playerData.set("player-data." + uhcPlayer.getUUID() + ".games-played", getGamesPlayed());
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public int getWins() {
		return wins;
	}

	private void addWin() {
		wins++;
		double money = GameValues.REWARDS.COINS_FOR_WIN * GameValues.REWARDS.MULTIPLIER;
		double uhcExp = GameValues.REWARDS.UHC_EXP_FOR_WIN * GameValues.REWARDS.MULTIPLIER;

		uhcPlayer.addMoneyForGameResult(money);
		uhcPlayer.addUHCExpForGameResult(uhcExp);

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().addWin(uhcPlayer.getUUID());
			return;
		}

		playerData.set("player-data." + uhcPlayer.getUUID() + ".wins", wins);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public int getLosses() {
		return losses;
	}

	private void addLose() {
		losses++;
		double money = GameValues.REWARDS.COINS_FOR_LOSE;
		double exp = GameValues.REWARDS.UHC_EXP_FOR_LOSE;

		uhcPlayer.addMoneyForGameResult(money);
		uhcPlayer.addUHCExpForGameResult(exp);

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().addLose(uhcPlayer.getUUID());
			return;
		}

		playerData.set("player-data." + uhcPlayer.getUUID() + ".losses", losses);
		gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
	}

	public int getKills() {
		return kills;
	}

	private void addKills(int amount) {
		double money = GameValues.REWARDS.COINS_FOR_KILL;
		double exp = GameValues.REWARDS.UHC_EXP_FOR_KILL;

		uhcPlayer.addMoneyForKills(money);
		uhcPlayer.addUHCExpForKills(exp);

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().addKill(uhcPlayer.getUUID(), amount);
			return;
		}

		playerData.set("player-data." + uhcPlayer.getUUID() + ".kills", getKills() + amount);
		gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
	}

	public int getKillstreak() {
		return killstreak;
	}

	private void setKillstreak(int amount) {
		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().setKillstreak(uhcPlayer.getUUID(), amount);
			return;
		}
		playerData.set("player-data." + uhcPlayer.getUUID() + ".killstreak", amount);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public int getAssists() {
		return assists;
	}

	private void addAssists(int amount) {
		double money = GameValues.REWARDS.COINS_FOR_ASSIST;
		double exp = GameValues.REWARDS.UHC_EXP_FOR_ASSIST;

		uhcPlayer.addMoneyForAssists(money);
		uhcPlayer.addUHCExpForAssists(exp);

		uhcPlayer.sendMessage("[DEBUG MESSAGE] You have got assist!");

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().addAssist(uhcPlayer.getUUID(), amount);
			return;
		}

		playerData.set("player-data." + uhcPlayer.getUUID() + ".assists", getAssists() + amount);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public int getDeaths() {
		return deaths;
	}

	private void addDeaths(int amount) {

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().addDeath(uhcPlayer.getUUID(), amount);
			return;
		}

		playerData.set("player-data." + uhcPlayer.getUUID() + ".deaths", getDeaths() + amount);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void buyKit(Kit kit) {
		if (!kit.isFree() && !hasEnoughMoney(kit.getCost())) {
			uhcPlayer.sendMessage(Messages.NO_MONEY.toString());
			return;
		}
		boughtKitsList.add(kit);
		withdrawMoney(kit.getCost());
		saveKits();
		String kitCost = String.valueOf(kit.getCost());
		String money = String.valueOf(getMoney());
		String prevMoney = String.valueOf(uhcPlayer.getData().getMoney() + kit.getCost());
		uhcPlayer.sendMessage(Messages.KITS_MONEY_DEDUCT.toString(), "%previous-money%", prevMoney, "%money%", money, "%kit%", kit.getDisplayName(), "%kit-cost%", kitCost);
		uhcPlayer.setKit(kit);
		gameManager.getSoundManager().playSelectSound(uhcPlayer.getPlayer());
	}

	public boolean hasKitBought(Kit kit) {
		return boughtKitsList.contains(kit);
	}

	private void loadBoughtKits() {
		List<String> boughtKitsList;

		if (gameManager.isDatabaseConnected()) {
			boughtKitsList = gameManager.getDatabase().getBoughtKits(uhcPlayer.getUUID());
		} else {
			boughtKitsList = playerData.getStringList("player-data." + uhcPlayer.getUUID() + ".kits");
		}

		for (String kitName : boughtKitsList) {
			Kit kit = gameManager.getKitsManager().getKit(kitName);
			if (kit != null) this.boughtKitsList.add(kit);
		}
	}

	public List<Kit> getBoughtKits() {
		return boughtKitsList;
	}

	private void saveKits() {
		List<String> kitsNameList = boughtKitsList.stream().map(Kit::getName).collect(Collectors.toList());

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().setBoughtKits(uhcPlayer.getUUID(), kitsNameList.toString().replace("[", "").replace("]", ""));
			return;
		}

		playerData.set("player-data." + uhcPlayer.getUUID() + ".kits", kitsNameList);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	public void buyPerk(Perk perk) {
		if (!perk.isFree() && !hasEnoughMoney(perk.getCost())) {
			uhcPlayer.sendMessage(Messages.NO_MONEY.toString());
			return;
		}
		boughtPerksList.add(perk);
		withdrawMoney(perk.getCost());
		savePerks();
		String perkCost = String.valueOf(perk.getCost());
		String money = String.valueOf(getMoney());
		String prevMoney = String.valueOf(uhcPlayer.getData().getMoney() + perk.getCost());
		uhcPlayer.sendMessage(Messages.PERKS_MONEY_DEDUCT.toString().toString(), "%previous-money%", prevMoney, "%money%", money, "%perk%", perk.getDisplayName(), "%perk-cost%", perkCost);
		uhcPlayer.setPerk(perk);
		gameManager.getSoundManager().playSelectSound(uhcPlayer.getPlayer());
	}

	public boolean hasPerkBought(Perk perk) {
		return boughtPerksList.contains(perk);
	}

	private void loadBoughtPerks() {
		List<String> boughtPerksList;

		if (gameManager.isDatabaseConnected()) {
			boughtPerksList = gameManager.getDatabase().getBoughtPerks(uhcPlayer.getUUID());
		} else {
			boughtPerksList = playerData.getStringList("player-data." + uhcPlayer.getUUID() + ".perks");
		}

		for (String perkName : boughtPerksList) {
			Perk perk = gameManager.getPerksManager().getPerk(perkName);
			if (perk != null) this.boughtPerksList.add(perk);
		}
	}

	public List<Perk> getBoughtPerks() {
		return boughtPerksList;
	}

	private void savePerks() {
		List<String> perksNameList = boughtPerksList.stream().map(Perk::getName).collect(Collectors.toList());

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().setBoughtPerks(uhcPlayer.getUUID(), perksNameList.toString().replace("[", "").replace("]", ""));
			return;
		}

		playerData.set("player-data." + uhcPlayer.getUUID() + ".perks", perksNameList);
		gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
	}

	/*public long getTimePlayed() {
		return timePlayed;
	}

	public void addTimePlayed() {
		playerData.set("player-data." + uhcPlayer.getUUID() + ".time-played", getTimePlayed() + uhcPlayer.getTimePlayed());
		gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
	}*/

	/* UHC Level System */
	public void addUHCExp(double amount) {
		uhcExp += amount;
		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().addUHCExp(uhcPlayer.getUUID(), amount);
		} else {
			playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", uhcExp);
			gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}
		checkLevelUp();
	}

	private void checkLevelUp() {
		while (uhcExp >= requiredUhcExp) {
			increaseUHCLevel();
		}
	}

	private void increaseUHCLevel() {
		uhcExp -= requiredUhcExp;
		int previousLevel = uhcLevel;
		uhcLevel++;
		int newLevel = uhcLevel;
		requiredUhcExp = setRequiredUHCExp();

		if (gameManager.isDatabaseConnected()) {
			gameManager.getDatabase().setUHCExp(uhcPlayer.getUUID(), uhcExp);
			gameManager.getDatabase().addUHCLevel(uhcPlayer.getUUID());
			gameManager.getDatabase().setRequiredUHCExp(uhcPlayer.getUUID(), requiredUhcExp);
		} else {
			playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", uhcExp);
			playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", uhcLevel);
			playerData.set("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", requiredUhcExp);
			gameManager.getConfigManager().saveFile(ConfigType.PLAYER_DATA);
		}

		double reward = GameValues.REWARDS.BASE_REWARD * GameValues.REWARDS.REWARD_COEFFICIENT * uhcLevel;
		depositMoney(reward);
		if (uhcPlayer.getPlayer() != null) {
			gameManager.getSoundManager().playUHCLevelUpSound(uhcPlayer.getPlayer());
			uhcPlayer.sendMessage(Messages.LEVEL_UP.toString()
					.replace("%previous-uhc-level%", String.valueOf(previousLevel))
					.replace("%new-uhc-level%", String.valueOf(newLevel)));
			uhcPlayer.sendMessage(Messages.REWARDS_LEVEL_UP.toString().replace("%money%", String.valueOf(reward)));
		}
	}

	public int getUHCLevel() {
		return uhcLevel;
	}

	public double getUHCExp() {
		return uhcExp;
	}

	public double getRequiredUHCExp() {
		return requiredUhcExp;
	}

	private double setRequiredUHCExp() {
		return requiredUhcExp * GameValues.STATISTICS.EXP_MULTIPLIER;
	}

	/* Statistics */
	private void addGameResult() {
		if (uhcPlayer.isWinner()) addWin();
		else addLose();
	}

	public void saveStatistics() {
		addGameResult();
		if (uhcPlayer.getKills() > 0) addKills(uhcPlayer.getKills());
		if (uhcPlayer.getAssists() > 0) addAssists(uhcPlayer.getAssists());
		if (uhcPlayer.isDead()) addDeaths(1);

		if (uhcPlayer.getKills() > getKillstreak()) {
			setKillstreak(uhcPlayer.getKills());
			uhcPlayer.sendMessage(PlaceholderUtil.setPlaceholders(Messages.KILLSTREAK_NEW.toString(), uhcPlayer.getPlayer()));
		}

		double money = uhcPlayer.getMoneyForGameResult() + uhcPlayer.getMoneyForKills() + uhcPlayer.getMoneyForAssists() + uhcPlayer.getMoneyForActivity();
		double uhcExp = uhcPlayer.getUHCExpForGameResult() + uhcPlayer.getUHCExpForKills() + uhcPlayer.getUHCExpForAssists() + uhcPlayer.getUHCExpForActivity();
		money *= GameValues.REWARDS.MULTIPLIER;
		uhcExp *= GameValues.REWARDS.MULTIPLIER;

		setGamesPlayed();
		depositMoney(money);
		addUHCExp(uhcExp);
	}

	public void showStatistics() {
		List<String> rewards = uhcPlayer.isWinner() ? Messages.REWARDS_WIN.toList() : Messages.REWARDS_LOSE.toList();

		for (String message : rewards) {
			message = PlaceholderUtil.setPlaceholders(message, uhcPlayer.getPlayer());
			uhcPlayer.sendMessage(TextUtils.color(message));
		}
	}

	public void addActivityRewards() {
		double money = GameValues.ACTIVITY_REWARDS.MONEY;
		double uhcExp = GameValues.ACTIVITY_REWARDS.EXP;

		uhcPlayer.addMoneyForActivity(money);
		uhcPlayer.addUHCExpForActivity(uhcExp);

		depositMoney(money);
		addUHCExp(uhcExp);

		uhcPlayer.sendMessage(Messages.REWARDS_ACTIVITY.toString().replace("%money%", String.valueOf(money)).replace("%uhc-exp%", String.valueOf(uhcExp)));
	}

}
