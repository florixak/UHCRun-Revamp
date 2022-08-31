package me.florixak.uhcrun.action;

import me.florixak.uhcrun.UHCRun;
import org.bukkit.entity.Player;

public interface Action {

    String getIdentifier();

    void execute(UHCRun plugin, Player player, String data);

}
