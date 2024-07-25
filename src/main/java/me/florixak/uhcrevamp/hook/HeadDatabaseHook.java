package me.florixak.uhcrevamp.hook;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class HeadDatabaseHook {

    private static HeadDatabaseAPI headDatabaseAPI = null;

    private HeadDatabaseHook() {
    }

    public static void setupHeadDatabase() {
        if (!GameValues.ADDONS.CAN_USE_HEADDATABASE) return;
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().contains("LuckPerms")) {
                headDatabaseAPI = new HeadDatabaseAPI();
            }
        }
        if (!hasHeadDatabase()) {
            Bukkit.getLogger().info("HeadDatabase not found! Disabling HeadDatabase support.");
        }
    }

    public static boolean hasHeadDatabase() {
        return headDatabaseAPI != null;
    }

    public static HeadDatabaseAPI getHeadDatabaseAPI() {
        return headDatabaseAPI;
    }
}
