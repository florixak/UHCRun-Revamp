package me.florixak.uhcrun;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.hook.LuckPermsHook;
import me.florixak.uhcrun.hook.PAPIHook;
import me.florixak.uhcrun.hook.ProtocolLibHook;
import me.florixak.uhcrun.hook.VaultHook;
import me.florixak.uhcrun.manager.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;

public final class UHCRun extends JavaPlugin {

    private static UHCRun plugin;

    public static String nmsVer;
    public static boolean useOldMethods;

    private GameManager gameManager;

    @Override
    public void onLoad() {
        new WorldManager().createNewUHCWorld();
    }

    @Override
    public void onEnable() {
        plugin = this;

        getLogger().info(getDescription().getName());
        getLogger().info("Author: " + getAuthor());
        getLogger().info("Web: www.florixak.tk");
        getLogger().info("Version: " + getDescription().getVersion());

        checkNMSVersion();
        ProtocolLibHook.setupProtocolLib();

//        OreGeneratorTask oreGeneratorTask = new OreGeneratorTask(this);
//        oreGeneratorTask.runTaskTimer(this, 20L, 20L); // Spustí task každý tick (20 ticků = 1 sekunda)
        new WorldManager().createNewUHCWorld("world", WorldType.NORMAL, true);

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

    public static UHCRun getInstance() {
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