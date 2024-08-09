package me.florixak.uhcrevamp.hook;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderExp;
import org.bukkit.Bukkit;

public class PAPIHook {

	private final UHCRevamp plugin;

	public PAPIHook(final UHCRevamp plugin) {
		this.plugin = plugin;
		setupPlaceholderAPI();
	}

	public void setupPlaceholderAPI() {
		if (!GameValues.ADDONS.CAN_USE_PLACEHOLDERAPI) {
			return;
		}
		if (!hasPlaceholderAPI()) {
			Bukkit.getLogger().info("PlaceholderAPI plugin not found! Disabling PAPI support.");
			return;
		}
		new PlaceholderExp(plugin).register();
	}

	public boolean hasPlaceholderAPI() {
		return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
	}
}
