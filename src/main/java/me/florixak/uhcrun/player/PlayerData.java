package me.florixak.uhcrun.player;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerData {

    private UHCPlayer uhcPlayer;
    private FileConfiguration player_data;

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
    }

    public void setData() {
        if (hasStatistics()) return;

        player_data.set("player-data." + uhcPlayer.getUUID() + ".name", uhcPlayer.getName());
        player_data.set("player-data." + uhcPlayer.getUUID() + ".money", 0);

        player_data.set("player-data." + uhcPlayer.getUUID() + ".wins", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".losses", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".kills", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".deaths", 0);

        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-exp", setRequiredUHCExp());

        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public boolean hasStatistics() {
        return player_data.getConfigurationSection("player-data." + uhcPlayer.getUUID()) != null;
    }

    public double getMoney() {
        if (GameManager.getGameManager().isVaultEnabled()) {
            return UHCRun.getVault().getBalance(uhcPlayer.getPlayer());
        }
        return player_data.getDouble("player-data." + uhcPlayer.getUUID() + ".money");
    }

    public int getWins() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".wins");
    }

    public int getLosses() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".losses");
    }

    public int getKills() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".kills");
    }

    public int getDeaths() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".deaths");
    }

    public int getUHCLevel() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level");
    }
    public int getPreviousUHCLevel() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level")-1;
    }
    public int getRequiredUHCExp() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".required-exp");
    }
    public double setRequiredUHCExp() {
        double totalRequiredExp = 0;
        if (getUHCLevel() <= 100) {
            totalRequiredExp = 10000 * Math.pow(1.025, getUHCLevel()) - 10000;
        }
        if (getUHCLevel() > 100) {
            totalRequiredExp = (20000 * Math.pow(1.025, 100) * Math.pow(getUHCLevel(), 2.5)/Math.pow(100, 2.5));
        }

        if (totalRequiredExp == 0) totalRequiredExp = 100.0;

        return totalRequiredExp;
    }

    public void addUHCLevel(int level) {

        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-exp", setRequiredUHCExp());
        player_data.set("player-data." + uhcPlayer.getUUID() + ".level", getUHCLevel()+level);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        // gameManager.getSoundManager().playLevelUP(player.getPlayer());

        // addMoney(getUHCLevel()*25.25);

        uhcPlayer.sendMessage(Messages.LEVEL_UP.toString()
                .replace("%level%", String.valueOf(getUHCLevel()))
                .replace("%previous-level%", String.valueOf(getPreviousUHCLevel())));
    }
    public void addUHCExp(double exp) {

        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-exp", getRequiredUHCExp()-exp);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        /*if (exp >= getRequiredExp()) {

        }*/

        if (getRequiredUHCExp() <= 0) {
            double over = Math.abs(getRequiredUHCExp());
            addUHCLevel(1);
            player_data.set("player-data." + uhcPlayer.getUUID() + ".required-exp", getRequiredUHCExp()-over);
            GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        }
    }

    public void addWin(int wins) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".wins", getWins()+wins);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (!GameManager.getGameManager().areStatisticsAddedOnEnd()) {
            double money = 500;
            double exp = 300;
            addMoney(money);
            addUHCExp(exp);
            this.moneyForGameResult += money;
            this.uhcExpForGameResult += exp;
        }
    }

    public void addLose(int losses) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".losses", getLosses()+losses);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (!GameManager.getGameManager().areStatisticsAddedOnEnd()) {
            double money = 100;
            double exp = 50;
            addMoney(money);
            addUHCExp(exp);
            this.moneyForGameResult += money;
            this.uhcExpForGameResult += exp;
        }
    }

    public void addKills(int kills) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".kills", getKills()+kills);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (!GameManager.getGameManager().areStatisticsAddedOnEnd()) {
            double money = 20;
            addMoney(money);
            this.moneyForKills += money;
        }
    }

    public void addDeaths(int deaths) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".deaths", getDeaths()+deaths);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public void addMoney(double money) {
        if (GameManager.getGameManager().isVaultEnabled()) {
            UHCRun.getVault().depositPlayer(uhcPlayer.getPlayer(), money);
            return;
        }
        player_data.set("player-data." + uhcPlayer.getUUID() + ".money", getMoney()+money);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
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
                        .replace("%uhc-exp-for-kills%", String.valueOf(uhcExpForGameResult));
                uhcPlayer.sendMessage(TextUtils.color(message));
            }
        } else {
            for (String message : Messages.LOSE_REWARDS.toList()) {

                message = message
                        .replace("%money-for-lose%", String.valueOf(moneyForGameResult))
                        .replace("%money-for-kills%", String.valueOf(moneyForKills))
                        .replace("%uhc-exp-for-lose%", String.valueOf(uhcExpForGameResult))
                        .replace("%uhc-exp-for-kills%", String.valueOf(uhcExpForGameResult));
                uhcPlayer.sendMessage(TextUtils.color(message));
            }
        }
    }


}
