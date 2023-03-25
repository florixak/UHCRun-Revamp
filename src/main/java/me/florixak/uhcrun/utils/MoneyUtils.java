package me.florixak.uhcrun.utils;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class MoneyUtils {

    public static String displayMoney(Player p){

        DecimalFormat format = new DecimalFormat("##,###,##0.00");
        String formatted;
        Economy economy = UHCRun.getEconomy();
        if (UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig()
                .getBoolean("use-Vault", true)) formatted = format.format(economy.getBalance(p));
        else formatted = format.format(UHCRun.plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA)
                    .getConfig().getInt(p.getUniqueId() + ".money"));
        return formatted;
    }

    public static String convertMoney(int money) {
        DecimalFormat format = new DecimalFormat("##,###,##0.00");
        String formatted = format.format(money);
        return formatted;
    }

    public static void takeMoney(Player player, double amount) {

        FileConfiguration config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        FileConfiguration statistics = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        Economy economy = UHCRun.getEconomy();

        if (config.getBoolean("use-Vault", true)) {
            EconomyResponse r = economy.withdrawPlayer(player, amount);
            if (!r.transactionSuccess()) {
                player.sendMessage(Messages.NO_MONEY.toString());
                return;
            }
        } else if (config.getBoolean("MySQL.enabled", true)) {
            if (UHCRun.plugin.data.getMoney(player.getUniqueId()) < amount) {
                player.sendMessage(Messages.NO_MONEY.toString());
                return;
            } else {
                UHCRun.plugin.data.addMoney(player.getUniqueId(), -amount);
            }
        } else {
            if (statistics.getInt("statistics." + player.getUniqueId() + ".money") < amount) {
                player.sendMessage(Messages.NO_MONEY.toString());
                return;
            } else {
                statistics.set("statistics." + player.getUniqueId() + ".money", UHCRun.plugin.getStatistics().getMoney(player.getUniqueId())-amount);
                UHCRun.plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).save();
            }
        }
    }
}
