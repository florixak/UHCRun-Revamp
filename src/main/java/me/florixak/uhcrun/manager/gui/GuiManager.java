package me.florixak.uhcrun.manager.gui;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.kits.KitsGui;
import me.florixak.uhcrun.game.perks.PerksGui;
import me.florixak.uhcrun.teams.TeamGui;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    private GameManager gameManager;
    private ArrayList<Gui> guis;

    public GuiManager(GameManager gameManager) {
        this.gameManager = gameManager;

        this.guis = new ArrayList<>();
    }

    public void loadInventories() {
        this.guis.add(new TeamGui(gameManager));
        this.guis.add(new KitsGui(gameManager));
        this.guis.add(new StatisticsGui(gameManager));
        this.guis.add(new PerksGui(gameManager));
    }

    public Gui getInventory(String gui_name) {
        for (Gui gui : getInventories()) {
            if (gui.getTitle().equalsIgnoreCase(gui_name)) {
                return gui;
            }
        }
        UHCRun.getInstance().getLogger().info("Gui " + gui_name + " does not exist!");
        return null;
    }

    public List<Gui> getInventories() {
        return this.guis;
    }
}
