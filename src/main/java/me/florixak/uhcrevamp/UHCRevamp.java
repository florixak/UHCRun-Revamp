package me.florixak.uhcrevamp;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.hook.LuckPermsHook;
import me.florixak.uhcrevamp.hook.PAPIHook;
import me.florixak.uhcrevamp.hook.ProtocolLibHook;
import me.florixak.uhcrevamp.hook.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class UHCRevamp extends JavaPlugin {

    private static UHCRevamp plugin;

    public static String nmsVer;
    public static boolean useOldMethods;

    private GameManager gameManager;

    @Override
    public void onEnable() {
        plugin = this;

        getLogger().info(getDescription().getName());
        getLogger().info("Author: " + getAuthor());
        getLogger().info("Web: www.github.com/florixak");
        getLogger().info("Version: " + getDescription().getVersion());

        checkNMSVersion();
        ProtocolLibHook.setupProtocolLib();

        this.gameManager = new GameManager(this);

        registerDependency();
        getGameManager().loadNewGame();
    }

    @Override
    public void onDisable() {
        getGameManager().onDisable();
    }

    public String getAuthor() {
        return getDescription().getAuthors().get(0);
    }

    public static UHCRevamp getInstance() {
        return plugin;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    private void registerDependency() {
        if (GameValues.ADDONS.CAN_USE_VAULT)
            VaultHook.setupEconomy();

        if (GameValues.ADDONS.CAN_USE_LUCKPERMS)
            LuckPermsHook.setupLuckPerms();

        if (GameValues.ADDONS.CAN_USE_PLACEHOLDERAPI)
            PAPIHook.setupPlaceholderAPI();
    }

    public void checkNMSVersion() {
        nmsVer = Bukkit.getServer().getClass().getPackage().getName();
        nmsVer = nmsVer.substring(nmsVer.lastIndexOf(".") + 1);

        if (nmsVer.equalsIgnoreCase("v1_8_R1") || nmsVer.startsWith("v1_7_")) {
            useOldMethods = true;
        }
    }

}