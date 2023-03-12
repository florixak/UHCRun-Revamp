package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.entity.Player;

public class InfoAction implements Action {
    @Override
    public String getIdentifier() {
        return "INFO";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {
        player.sendMessage(TextUtils.color(data));
    }
}