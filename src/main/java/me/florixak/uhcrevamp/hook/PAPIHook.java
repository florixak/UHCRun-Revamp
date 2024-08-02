package me.florixak.uhcrevamp.hook;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderExp;
import org.bukkit.Bukkit;

public class PAPIHook {

	public PAPIHook() {
	}

	public void setupPlaceholderAPI() {
		if (!hasPlaceholderAPI()) {
			Bukkit.getLogger().info("PlaceholderAPI plugin not found! Please download it, if you want to use it or disable in config.");
			return;
		}
		new PlaceholderExp(UHCRevamp.getInstance()).register();
	}

	private boolean hasPlaceholderAPI() {
		return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
	}
}
