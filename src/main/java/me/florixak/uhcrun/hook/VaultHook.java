package me.florixak.uhcrun.hook;

import me.florixak.uhcrun.game.GameValues;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economy = null;

    private VaultHook() {
    }

    public static void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) economy = rsp.getProvider();
    }

    public static boolean hasEconomy() {
        return economy != null || !GameValues.CAN_USE_VAULT;
    }

    public static double getBalance(OfflinePlayer target) {
        if (!hasEconomy())
            throw new UnsupportedOperationException("Vault plugin not found!");

        return economy.getBalance(target);
    }

    public static String withdraw(OfflinePlayer target, double amount) {
        if (!hasEconomy())
            throw new UnsupportedOperationException("Vault plugin not found!");

        return economy.withdrawPlayer(target, amount).errorMessage;
    }

    public static String deposit(OfflinePlayer target, double amount) {
        if (!hasEconomy())
            throw new UnsupportedOperationException("Vault plugin not found!");

        return economy.depositPlayer(target, amount).errorMessage;
    }

    public static String formatCurrencySymbol(double amount) {
        if (!hasEconomy())
            throw new UnsupportedOperationException("Vault plugin not found!");

        return economy.format(amount);
    }



}
