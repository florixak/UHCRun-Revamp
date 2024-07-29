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

    private final GameManager gameManager;
    private final UHCPlayer uhcPlayer;
    private final FileConfiguration playerData;

    private final List<Kit> boughtKitsList;
    private final List<Perk> boughtPerksList;

    public PlayerData(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
        this.gameManager = GameManager.getGameManager();
        this.playerData = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

        this.boughtKitsList = new ArrayList<>();
        this.boughtPerksList = new ArrayList<>();

        initializeData();
    }

    public void initializeData() {
        setInitialData();
        loadBoughtKits();
        loadBoughtPerks();
    }

    public void setInitialData() {

        if (gameManager.isDatabaseConnected()) {
            gameManager.getDatabase().createPlayer(uhcPlayer.getPlayer());
        }

        if (hasData()) return;

        String path = "player-data." + uhcPlayer.getUUID();

        playerData.set(path + ".name", uhcPlayer.getName());
        playerData.set(path + ".uhc-level", GameValues.STATISTICS.FIRST_UHC_LEVEL);
        playerData.set(path + ".uhc-exp", 0);
        playerData.set(path + ".required-uhc-exp", GameValues.STATISTICS.FIRST_REQUIRED_EXP);
        playerData.set(path + ".wins", 0);
        playerData.set(path + ".losses", 0);
        playerData.set(path + ".kills", 0);
        playerData.set(path + ".assists", 0);
        playerData.set(path + ".deaths", 0);
        playerData.set(path + ".games-played", 0);
        playerData.set(path + ".kits", new ArrayList<>());
        playerData.set(path + ".perks", new ArrayList<>());
//        playerData.set(path + ".time-played", 0);
        playerData.set(path + ".displayed-top", "wins");

        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public boolean hasData() {
        return playerData.getConfigurationSection("player-data." + uhcPlayer.getUUID()) != null;
    }

    public String getName() {
        return playerData.getString("player-data." + uhcPlayer.getUUID() + ".name");
    }

    public double getMoney() {
        if (VaultHook.hasEconomy()) {
            return VaultHook.getBalance(uhcPlayer.getPlayer());
        }
        return GameValues.ERROR_INT_VALUE;
    }

    public void depositMoney(double amount) {
        if (VaultHook.hasEconomy()) {
            VaultHook.deposit(uhcPlayer.getPlayer(), amount);
        }
    }

    public void withdrawMoney(double amount) {
        if (VaultHook.hasEconomy()) {
            VaultHook.withdraw(uhcPlayer.getPlayer(), amount);
        }
    }

    public int getWins() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getWins(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".wins", GameValues.ERROR_INT_VALUE);
    }

    public void addWin(int amount) {
        if (amount == 0) return;

        playerData.set("player-data." + uhcPlayer.getUUID() + ".wins", getWins() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getDatabase().addWin(uhcPlayer.getUUID());
        }

        double money = GameValues.REWARDS.COINS_FOR_WIN;
        double exp = GameValues.REWARDS.UHC_EXP_FOR_WIN;

        depositMoney(money);
        addUHCExp(exp);
        uhcPlayer.addMoneyForGameResult(money);
        uhcPlayer.addUHCExpForGameResult(exp);
    }

    public int getLosses() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getLosses(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".losses", GameValues.ERROR_INT_VALUE);
    }

    public void addLose(int amount) {

        double money = GameValues.REWARDS.COINS_FOR_LOSE;
        double exp = GameValues.REWARDS.UHC_EXP_FOR_LOSE;

        depositMoney(money);
        addUHCExp(exp);
        uhcPlayer.addMoneyForGameResult(money);
        uhcPlayer.addUHCExpForGameResult(exp);

        if (gameManager.isDatabaseConnected()) {
            gameManager.getDatabase().addLose(uhcPlayer.getUUID());
            return;
        }

        playerData.set("player-data." + uhcPlayer.getUUID() + ".losses", getLosses() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getKills() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getKills(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".kills", GameValues.ERROR_INT_VALUE);
    }

    public void addKills(int amount) {
        double money = GameValues.REWARDS.COINS_FOR_KILL;
        double exp = GameValues.REWARDS.UHC_EXP_FOR_KILL;

        depositMoney(money);
        addUHCExp(exp);
        uhcPlayer.addMoneyForKills(money);
        uhcPlayer.addUHCExpForKills(exp);

        if (gameManager.isDatabaseConnected()) {
            gameManager.getDatabase().addKill(uhcPlayer.getUUID(), amount);
            return;
        }

        playerData.set("player-data." + uhcPlayer.getUUID() + ".kills", getKills() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getAssists() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getAssists(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".assists", GameValues.ERROR_INT_VALUE);
    }

    public void addAssists(int amount) {
        double money = GameValues.REWARDS.COINS_FOR_ASSIST;
        double exp = GameValues.REWARDS.UHC_EXP_FOR_ASSIST;

        depositMoney(money);
        addUHCExp(exp);
        uhcPlayer.addMoneyForAssists(money);
        uhcPlayer.addUHCExpForAssists(exp);

        uhcPlayer.sendMessage("[DEBUG MESSAGE] You have got assist!");

        if (gameManager.isDatabaseConnected()) {
            gameManager.getDatabase().addAssist(uhcPlayer.getUUID(), amount);
            return;
        }

        playerData.set("player-data." + uhcPlayer.getUUID() + ".assists", getAssists() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getDeaths() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getDeaths(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".deaths", GameValues.ERROR_INT_VALUE);
    }

    public void addDeaths(int amount) {

        if (gameManager.isDatabaseConnected()) {
            gameManager.getDatabase().addDeath(uhcPlayer.getUUID(), amount);
            return;
        }

        playerData.set("player-data." + uhcPlayer.getUUID() + ".deaths", getDeaths() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public void buyKit(Kit kit) {
        if (!kit.isFree() && uhcPlayer.getData().getMoney() < kit.getCost()) {
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

    public void loadBoughtKits() {
        List<String> boughtKitsList = playerData.getStringList("player-data." + uhcPlayer.getUUID() + ".kits");

        for (String kitName : boughtKitsList) {
            Kit kit = gameManager.getKitsManager().getKit(kitName);
            if (kit != null) this.boughtKitsList.add(kit);
        }
        saveKits();
    }

    public List<Kit> getKits() {
        return boughtKitsList;
    }

    private void saveKits() {
        List<String> kitsNameList = boughtKitsList.stream().map(Kit::getName).collect(Collectors.toList());
        playerData.set("player-data." + uhcPlayer.getUUID() + ".kits", kitsNameList);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public void buyPerk(Perk perk) {
        if (!perk.isFree() && uhcPlayer.getData().getMoney() < perk.getCost()) {
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

    public void loadBoughtPerks() {
        List<String> boughtPerksList = playerData.getStringList("player-data." + uhcPlayer.getUUID() + ".perks");

        for (String perkName : boughtPerksList) {
            Perk perk = gameManager.getPerksManager().getPerk(perkName);
            if (perk != null) this.boughtPerksList.add(perk);
        }
        savePerks();
    }

    public List<Perk> getPerks() {
        return boughtPerksList;
    }

    private void savePerks() {
        List<String> perksNameList = boughtPerksList.stream().map(Perk::getName).collect(Collectors.toList());
        playerData.set("player-data." + uhcPlayer.getUUID() + ".perks", perksNameList);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
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
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public long getTimePlayed() {
        return playerData.getLong("player-data." + uhcPlayer.getUUID() + ".time-played", 0);
    }

    public void addTimePlayed() {
        playerData.set("player-data." + uhcPlayer.getUUID() + ".time-played", getTimePlayed() + uhcPlayer.getTimePlayed());
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getUHCLevel() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getUHCLevel(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level", GameValues.ERROR_INT_VALUE);
    }

    public void increaseUHCLevel() {

        if (gameManager.isDatabaseConnected()) {
            gameManager.getDatabase().setUHCExp(uhcPlayer.getUUID(), getUHCExp() - getRequiredUHCExp());
            gameManager.getDatabase().addUHCLevel(uhcPlayer.getUUID());
            gameManager.getDatabase().setRequiredUHCExp(uhcPlayer.getUUID(), setRequiredUHCExp());
        } else {
            playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", getUHCExp() - getRequiredUHCExp());
            playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", getUHCLevel() + 1);
            playerData.set("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", setRequiredUHCExp());
            gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        }

        double reward = GameValues.REWARDS.BASE_REWARD * GameValues.REWARDS.REWARD_COEFFICIENT * getUHCLevel();
        depositMoney(reward);
        gameManager.getSoundManager().playUHCLevelUpSound(uhcPlayer.getPlayer());
        uhcPlayer.sendMessage(Messages.LEVEL_UP.toString().replace("%uhc-level%", String.valueOf(getUHCLevel())).replace("%previous-uhc-level%", String.valueOf(getPreviousUHCLevel())));
        uhcPlayer.sendMessage(Messages.REWARDS_LEVEL_UP.toString().replace("%money%", String.valueOf(reward)).replace("%prev-level%", String.valueOf(getPreviousUHCLevel())).replace("%new-level%", String.valueOf(getUHCLevel())));
    }

    public int getPreviousUHCLevel() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getUHCLevel(uhcPlayer.getUUID()) - 1;
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level", 1) - 1;
    }

    public double getUHCExp() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getUHCExp(uhcPlayer.getUUID());
        }
        return playerData.getDouble("player-data." + uhcPlayer.getUUID() + ".uhc-exp", GameValues.ERROR_INT_VALUE);
    }

    public void addUHCExp(double amount) {

        if (gameManager.isDatabaseConnected()) {
            gameManager.getDatabase().addUHCExp(uhcPlayer.getUUID(), amount);
        } else {
            double newExp = getUHCExp() + amount;
            playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", newExp);
            gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        }

        if (getUHCExp() >= getRequiredUHCExp()) {
            increaseUHCLevel();
        }
    }

    public double getRequiredUHCExp() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getRequiredUHCExp(uhcPlayer.getUUID());
        }
        return playerData.getDouble("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", GameValues.ERROR_INT_VALUE);
    }

    public double setRequiredUHCExp() {
        return getRequiredUHCExp() * GameValues.STATISTICS.EXP_MULTIPLIER;
    }

    public String getDisplayedTop() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getDatabase().getDisplayedTop(uhcPlayer.getUUID()).toLowerCase().replace("_", "-");
        }
        return playerData.getString("player-data." + uhcPlayer.getUUID() + ".displayed-top", "wins").toLowerCase();
    }

    public void setDisplayedTop(String topMode) {
        String displayedTop = getDisplayedTop();
        if (displayedTop.equalsIgnoreCase(topMode)) return;

        if (gameManager.isDatabaseConnected()) {
            gameManager.getDatabase().setDisplayedTop(uhcPlayer.getUUID(), topMode.toLowerCase());
            return;
        }

        playerData.set("player-data." + uhcPlayer.getUUID() + ".displayed-top", topMode.toLowerCase());
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

    }

    public void addWinOrLose() {
        if (uhcPlayer.isWinner()) addWin(1);
        else addLose(1);
        setGamesPlayed();
    }

//    public void addStatistics() {
//        depositMoney(moneyForGameResult + moneyForKills);
//        addUHCExp(uhcExpForGameResult + uhcExpForKills);
//        addKills(uhcPlayer.getKills());
//        addAssists(uhcPlayer.getAssists());
//        addDeaths(uhcPlayer.isWinner() ? 0 : 1);
//        addWinOrLose();
//    }

    public void addActivityRewards() {
        double money = GameValues.ACTIVITY_REWARDS.MONEY;
        double uhcExp = GameValues.ACTIVITY_REWARDS.EXP;

        uhcPlayer.addMoneyForActivity(money);
        uhcPlayer.addUHCExpForActivity(uhcExp);

        depositMoney(money);
        addUHCExp(uhcExp);

        uhcPlayer.sendMessage(Messages.REWARDS_ACTIVITY.toString().replace("%money%", String.valueOf(money)).replace("%uhc-exp%", String.valueOf(uhcExp)));
    }

    public void showStatistics() {
        List<String> rewards = uhcPlayer.isWinner() ? Messages.REWARDS_WIN.toList() : Messages.REWARDS_LOSE.toList();

        for (String message : rewards) {
            message = PlaceholderUtil.setPlaceholders(message, uhcPlayer.getPlayer());
            uhcPlayer.sendMessage(TextUtils.color(message));
        }
    }
}
