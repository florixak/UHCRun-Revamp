package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.sql.SQLGetter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatisticsManager {

    private UHCRun plugin;
    private FileConfiguration config, data;
    private SQLGetter sqlGetter;

    public StatisticsManager(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.data = plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
        this.sqlGetter = plugin.data;
    }

    public void setData(UHCPlayer uhcPlayer) {

        if (data.getConfigurationSection("statistics." + uhcPlayer.getUUID().toString()) != null) return;

        data.set("statistics." + uhcPlayer.getUUID().toString() + ".name", uhcPlayer.getName());
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".level", 1);
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".requiredXP", plugin.getLevelManager().setRequiredExp(uhcPlayer));
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".money", config.getDouble("string-coins"));
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".wins", 0);
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".kills", 0);
        data.set("statistics." + uhcPlayer.getUUID().toString() + ".deaths", 0);

        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public double getMoney(UUID uuid) {
        Economy economy = UHCRun.getVault();
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("use-Vault", true)) {
            return economy.getBalance(p);
        }
        else if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getMoney(uuid);
        }
        else {
            return data.getDouble("statistics." + p.getUniqueId() + ".money");
        }
    }

    public void addMoney(UHCPlayer p, double amount) {
        if (config.getBoolean("use-Vault", true)) {
            Economy economy = UHCRun.getVault();
            Player player = p.getPlayer();
            economy.depositPlayer(player, amount);
            data.set("statistics." + p.getUUID().toString() + ".money", economy.getBalance(player));
            plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        } else if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addMoney(p.getUUID(), amount);
            data.set("statistics." + p.getUUID().toString() + ".money", plugin.data.getMoney(p.getUUID()));
        } else {
            data.set("statistics." + p.getUUID().toString() + ".money", plugin.getStatistics().getMoney(p.getUUID())+amount);
        }
        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    /*public void takeMoney(UHCPlayer p, double amount) {
        Economy economy = UHCRun.getVault();
        if (config.getBoolean("use-Vault", true)) {
            EconomyResponse r = economy.withdrawPlayer(p, amount);
            if (!r.transactionSuccess()) {
                p.sendMessage(Messages.NO_MONEY.toString());
                return;
            }
            data.set("statistics." + p.getUniqueId() + ".money", economy.getBalance(p));
            plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        } else if (config.getBoolean("MySQL.enabled", true)) {
            if (plugin.data.getMoney(p.getUniqueId()) < amount) {
                p.sendMessage(Messages.NO_MONEY.toString());
            } else {
                plugin.data.addMoney(p.getUniqueId(), -amount);
            }
        } else {
            if (data.getInt("statistics." + p.getUniqueId() + ".money") < amount) {
                p.sendMessage(Messages.NO_MONEY.toString());
            } else {
                data.set("statistics." + p.getUniqueId() + ".money", plugin.getStatistics().getMoney(p.getUniqueId())-amount);
                plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
            }
        }
    }*/

    public int getWins(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getWins(player.getUUID());
        }
        return data.getInt("statistics." + player.getUUID().toString() + ".wins");
    }

    public void addWin(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addWin(player.getUUID(), 1);
            return;
        }
        data.set("statistics." + player.getUUID().toString() + ".wins", getWins(player)+1);
        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getKills(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getKills(player.getUUID());
        }
        return data.getInt("statistics." + player.getUUID().toString() + ".kills");
    }

    public void addKill(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addKill(player.getUUID(), 1);
            return;
        }
        data.set("statistics." + player.getUUID().toString() + ".kills", getKills(player)+1);
        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getDeaths(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getDeaths(player.getUUID());
        }
        return data.getInt("statistics." + player.getUUID().toString() + ".deaths");
    }

    public void addDeath(UHCPlayer player) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addDeath(player.getUUID(), 1);
            return;
        }
        data.set("statistics." + player.getUUID().toString() + ".deaths", getDeaths(player)+1);
        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }
}
