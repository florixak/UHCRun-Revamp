package me.florixak.uhcrevamp.hook;

import me.florixak.uhcrevamp.game.GameValues;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economy = null;

    private VaultHook() {
    }

    public static void setupEconomy() {
        if (!GameValues.ADDONS.CAN_USE_VAULT) return;
        if (!hasVault()) {
            Bukkit.getLogger().info("Vault plugin not found! Disabling Vault support.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) economy = rsp.getProvider();
    }

    public static boolean hasVault() {
        return Bukkit.getPluginManager().getPlugin("Vault") != null;
    }

    public static boolean hasEconomy() {
        return economy != null;
    }

    public static double getBalance(Player target) {
        if (!hasEconomy())
            return -1.00;

        return economy.getBalance(target);
    }

    public static String withdraw(Player target, double amount) {
        if (!hasEconomy())
            return "ERROR";

        return economy.withdrawPlayer(target, amount).errorMessage;
    }

    public static String deposit(Player target, double amount) {
        if (!hasEconomy())
            return "ERROR";

        return economy.depositPlayer(target, amount).errorMessage;
    }

    public static String formatCurrencySymbol(double amount) {
        if (!hasEconomy())
            return "ERROR";

        return economy.format(amount);
    }


}
