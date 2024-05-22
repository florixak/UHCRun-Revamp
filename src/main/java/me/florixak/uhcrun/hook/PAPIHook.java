package me.florixak.uhcrun.hook;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.utils.placeholderapi.PlaceholderExp;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Bukkit;

public class PAPIHook {

    private PAPIHook() {
    }

    public static void setupPlaceholderAPI() {
        if (!hasPlaceholderAPI()){
            Bukkit.getLogger().info(TextUtils.color("&cPlaceholderAPI plugin not found."));
            return;
        }
        new PlaceholderExp(UHCRun.getInstance()).register();
    }

    public static boolean hasPlaceholderAPI() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
