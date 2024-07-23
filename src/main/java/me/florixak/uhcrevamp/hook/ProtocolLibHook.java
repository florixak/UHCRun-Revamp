package me.florixak.uhcrevamp.hook;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;

public class ProtocolLibHook {

    private static ProtocolManager protocolManager;

    public static void setupProtocolLib() {
        try {
            if (!hasProtocolLib()) {
                Bukkit.getLogger().info("ProtocolLib plugin not found! Please download it.");
                return;
            }
            protocolManager = ProtocolLibrary.getProtocolManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static boolean hasProtocolLib() {
        return Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;
    }
}
