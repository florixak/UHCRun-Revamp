package me.florixak.uhcrun.player;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.utils.text.TextUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class PlayerData {

    private final GameManager gameManager;
    private final UHCPlayer uhcPlayer;
    private final FileConfiguration player_data, config;

    private double moneyForGameResult, moneyForKills, moneyForAssists;
    private double uhcExpForGameResult, uhcExpForKills, uhcExpForAssists;

    public PlayerData(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
        this.gameManager = GameManager.getGameManager();
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.player_data = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

        this.moneyForGameResult = 0;
        this.moneyForKills = 0;
        this.moneyForAssists = 0;
        this.uhcExpForGameResult = 0;
        this.uhcExpForKills = 0;
        this.uhcExpForAssists = 0;

        setData();
    }

    public void setData() {

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().createPlayer(uhcPlayer.getPlayer());
        }

        if (hasData()) return;

        player_data.set("player-data." + uhcPlayer.getUUID() + ".name", uhcPlayer.getName());

        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", config.getDouble("settings.statistics.player-level.first-uhc-level"));
        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", config.getDouble("settings.statistics.player-level.first-required-exp"));

        player_data.set("player-data." + uhcPlayer.getUUID() + ".wins", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".losses", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".kills", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".assists", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".deaths", 0);

        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }
    public boolean hasData() {
        return player_data.getConfigurationSection("player-data." + uhcPlayer.getUUID()) != null;
    }

    public double getMoney() {
        if (gameManager.isVaultEnabled()) {
            return UHCRun.getVault().getBalance(uhcPlayer.getPlayer());
        }
        return 0.00;
    }
    public void depositMoney(double amount) {
        if (gameManager.isVaultEnabled()) {
            UHCRun.getVault().depositPlayer(uhcPlayer.getPlayer(), amount);
        }
    }
    public void withdrawMoney(double amount) {
        if (gameManager.isVaultEnabled()) {
            Economy economy = UHCRun.getVault();
            economy.withdrawPlayer(uhcPlayer.getPlayer(), amount);
        }
    }

    public int getWins() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getWins(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".wins", 0);
    }
    public void addWin(int amount) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".wins", getWins()+amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addWin(uhcPlayer.getUUID());
        }

        double money = GameValues.MONEY_FOR_WIN;
        double exp = GameValues.UHC_EXP_FOR_WIN;

        depositMoney(money);
        addUHCExp(exp);
        this.moneyForGameResult += money;
        this.uhcExpForGameResult += exp;
    }

    public int getLosses() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getLosses(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".losses", 0);
    }
    public void addLose(int amount) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".losses", getLosses()+amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addLose(uhcPlayer.getUUID());
        }

        double money = GameValues.MONEY_FOR_LOSE;
        double exp = GameValues.UHC_EXP_FOR_LOSE;

        depositMoney(money);
        addUHCExp(exp);
        this.moneyForGameResult += money;
        this.uhcExpForGameResult += exp;

    }

    public int getKills() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getKills(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".kills", 0);
    }
    public void addKills(int amount) {

        player_data.set("player-data." + uhcPlayer.getUUID() + ".kills", getKills()+amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addKill(uhcPlayer.getUUID(), amount);
        }

        if (amount == 0) return;

        double money = GameValues.MONEY_FOR_KILL;
        double exp = GameValues.UHC_EXP_FOR_KILL;

        depositMoney(money);
        addUHCExp(exp);

        this.moneyForKills += money;
        this.uhcExpForKills += exp;
    }

    public int getAssists() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getAssists(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".assists", 0);
    }
    public void addAssists(int amount) {

        player_data.set("player-data." + uhcPlayer.getUUID() + ".assists", getAssists()+amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addAssist(uhcPlayer.getUUID(), amount);
        }

        if (amount == 0) return;

        double money = GameValues.MONEY_FOR_ASSIST;
        double exp = GameValues.UHC_EXP_FOR_ASSIST;

        depositMoney(money);
        addUHCExp(exp);
        this.moneyForAssists += money;
        this.uhcExpForAssists += exp;
    }

    public int getDeaths() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getDeaths(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".deaths", 0);
    }
    public void addDeaths(int amount) {

        player_data.set("player-data." + uhcPlayer.getUUID() + ".deaths", getDeaths()+amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addDeath(uhcPlayer.getUUID(), amount);
        }

        if (GameValues.TEAM_MODE && !uhcPlayer.getTeam().isAlive()) {
            addLose(1);
        }
    }

    public int getUHCLevel() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getUHCLevel(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level", 0);
    }
    public void addUHCLevel() {

        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", getUHCExp()-getRequiredUHCExp());
        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", getUHCLevel()+1);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", setRequiredUHCExp());
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().setUHCExp(uhcPlayer.getUUID(),getUHCExp()-getRequiredUHCExp());
            gameManager.getData().addUHCLevel(uhcPlayer.getUUID());
            gameManager.getData().setRequiredUHCExp(uhcPlayer.getUUID(), setRequiredUHCExp());
        }

        uhcPlayer.sendMessage(Messages.LEVEL_UP.toString()
                .replace("%uhc-level%", String.valueOf(getUHCLevel()))
                .replace("%previous-uhc-level%", String.valueOf(getPreviousUHCLevel())));

        gameManager.getSoundManager().playLevelUP(uhcPlayer.getPlayer());

        double reward = config.getInt("settings.statistics.rewards.base-reward", 100)
                *
                config.getDouble("settings.statistics.rewards.reward-coefficient", 1)
                *
                getUHCLevel();

        depositMoney(reward);
        uhcPlayer.sendMessage(Messages.REWARDS_LEVEL_UP.toString()
                .replace("%money-for-level-up%", String.valueOf(reward))
                .replace("%prev-level%", String.valueOf(getPreviousUHCLevel()))
                .replace("%new-level%", String.valueOf(getUHCLevel()))
        );
    }
    public int getPreviousUHCLevel() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getUHCLevel(uhcPlayer.getUUID())-1;
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level", 1)-1;
    }

    public double getUHCExp() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getUHCExp(uhcPlayer.getUUID());
        }
        return player_data.getDouble("player-data." + uhcPlayer.getUUID() + ".uhc-exp", 0);
    }
    public void addUHCExp(double amount) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", getUHCExp()+amount);
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
        return player_data.getDouble("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", 0);
    }
    public double setRequiredUHCExp() {
        return getRequiredUHCExp()*config.getDouble("settings.statistics.player-level.required-exp-multiplier", 1.2);
    }

    public void addWinOrLose() {
        if (uhcPlayer.isWinner())
            addWin(1);
        else
            addLose(1);
    }

    public void addStatistics() {
        depositMoney(moneyForGameResult+moneyForKills);
        addUHCExp(uhcExpForGameResult+uhcExpForKills);
        addWinOrLose();
        addKills(uhcPlayer.getKills());
        addAssists(uhcPlayer.getAssists());
        addDeaths(uhcPlayer.isWinner() ? 0 : 1);
    }
    public void displayStatistics() {
        List<String> rewards = uhcPlayer.isWinner() ? Messages.REWARDS_WIN.toList() : Messages.REWARDS_LOSE.toList();

        for (String message : rewards) {
            message = message
                    .replace("%money-for-game%", String.valueOf(moneyForGameResult))
                    .replace("%money-for-kills%", String.valueOf(moneyForKills))
                    .replace("%money-for-assists%", String.valueOf(moneyForAssists))
                    .replace("%uhc-exp-for-game%", String.valueOf(uhcExpForGameResult))
                    .replace("%uhc-exp-for-kills%", String.valueOf(uhcExpForKills))
                    .replace("%uhc-exp-for-assists%", String.valueOf(uhcExpForAssists));
            uhcPlayer.sendMessage(TextUtils.color(message));
        }
    }
}
