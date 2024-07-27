package me.florixak.uhcrevamp.gui;

import me.florixak.uhcrevamp.game.player.UHCPlayer;

import java.util.HashMap;

public class MenuManager {

    private static final HashMap<UHCPlayer, MenuUtils> menuUtilsMap = new HashMap<>();

    public static MenuUtils getMenuUtils(UHCPlayer uhcPlayer) {
        if (!menuUtilsMap.containsKey(uhcPlayer)) {
            menuUtilsMap.put(uhcPlayer, new MenuUtils(uhcPlayer));
        }
        return menuUtilsMap.get(uhcPlayer);
    }
}
