package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

public class AnvilAction implements Action {
    @Override
    public String getIdentifier() {
        return "ANVIL";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {

        AnvilGUI.Builder anvil = new AnvilGUI.Builder();
        anvil.plugin(UHCRun.plugin);
        anvil.open(player);
    }
}
