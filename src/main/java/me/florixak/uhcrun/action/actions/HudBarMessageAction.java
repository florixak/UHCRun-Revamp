package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.config.Messages;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class HudBarMessageAction implements Action {

    @Override
    public String getIdentifier() {
        return "HUDBAR";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(Messages.CLICK_SELECT_INV.toString()));
    }
}
