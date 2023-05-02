package me.florixak.uhcrun.gui;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.kits.KitsGui;
import me.florixak.uhcrun.teams.TeamGui;

import java.util.ArrayList;

public class GuiManager {

    private ArrayList<Gui> guis;

    public GuiManager() {
        this.guis = new ArrayList<>();
    }

    public void loadInventories() {
        this.guis.add(new TeamGui());
        this.guis.add(new KitsGui());
    }

    public Gui getGui(String gui_name) {
        for (Gui gui : guis) {
            if (gui.getTitle().equalsIgnoreCase(gui_name)) {
                return gui;
            }
        }
        UHCRun.getInstance().getLogger().info("Gui " + gui_name + " does not exist!");
        return null;
    }
}
