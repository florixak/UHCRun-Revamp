package me.florixak.uhcrun.game.player;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.hook.VaultHook;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerData {

    // TODO - CHECK PLAYER DEATHS AND LOSSES STATS

    private final GameManager gameManager;
    private final UHCPlayer uhcPlayer;
    private final FileConfiguration playerData;

    private final ArrayList<Kit> boughtKits;
//    private final ArrayList<Perk> boughtPerks;

    private double moneyForGameResult, moneyForKills, moneyForAssists, moneyForActivity;
    private double uhcExpForGameResult, uhcExpForKills, uhcExpForAssists, uhcExpForActivity;

    public PlayerData(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
        this.gameManager = GameManager.getGameManager();
        this.playerData = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

        this.moneyForGameResult = 0;
        this.moneyForKills = 0;
        this.moneyForAssists = 0;
        this.moneyForActivity = 0;
        this.uhcExpForGameResult = 0;
        this.uhcExpForKills = 0;
        this.uhcExpForAssists = 0;

        this.boughtKits = new ArrayList<>();

        setData();
        loadBoughtKits();
    }

    public void setData() {

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().createPlayer(uhcPlayer.getPlayer());
        }

        if (hasData()) return;

        playerData.set("player-data." + uhcPlayer.getUUID() + ".name", uhcPlayer.getName());

        playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", GameValues.STATISTICS.FIRST_UHC_LEVEL);
        playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", 0);
        playerData.set("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", GameValues.STATISTICS.FIRST_REQUIRED_EXP);

        playerData.set("player-data." + uhcPlayer.getUUID() + ".wins", 0);
        playerData.set("player-data." + uhcPlayer.getUUID() + ".losses", 0);
        playerData.set("player-data." + uhcPlayer.getUUID() + ".kills", 0);
        playerData.set("player-data." + uhcPlayer.getUUID() + ".assists", 0);
        playerData.set("player-data." + uhcPlayer.getUUID() + ".deaths", 0);
        playerData.set("player-data." + uhcPlayer.getUUID() + ".games-played", 0);

        playerData.set("player-data." + uhcPlayer.getUUID() + ".kits", boughtKits);

        playerData.set("player-data." + uhcPlayer.getUUID() + ".displayed-top", "wins");

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
            return gameManager.getData().getWins(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".wins", GameValues.ERROR_INT_VALUE);
    }

    public void addWin(int amount) {
        if (amount == 0) return;

        playerData.set("player-data." + uhcPlayer.getUUID() + ".wins", getWins() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addWin(uhcPlayer.getUUID());
        }

        double money = GameValues.REWARDS.MONEY_FOR_WIN;
        double exp = GameValues.REWARDS.UHC_EXP_FOR_WIN;

        depositMoney(money);
        addUHCExp(exp);
        this.moneyForGameResult += money;
        this.uhcExpForGameResult += exp;
    }

    public int getLosses() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getLosses(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".losses", GameValues.ERROR_INT_VALUE);
    }

    public void addLose(int amount) {
        if (amount == 0) return;

        playerData.set("player-data." + uhcPlayer.getUUID() + ".losses", getLosses() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addLose(uhcPlayer.getUUID());
        }

        double money = GameValues.REWARDS.MONEY_FOR_LOSE;
        double exp = GameValues.REWARDS.UHC_EXP_FOR_LOSE;

        depositMoney(money);
        addUHCExp(exp);
        this.moneyForGameResult += money;
        this.uhcExpForGameResult += exp;
    }

    public int getKills() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getKills(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".kills", GameValues.ERROR_INT_VALUE);
    }

    public void addKills(int amount) {
        if (amount == 0) return;

        playerData.set("player-data." + uhcPlayer.getUUID() + ".kills", getKills() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addKill(uhcPlayer.getUUID(), amount);
        }

        double money = GameValues.REWARDS.MONEY_FOR_KILL;
        double exp = GameValues.REWARDS.UHC_EXP_FOR_KILL;

        depositMoney(money);
        addUHCExp(exp);
        this.moneyForKills += money;
        this.uhcExpForKills += exp;
    }

    public int getAssists() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getAssists(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".assists", GameValues.ERROR_INT_VALUE);
    }

    public void addAssists(int amount) {
        if (amount == 0) return;

        playerData.set("player-data." + uhcPlayer.getUUID() + ".assists", getAssists() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addAssist(uhcPlayer.getUUID(), amount);
        }

        double money = GameValues.REWARDS.MONEY_FOR_ASSIST;
        double exp = GameValues.REWARDS.UHC_EXP_FOR_ASSIST;

        depositMoney(money);
        addUHCExp(exp);
        this.moneyForAssists += money;
        this.uhcExpForAssists += exp;
    }

    public int getDeaths() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getDeaths(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".deaths", GameValues.ERROR_INT_VALUE);
    }

    public void addDeaths(int amount) {
        if (amount == 0) return;

        playerData.set("player-data." + uhcPlayer.getUUID() + ".deaths", getDeaths() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addDeath(uhcPlayer.getUUID(), amount);
        }
    }

    public void buyKit(Kit kit) {
        boughtKits.add(kit);
        withdrawMoney(kit.getCost());
        String kitCost = String.valueOf(kit.getCost());
        String money = String.valueOf(getMoney());
        String prevMoney = String.valueOf(uhcPlayer.getData().getMoney() + kit.getCost());
        uhcPlayer.sendMessage(Messages.KITS_MONEY_DEDUCT.toString(), "%previous-money%", prevMoney, "%money%", money, "%kit%", kit.getDisplayName(), "%kit-cost%", kitCost);
        saveKits();
    }

    public boolean alreadyBoughtKit(Kit kit) {
        return boughtKits.contains(kit);
    }

    public void loadBoughtKits() {
        List<String> stringKits = playerData.getStringList("player-data." + uhcPlayer.getUUID() + ".kits");

        for (String kitName : stringKits) {
            Kit kit = gameManager.getKitsManager().getKit(kitName);
            if (kit != null) boughtKits.add(kit);
        }
        saveKits();
    }

    public List<Kit> getKits() {
        return boughtKits;
    }

    private void saveKits() {
        List<String> stringKits = boughtKits.stream()
                .map(Kit::getName)
                .collect(Collectors.toList());
        playerData.set("player-data." + uhcPlayer.getUUID() + ".kits", stringKits);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getGamesPlayed() {
        return (getWins() + getLosses());
    }

    public void setGamesPlayed() {
        playerData.set("player-data." + uhcPlayer.getUUID() + ".games-played", getGamesPlayed());
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().setGamesPlayed(uhcPlayer.getUUID(), getGamesPlayed());
        }
    }

    public int getUHCLevel() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getUHCLevel(uhcPlayer.getUUID());
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level", GameValues.ERROR_INT_VALUE);
    }

    public void addUHCLevel() {

        playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", getUHCExp() - getRequiredUHCExp());
        playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", getUHCLevel() + 1);
        playerData.set("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", setRequiredUHCExp());
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().setUHCExp(uhcPlayer.getUUID(), getUHCExp() - getRequiredUHCExp());
            gameManager.getData().addUHCLevel(uhcPlayer.getUUID());
            gameManager.getData().setRequiredUHCExp(uhcPlayer.getUUID(), setRequiredUHCExp());
        }

        uhcPlayer.sendMessage(Messages.LEVEL_UP.toString()
                .replace("%uhc-level%", String.valueOf(getUHCLevel()))
                .replace("%previous-uhc-level%", String.valueOf(getPreviousUHCLevel())));

        gameManager.getSoundManager().playLevelUP(uhcPlayer.getPlayer());

        double reward = GameValues.REWARDS.BASE_REWARD * GameValues.REWARDS.REWARD_COEFFICIENT * getUHCLevel();

        depositMoney(reward);
        uhcPlayer.sendMessage(Messages.REWARDS_LEVEL_UP.toString()
                .replace("%money%", String.valueOf(reward))
                .replace("%prev-level%", String.valueOf(getPreviousUHCLevel()))
                .replace("%new-level%", String.valueOf(getUHCLevel())));
    }

    public int getPreviousUHCLevel() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getUHCLevel(uhcPlayer.getUUID()) - 1;
        }
        return playerData.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level", 1) - 1;
    }

    public double getUHCExp() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getUHCExp(uhcPlayer.getUUID());
        }
        return playerData.getDouble("player-data." + uhcPlayer.getUUID() + ".uhc-exp", GameValues.ERROR_INT_VALUE);
    }

    public void addUHCExp(double amount) {
        if (amount == 0.00) return;

        playerData.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", getUHCExp() + amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addUHCExp(uhcPlayer.getUUID(), amount);
        }

        if (getUHCExp() >= getRequiredUHCExp()) {
            addUHCLevel();
        }
    }

    public double getRequiredUHCExp() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getRequiredUHCExp(uhcPlayer.getUUID());
        }
        return playerData.getDouble("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", GameValues.ERROR_INT_VALUE);
    }

    public double setRequiredUHCExp() {
        return getRequiredUHCExp() * GameValues.STATISTICS.EXP_MULTIPLIER;
    }

    public String getDisplayedTop() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getDisplayedTop(uhcPlayer.getUUID()).toLowerCase().replace("_", "-");
        }
        return playerData.getString("player-data." + uhcPlayer.getUUID() + ".displayed-top", "wins").toLowerCase();
    }

    public void setDisplayedTop(String topMode) {
        if (getDisplayedTop().equalsIgnoreCase(topMode)) return;

        playerData.set("player-data." + uhcPlayer.getUUID() + ".displayed-top", topMode.toLowerCase());
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().setDisplayedTop(uhcPlayer.getUUID(), topMode.toLowerCase());
        }
    }

    public void addWinOrLose() {
        if (uhcPlayer.isWinner()) addWin(1);
        else addLose(1);
        setGamesPlayed();
    }

    public void addStatistics() {
        depositMoney(moneyForGameResult + moneyForKills);
        addUHCExp(uhcExpForGameResult + uhcExpForKills);
        addKills(uhcPlayer.getKills());
        addAssists(uhcPlayer.getAssists());
        addDeaths(uhcPlayer.isWinner() ? 0 : 1);
        addWinOrLose();
    }

    public void addActivityRewards() {
        double money = GameValues.ACTIVITY_REWARDS.MONEY;
        double uhcExp = GameValues.ACTIVITY_REWARDS.EXP;

        this.moneyForActivity += money;
        this.uhcExpForActivity += uhcExp;

        depositMoney(money);
        addUHCExp(uhcExp);

        uhcPlayer.sendMessage(Messages.REWARDS_ACTIVITY.toString()
                .replace("%money%", String.valueOf(money))
                .replace("%uhc-exp%", String.valueOf(uhcExp)));
    }

    public void showStatistics() {
        List<String> rewards = uhcPlayer.isWinner() ? Messages.REWARDS_WIN.toList() : Messages.REWARDS_LOSE.toList();

        for (String message : rewards) {
            message = message
                    .replace("%money-for-game%", String.valueOf(moneyForGameResult))
                    .replace("%money-for-kills%", String.valueOf(moneyForKills))
                    .replace("%money-for-assists%", String.valueOf(moneyForAssists))
                    .replace("%money-for-activity%", String.valueOf(moneyForActivity))
                    .replace("%uhc-exp-for-game%", String.valueOf(uhcExpForGameResult))
                    .replace("%uhc-exp-for-kills%", String.valueOf(uhcExpForKills))
                    .replace("%uhc-exp-for-assists%", String.valueOf(uhcExpForAssists))
                    .replace("%uhc-exp-for-activity%", String.valueOf(uhcExpForActivity));
            uhcPlayer.sendMessage(TextUtils.color(message));
        }
    }
}
