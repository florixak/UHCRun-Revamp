package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatisticsManager {

    private GameManager gameManager;
    private FileConfiguration config, data;
    // private SQLGetter sqlGetter;

    public StatisticsManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.data = gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
        // this.sqlGetter = this.gameManager.data;
    }

    public void setData(UHCPlayer uhcPlayer) {

        if (data.getConfigurationSection("statistics." + uhcPlayer.getUUID().toString()) != null) return;

        data.set("statistics." + uhcPlayer.getUUID().toString() + ".name", uhcPlayer.getName());
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".level", 1);
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".requiredXP", gameManager.getLevelManager().setRequiredExp(uhcPlayer));
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".money", config.getDouble("string-coins"));
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".wins", 0);
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".kills", 0);
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".deaths", 0);

        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public double getMoney(UUID uuid) {
        Economy economy = UHCRun.getVault();
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("use-Vault", true)) {
            return economy.getBalance(p);
        } else {
            return data.getDouble("statistics." + p.getUniqueId() + ".money");
        }
    }

    public void addMoney(UHCPlayer p, double amount) {
        if (config.getBoolean("use-Vault", true)) {
            Economy economy = UHCRun.getVault();
            economy.depositPlayer(p.getPlayer(), amount);
            data.set("statistics." + p.getUUID().toString() + ".money", economy.getBalance(p.getPlayer()));
        } else {
            data.set("statistics." + p.getUUID().toString() + ".money", gameManager.getStatistics().getMoney(p.getUUID())+amount);
        }
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getWins(UHCPlayer player) {
        return data.getInt("statistics." + player.getUUID().toString() + ".wins");
    }

    public void addWin(UHCPlayer player) {
        data.set("statistics." + player.getUUID().toString() + ".wins", getWins(player)+1);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getKills(UHCPlayer player) {
        return data.getInt("statistics." + player.getUUID().toString() + ".kills");
    }

    public void addKill(UHCPlayer player) {

        data.set("statistics." + player.getUUID().toString() + ".kills", getKills(player)+1);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getDeaths(UHCPlayer player) {
        return data.getInt("statistics." + player.getUUID().toString() + ".deaths");
    }

    public void addDeath(UHCPlayer player) {
        data.set("statistics." + player.getUUID().toString() + ".deaths", getDeaths(player)+1);
        gameManager.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }
}
