package me.florixak.uhcrun.player;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerData {

    private UHCPlayer uhcPlayer;
    private FileConfiguration player_data;

    public PlayerData(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
        this.player_data = GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

        loadData();
    }

    public void loadData() {
        if (hasStatistics()) return;

        player_data.set("player-data." + uhcPlayer.getUUID() + ".name", uhcPlayer.getName());
        player_data.set("player-data." + uhcPlayer.getUUID() + ".money", 0);

        player_data.set("player-data." + uhcPlayer.getUUID() + ".wins", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".losses", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".kills", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".deaths", 0);

        player_data.set("player-data." + uhcPlayer.getUUID() + ".uhc-level", 0);
        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-exp", setRequiredExp());

        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public boolean hasStatistics() {
        return player_data.getConfigurationSection("player-data." + uhcPlayer.getUUID()) != null;
    }

    public int getMoney() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".money");
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
    public int getPreviousLevel() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".uhc-level")-1;
    }
    public int getRequiredExp() {
        return player_data.getInt("player-data." + uhcPlayer.getUUID() + ".required-exp");
    }
    public double setRequiredExp() {
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

        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-exp", setRequiredExp());
        player_data.set("player-data." + uhcPlayer.getUUID() + ".level", getUHCLevel()+level);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        // gameManager.getSoundManager().playLevelUP(player.getPlayer());

        uhcPlayer.sendMessage(Messages.LEVEL_UP.toString()
                .replace("%level%", String.valueOf(getUHCLevel()))
                .replace("%previous-level%", String.valueOf(getPreviousLevel())));
    }

    public void addUHCExp(double exp) {

        player_data.set("player-data." + uhcPlayer.getUUID() + ".required-exp", getRequiredExp()-exp);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();

        if (getRequiredExp() <= 0) {
            double over = Math.abs(getRequiredExp());
            addUHCLevel(1);
            player_data.set("player-data." + uhcPlayer.getUUID() + ".required-exp", getRequiredExp()-over);
            GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        }
    }

    public void addWin(int wins) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".wins", getWins()+wins);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public void addLose(int losses) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".losses", getLosses()+losses);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public void addKills(int kills) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".kills", getKills()+kills);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public void addDeaths(int deaths) {
        player_data.set("player-data." + uhcPlayer.getUUID() + ".deaths", getDeaths()+deaths);
        GameManager.getGameManager().getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public void addMoney(double money) {
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

    public void addStatisticsForGame() {
        double money = (uhcPlayer.isWinner() ? 500 : 150)
                + (uhcPlayer.getKills() > 0 ? uhcPlayer.getKills() * 60 : 0);
        double exp = (uhcPlayer.isWinner() ? 300 : 150) + (uhcPlayer.getKills() > 0 ? uhcPlayer.getKills() * 20 : 0);

        addMoney(money);
        addUHCExp(exp);
        addGameResult();
        addKills(uhcPlayer.getKills());
        addDeaths(!uhcPlayer.isWinner() ? 1 : 0);
    }

    public void showStatistics() {

    }


}
