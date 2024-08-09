package me.florixak.uhcrevamp.hook;

import me.florixak.uhcrevamp.game.GameValues;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsHook {

	private LuckPerms luckPerms = null;

	public LuckPermsHook() {
		setupLuckPerms();
	}

	public void setupLuckPerms() {
		if (!Bukkit.getServer().getPluginManager().isPluginEnabled("LuckPerms") || GameValues.ADDONS.CAN_USE_LUCKPERMS)
			return;
		try {
			final RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
			if (provider != null) luckPerms = provider.getProvider();

			if (!hasLuckPerms()) {
				Bukkit.getLogger().info("LuckPerms not found! Disabling LuckPerms support.");
			}
		} catch (final Exception e) {
			Bukkit.getLogger().info("LuckPerms not found! Disabling LuckPerms support.");
		}
	}

	public boolean hasLuckPerms() {
		return luckPerms != null;
	}

	public String getPrefix(final Player player) {
		if (!hasLuckPerms())
			return "";

		final User user = luckPerms.getUserManager().getUser(player.getUniqueId());
		if (user == null)
			return "";
		final String prefix = user.getCachedData().getMetaData().getPrefix();
		if (prefix == null)
			return "";
		return prefix;
	}

	public String getSuffix(final Player player) {
		if (!hasLuckPerms())
			return "";

		final User user = luckPerms.getUserManager().getUser(player.getUniqueId());
		if (user == null)
			return "";
		final String suffix = user.getCachedData().getMetaData().getSuffix();
		if (suffix == null)
			return "";
		return suffix;
	}
}
