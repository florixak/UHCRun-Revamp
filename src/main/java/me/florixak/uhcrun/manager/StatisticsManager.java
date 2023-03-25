package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
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

    public void setData(Player p) {

        if (data.getConfigurationSection("statistics." + p.getUniqueId().toString()) != null) return;

        data.set("statistics." + p.getUniqueId().toString() + ".name", p.getName());
        data.set("statistics." + p.getUniqueId().toString() + ".level", 1);
        data.set("statistics." + p.getUniqueId().toString() + ".requiredXP", plugin.getLevelManager().setRequiredExp(p.getUniqueId()));
        data.set("statistics." + p.getUniqueId().toString() + ".money", config.getDouble("string-coins"));
        data.set("statistics." + p.getUniqueId().toString() + ".wins", 0);
        data.set("statistics." + p.getUniqueId().toString() + ".kills", 0);
        data.set("statistics." + p.getUniqueId().toString() + ".deaths", 0);

        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public double getMoney(UUID uuid) {
        Economy economy = UHCRun.getEconomy();
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

    public void addMoney(Player p, double amount) {
        Economy economy = UHCRun.getEconomy();
        if (config.getBoolean("use-Vault", true)) {
            economy.depositPlayer(p, amount);
            data.set("statistics." + p.getUniqueId() + ".money", economy.getBalance(p));
            plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
        } else if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addMoney(p.getUniqueId(), amount);
            data.set("statistics." + p.getUniqueId() + ".money", plugin.data.getMoney(p.getUniqueId()));
        } else {
            data.set("statistics." + p.getUniqueId() + ".money", plugin.getStatistics().getMoney(p.getUniqueId())+amount);
        }
        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public void takeMoney(Player p, double amount) {
        Economy economy = UHCRun.getEconomy();
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
    }

    public int getWins(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getWins(uuid);
        }
        return data.getInt("statistics." + uuid.toString() + ".wins");
    }

    public void addWin(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addWin(uuid, 1);
            return;
        }
        data.set("statistics." + uuid.toString() + ".wins", getWins(uuid)+1);
        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getKills(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getKills(uuid);
        }
        return data.getInt("statistics." + uuid.toString() + ".kills");
    }

    public void addKill(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addKill(uuid, 1);
            return;
        }
        data.set("statistics." + uuid.toString() + ".kills", getKills(uuid)+1);
        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }

    public int getDeaths(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getDeaths(uuid);
        }
        return data.getInt("statistics." + uuid.toString() + ".deaths");
    }

    public void addDeath(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addDeath(uuid, 1);
            return;
        }
        data.set("statistics." + uuid.toString() + ".deaths", getDeaths(uuid)+1);
        plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
    }
}
