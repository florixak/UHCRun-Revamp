package me.florixak.uhcrun.hook;

import me.florixak.uhcrun.game.GameValues;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsHook {

    private static LuckPerms luckPerms = null;

    private LuckPermsHook() {
    }

    public static void setupLuckPerms() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().contains("LuckPerms")) {
                RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
                if (provider != null) luckPerms = provider.getProvider();
            }
        }
    }

    public static boolean hasLuckPerms() {
        return luckPerms != null && GameValues.CAN_USE_LUCKPERMS;
    }

    public static String getPrefix(Player player) {
        if (!hasLuckPerms())
            throw new UnsupportedOperationException("LuckPerms plugin not found or is disabled!");

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix;
    }

    public static String getSuffix(Player player) {
        if (!hasLuckPerms())
            throw new UnsupportedOperationException("Vault plugin not found!");

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        String prefix = user.getCachedData().getMetaData().getSuffix();
        return prefix;
    }
}
