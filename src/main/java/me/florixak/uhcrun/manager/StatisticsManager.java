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
    private FileConfiguration config, statistics;
    private SQLGetter sqlGetter;

    public StatisticsManager(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.statistics = plugin.getConfigManager().getFile(ConfigType.STATISTICS).getConfig();
        this.sqlGetter = new SQLGetter(plugin);
    }

    public void setStatistics(Player p) {

        if (statistics.getConfigurationSection("statistics." + p.getUniqueId().toString()) != null) return;

        statistics.set("statistics." + p.getUniqueId().toString() + ".name", p.getName());

        statistics.set("statistics." + p.getUniqueId().toString() + ".level", 1);
        statistics.set("statistics." + p.getUniqueId().toString() + ".requiredXP", plugin.getLevelManager().setRequiredExp(p.getUniqueId()));
        statistics.set("statistics." + p.getUniqueId().toString() + ".money", config.getDouble("string-coins"));
        statistics.set("statistics." + p.getUniqueId().toString() + ".wins", 0);
        statistics.set("statistics." + p.getUniqueId().toString() + ".kills", 0);
        statistics.set("statistics." + p.getUniqueId().toString() + ".deaths", 0);

        statistics.set("statistics." + p.getUniqueId().toString() + ".kits.starter", true);
        statistics.set("statistics." + p.getUniqueId().toString() + ".kits.miner", false);
        statistics.set("statistics." + p.getUniqueId().toString() + ".kits.enchanter", false);
        statistics.set("statistics." + p.getUniqueId().toString() + ".kits.healer", false);
        statistics.set("statistics." + p.getUniqueId().toString() + ".kits.horse-rider", false);

        statistics.set("statistics." + p.getUniqueId().toString() + ".perks.strength", false);
        statistics.set("statistics." + p.getUniqueId().toString() + ".perks.regen", false);
        statistics.set("statistics." + p.getUniqueId().toString() + ".perks.speed", false);
        statistics.set("statistics." + p.getUniqueId().toString() + ".perks.invis", false);
        statistics.set("statistics." + p.getUniqueId().toString() + ".perks.fire-res", false);
        statistics.set("statistics." + p.getUniqueId().toString() + ".perks.end-pearl", false);
        statistics.set("statistics." + p.getUniqueId().toString() + ".perks.resist", false);

        plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
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
            return statistics.getDouble("statistics." + p.getUniqueId() + ".money");
        }
    }
    public void addMoney(Player p, double amount) {
        Economy economy = UHCRun.getEconomy();
        if (config.getBoolean("use-Vault", true)) {
            economy.depositPlayer(p, amount);
            statistics.set("statistics." + p.getUniqueId() + ".money", economy.getBalance(p));
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        } else if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addMoney(p.getUniqueId(), amount);
            statistics.set("statistics." + p.getUniqueId() + ".money", plugin.data.getMoney(p.getUniqueId()));
        } else {
            statistics.set("statistics." + p.getUniqueId() + ".money", plugin.getStatisticManager().getMoney(p.getUniqueId())+amount);
        }
        plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
    }
    public void takeMoney(Player p, double amount) {
        Economy economy = UHCRun.getEconomy();
        if (config.getBoolean("use-Vault", true)) {
            EconomyResponse r = economy.withdrawPlayer(p, amount);
            if (!r.transactionSuccess()) {
                p.sendMessage(Messages.NO_MONEY.toString());
                return;
            }
            statistics.set("statistics." + p.getUniqueId() + ".money", economy.getBalance(p));
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        } else if (config.getBoolean("MySQL.enabled", true)) {
            if (plugin.data.getMoney(p.getUniqueId()) < amount) {
                p.sendMessage(Messages.NO_MONEY.toString());
            } else {
                plugin.data.addMoney(p.getUniqueId(), -amount);
            }
        } else {
            if (statistics.getInt("statistics." + p.getUniqueId() + ".money") < amount) {
                p.sendMessage(Messages.NO_MONEY.toString());
            } else {
                statistics.set("statistics." + p.getUniqueId() + ".money", plugin.getStatisticManager().getMoney(p.getUniqueId())-amount);
                plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
            }
        }
    }

    public int getWins(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getWins(uuid);
        }
        else {
            return statistics.getInt("statistics." + p.getUniqueId() + ".wins");
        }
    }
    public void addWin(UUID uuid, int wins) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addWin(uuid, wins);
        }
        else {
            statistics.set("statistics." + uuid.toString() + ".wins", getWins(uuid)+wins);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }

    public int getKills(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getKills(uuid);
        }
        else {
            return statistics.getInt("statistics." + p.getUniqueId() + ".kills");
        }
    }
    public void addKill(UUID uuid, int kills) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addKill(uuid, kills);
        }
        else {
            statistics.set("statistics." + uuid.toString() + ".kills", getKills(uuid)+kills);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }

    public int getDeaths(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            return sqlGetter.getDeaths(uuid);
        }
        else {
            return statistics.getInt("statistics." + p.getUniqueId() + ".deaths");
        }
    }
    public void addDeath(UUID uuid, int deaths) {
        if (config.getBoolean("MySQL.enabled", true)) {
            plugin.data.addDeath(uuid, deaths);
        }
        else {
            statistics.set("statistics." + uuid.toString() + ".deaths", getDeaths(uuid)+deaths);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }

    public void addStarter(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addStarter(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".kits.starter", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveStarter(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveStarter(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".kits.starter");
        }
    }

    public void addMiner(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addMiner(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".kits.miner", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveMiner(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveMiner(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".kits.miner");
        }
    }

    public void addEnchanter(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addEnchanter(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".kits.enchanter", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveEnchanter(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveEnchanter(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".kits.enchanter");
        }
    }

    public void addHealer(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addHealer(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".kits.healer", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveHealer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveHealer(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".kits.healer");
        }
    }

    public void addHorseRider(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addHorseRider(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".kits.horse-rider", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveHorseRider(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveHorseRider(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".kits.horse-rider");
        }
    }


    public void addStrength(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addHorseRider(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".perks.strength", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveStrength(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveStrength(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".perks.strength");
        }
    }

    public void addRegeneration(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addHorseRider(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".perks.strength", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveRegeneration(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveStrength(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".perks.strength");
        }
    }

    public void addSpeed(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addHorseRider(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".perks.strength", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveSpeed(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveStrength(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".perks.strength");
        }
    }

    public void addInvisible(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addHorseRider(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".perks.strength", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveInvisible(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveStrength(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".perks.strength");
        }
    }

    public void addEnderPearl(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addHorseRider(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".perks.strength", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveEnderPearl(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveStrength(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".perks.strength");
        }
    }

    public void addFireResistance(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addHorseRider(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".perks.strength", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveFireResistance(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveStrength(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".perks.strength");
        }
    }

    public void addResistance(UUID uuid) {
        if (config.getBoolean("MySQL.enabled", true)) {
            sqlGetter.addHorseRider(uuid);
        }
        else {
            statistics.set("statistics." + Bukkit.getPlayer(uuid).getUniqueId() + ".perks.strength", true);
            plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
        }
    }
    public boolean haveResistance(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (config.getBoolean("MySQL.enabled", true)) {
            if (sqlGetter.haveStrength(uuid) == 0)
                return false;
            else
                return true;
        }
        else {
            return statistics.getBoolean("statistics." + p.getUniqueId() + ".perks.strength");
        }
    }



}
