package me.florixak.uhcrun.player;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class PlayerData {

    private final GameManager gameManager;
    private final UHCPlayer uhcPlayer;
    private final FileConfiguration player_data, config;

    private double moneyForGameResult;
    private double moneyForKills;
    private double uhcExpForGameResult;
    private double uhcExpForKills;

    public PlayerData(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
        this.gameManager = GameManager.getGameManager();
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.player_data = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

        this.moneyForGameResult = 0;
        this.moneyForKills = 0;
        this.uhcExpForGameResult = 0;
        this.uhcExpForKills = 0;

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
    public void addMoney(double amount) {
        if (gameManager.isVaultEnabled()) {
            UHCRun.getVault().depositPlayer(uhcPlayer.getPlayer(), amount);
        }
    }

    public int getWins() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getWins(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".wins");
    }
    public void addWin(int amount) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".wins", getWins()+amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addWin(uhcPlayer.getUUID());
        }

        double money = config.getDouble("settings.rewards.win.money");
        double exp = config.getDouble("settings.rewards.win.uhc-exp");

        addMoney(money);
        addUHCExp(exp);
        this.moneyForGameResult += money;
        this.uhcExpForGameResult += exp;
    }

    public int getLosses() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getLosses(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".losses");
    }
    public void addLose(int amount) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".losses", getLosses()+amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addLose(uhcPlayer.getUUID());
        }

        double money = config.getDouble("settings.rewards.lose.money");
        double exp = config.getDouble("settings.rewards.lose.uhc-exp");

        addMoney(money);
        addUHCExp(exp);
        this.moneyForGameResult += money;
        this.uhcExpForGameResult += exp;

    }

    public int getKills() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getKills(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".kills");
    }
    public void addKills(int amount) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".kills", getKills()+amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addKill(uhcPlayer.getUUID(), amount);
        }

        double money = config.getDouble("settings.rewards.kill.money");
        double exp = config.getDouble("settings.rewards.kill.uhc-exp");

        addMoney(money);
        addUHCExp(exp);
        this.moneyForKills += money;
        this.uhcExpForKills += exp;
    }

    public int getDeaths() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getDeaths(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".deaths");
    }
    public void addDeaths(int amount) {

        player_data.set("player-data." + uhcPlayer.getUUID() + ".deaths", getDeaths()+amount);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().addDeath(uhcPlayer.getUUID(), amount);
        }

        if (!uhcPlayer.getTeam().isAlive() && gameManager.isTeamMode()) {
            addLose(1);
        }
    }

    public int getUHCLevel() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getUHCLevel(uhcPlayer.getUUID());
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level");
    }
    public void addUHCLevel() {

        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", getUHCExp()-getRequiredUHCExp());
        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", getUHCLevel()+1);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp",
                getRequiredUHCExp()*config.getDouble("settings.statistics.player-level.required-exp-multiplier"));
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (gameManager.isDatabaseConnected()) {
            gameManager.getData().setUHCExp(uhcPlayer.getUUID(),getUHCExp()-getRequiredUHCExp());
            gameManager.getData().addUHCLevel(uhcPlayer.getUUID());
            gameManager.getData().setRequiredUHCExp(uhcPlayer.getUUID(),
                    getRequiredUHCExp()*config.getDouble("settings.statistics.player-level.required-exp-multiplier"));
        }

        uhcPlayer.sendMessage(Messages.LEVEL_UP.toString()
                .replace("%uhc-level%", String.valueOf(getUHCLevel()))
                .replace("%previous-uhc-level%", String.valueOf(getPreviousUHCLevel())));

        // gameManager.getSoundManager().playLevelUP(uhcPlayer.getPlayer());

        switch (getUHCLevel()) {
            case 5:
                addMoney(50);
                break;
            case 10:
                addMoney(200);
                break;
            case 15:
                addMoney(300);
                break;
            case 20:
                addMoney(350);
                break;
            case 30:
                addMoney(750);
                break;
            case 40:
                addMoney(1000);
                break;
            case 50:
                addMoney(2000);
                break;
            default:
                addMoney(20);
                uhcPlayer.sendMessage("+" + 20 + " for level up");
        }
    }
    public int getPreviousUHCLevel() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getUHCLevel(uhcPlayer.getUUID())-1;
        }
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level")-1;
    }

    public double getUHCExp() {
        if (gameManager.isDatabaseConnected()) {
            return gameManager.getData().getUHCExp(uhcPlayer.getUUID());
        }
        return player_data.getDouble("player-data." + uhcPlayer.getUUID() + ".uhc-exp");
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
        return player_data.getDouble("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp");
    }

    public void addGameResult() {
        if (uhcPlayer.isWinner()) {
            addWin(1);
        } else {
            addLose(1);
        }
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public void addStatistics() {
        addMoney(moneyForGameResult+moneyForKills);
        addUHCExp(uhcExpForGameResult+uhcExpForKills);
        addGameResult();
        addKills(uhcPlayer.getKills());
        addDeaths(!uhcPlayer.isWinner() ? 1 : 0);
    }
    public void showStatistics() {
        if (uhcPlayer.isWinner()) {
            for (String message : Messages.WIN_REWARDS.toList()) {

                message = message
                        .replace("%money-for-win%", String.valueOf(moneyForGameResult))
                        .replace("%money-for-kills%", String.valueOf(moneyForKills))
                        .replace("%uhc-exp-for-win%", String.valueOf(uhcExpForGameResult))
                        .replace("%uhc-exp-for-kills%", String.valueOf(uhcExpForKills));
                uhcPlayer.sendMessage(TextUtils.color(message));
            }
        } else {
            for (String message : Messages.LOSE_REWARDS.toList()) {

                message = message
                        .replace("%money-for-lose%", String.valueOf(moneyForGameResult))
                        .replace("%money-for-kills%", String.valueOf(moneyForKills))
                        .replace("%uhc-exp-for-lose%", String.valueOf(uhcExpForGameResult))
                        .replace("%uhc-exp-for-kills%", String.valueOf(uhcExpForKills));
                uhcPlayer.sendMessage(TextUtils.color(message));
            }
        }
    }


}
