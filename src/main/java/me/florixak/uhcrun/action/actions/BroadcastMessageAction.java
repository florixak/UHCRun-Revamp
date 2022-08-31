package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.utility.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastMessageAction implements Action {

    @Override
    public String getIdentifier() {
        return "BROADCAST";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(TextUtil.color(data));
        }
    }
}
