package me.florixak.uhcrevamp.hook;

import me.florixak.uhcrevamp.game.GameValues;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

	private Economy economy;

	public VaultHook() {
		setupEconomy();
	}

	public void setupEconomy() {
		if (!GameValues.ADDONS.CAN_USE_VAULT) return;
		if (!hasVault()) {
			Bukkit.getLogger().info("Vault plugin not found! Disabling Vault support.");
			return;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (rsp != null) economy = rsp.getProvider();
	}

	private boolean hasVault() {
		return Bukkit.getPluginManager().getPlugin("Vault") != null;
	}

	public boolean hasEconomy() {
		return economy != null;
	}

	public boolean hasAccount(Player target) {
		if (!hasEconomy())
			return false;
		return economy.hasAccount(target);
	}

	public double getBalance(Player target) {
		if (!hasEconomy())
			return -1.00;

		return economy.getBalance(target);
	}

	public String withdraw(String target, double amount) {
		if (!hasEconomy())
			return "ERROR";

		return economy.withdrawPlayer(target, amount).errorMessage;
	}

	public String deposit(String target, double amount) {
		if (!hasEconomy())
			return "ERROR";
		return economy.depositPlayer(target, amount).errorMessage;
	}

	public String formatCurrencySymbol(double amount) {
		if (!hasEconomy())
			return "ERROR";

		return economy.format(amount);
	}
}