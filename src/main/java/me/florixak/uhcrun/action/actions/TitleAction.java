package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.utility.TextUtil;
import org.bukkit.entity.Player;

public class TitleAction implements Action {
    @Override
    public String getIdentifier() {
        return "TITLE";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {
        player.sendTitle(TextUtil.color(data), TextUtil.color(""), 10, 100, 10);
    }
}
