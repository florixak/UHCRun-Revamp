package me.florixak.uhcrun.game.gui;

import java.util.HashMap;
import java.util.UUID;

public class GuiManager {

    private HashMap<UUID, Gui> guiHashMap;

    public GuiManager() {
        this.guiHashMap = new HashMap<>();
    }

    public void addInventory(UUID uuid, Gui gui) {
        guiHashMap.put(uuid, gui);
    }

    public void removeInventory(UUID uuid) {
        guiHashMap.remove(uuid);
    }

    public void onDisable() {
        for (Gui gui : guiHashMap.values()) {
            gui.close();
        }
        guiHashMap.clear();
    }
}
