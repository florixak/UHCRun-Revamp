package me.florixak.uhcrevamp.hook;

import me.florixak.uhcrevamp.game.GameValues;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsHook {

	private LuckPerms luckPerms = null;

	public LuckPermsHook() {
	}

	public void setupLuckPerms() {
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (plugin.getName().contains("LuckPerms")) {
				RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
				if (provider != null) luckPerms = provider.getProvider();
			}
		}
		if (!hasLuckPerms() && GameValues.ADDONS.CAN_USE_LUCKPERMS) {
			Bukkit.getLogger().info("LuckPerms not found! Disabling LuckPerms support.");
		}
	}

	public boolean hasLuckPerms() {
		return luckPerms != null;
	}

	public String getPrefix(Player player) {
		if (!hasLuckPerms())
			return "";

		User user = luckPerms.getUserManager().getUser(player.getUniqueId());
		String prefix = user.getCachedData().getMetaData().getPrefix();
		return prefix;
	}

	public String getSuffix(Player player) {
		if (!hasLuckPerms())
			return "";

		User user = luckPerms.getUserManager().getUser(player.getUniqueId());
		String prefix = user.getCachedData().getMetaData().getSuffix();
		return prefix;
	}
}
