package me.florixak.uhcrun.player;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerData {

    private final UHCPlayer uhcPlayer;
    private final FileConfiguration player_data;

    private double moneyForGameResult;
    private double moneyForKills;
    private double uhcExpForGameResult;
    private double uhcExpForKills;

    public PlayerData(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
        this.player_data = GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

        this.moneyForGameResult = 0;
        this.moneyForKills = 0;
        this.uhcExpForGameResult = 0;
        this.uhcExpForKills = 0;

        setData();
        GameManager.getGameManager().getData().createPlayer(uhcPlayer.getPlayer());
    }

    public void setData() {
        if (hasData()) return;

        player_data.set("player-data." + uhcPlayer.getUUID() + ".name", uhcPlayer.getName());

        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", 50);

        player_data.set("player-data." + uhcPlayer.getUUID() + ".wins", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".losses", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".kills", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".deaths", 0);

        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }
    public boolean hasData() {
        return player_data.getConfigurationSection("player-data." + uhcPlayer.getUUID()) != null;
    }

    public double getMoney() {
        if (GameManager.getGameManager().isVaultEnabled()) {
            return UHCRun.getVault().getBalance(uhcPlayer.getPlayer());
        }
        return 0.00;
    }
    public void addMoney(double money) {
        if (GameManager.getGameManager().isVaultEnabled()) {
            UHCRun.getVault().depositPlayer(uhcPlayer.getPlayer(), money);
        }
    }

    public int getWins() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".wins");
    }
    public void addWin(int wins) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".wins", getWins()+wins);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        double money = 500;
        double exp = 300;
        addMoney(money);
        addUHCExp(exp);
        this.moneyForGameResult += money;
        this.uhcExpForGameResult += exp;
    }

    public int getLosses() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".losses");
    }
    public void addLose(int losses) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".losses", getLosses()+losses);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (!GameManager.getGameManager().areStatsAddOnEnd()) {
            double money = 100;
            double exp = 50;
            addMoney(money);
            addUHCExp(exp);
            this.moneyForGameResult += money;
            this.uhcExpForGameResult += exp;
        }
    }

    public int getKills() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".kills");
    }
    public void addKills(int kills) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".kills", getKills()+kills);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (!GameManager.getGameManager().areStatsAddOnEnd()) {
            double money = 20;
            double exp = 25;
            addMoney(money);
            addUHCExp(exp);
            this.moneyForKills += money;
            this.uhcExpForKills += exp;
        }
    }

    public int getDeaths() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".deaths");
    }
    public void addDeaths(int deaths) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".deaths", getDeaths()+deaths);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getUHCLevel() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level");
    }
    public void addUHCLevel() {

        GameManager.getGameManager().getData().addUHCLevel(uhcPlayer.getUUID());

        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", getUHCExp()-getRequiredUHCExp());
        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", getUHCLevel()+1);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp", getRequiredUHCExp()*3.75);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        uhcPlayer.sendMessage(Messages.LEVEL_UP.toString()
                .replace("%uhc-level%", String.valueOf(getUHCLevel()))
                .replace("%previous-uhc-level%", String.valueOf(getPreviousUHCLevel())));

        switch (getUHCLevel()) {
            case 5:
                addMoney(50);
                break;
            case 10:
                addMoney(100);
                break;
            case 15:
                addMoney(150);
                break;
            case 20:
                addMoney(200);
                break;
            case 30:
                addMoney(300);
                break;
            case 40:
                addMoney(400);
                break;
            default:
                addMoney(20);
                uhcPlayer.sendMessage("+" + 20 + " for level up");
        }
    }
    public int getUHCExp() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-exp");
    }
    public void addUHCExp(double amount) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-exp", getUHCExp()+amount);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (getUHCExp() >= getRequiredUHCExp()) {
            addUHCLevel();
        }
    }
    public int getRequiredUHCExp() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".required-uhc-exp");
    }
    public int getPreviousUHCLevel() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level")-1;
    }

    public void addGameResult() {
        if (uhcPlayer.isWinner()) {
            addWin(1);
        } else {
            addLose(1);
        }
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
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
