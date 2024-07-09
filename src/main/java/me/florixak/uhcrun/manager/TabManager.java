package me.florixak.uhcrun.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.hook.ProtocolLibHook;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.entity.Player;

public class TabManager {

    private final GameManager gameManager;

    public TabManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setPlayerList(Player p) {
        if (!ProtocolLibHook.hasProtocolLib() || !GameValues.TABLIST_ENABLED) {
            return;
        }

        ProtocolManager pm = ProtocolLibHook.getProtocolManager();
        final PacketContainer pc = pm.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

        pc.getChatComponents()
                .write(0, WrappedChatComponent.fromText(TextUtils.color(GameValues.TABLIST_HEADER)))
                .write(1, WrappedChatComponent.fromText(TextUtils.color(GameValues.TABLIST_FOOTER)));

        try {
            pm.sendServerPacket(p, pc);
        } catch(Exception e) {
            e.printStackTrace();
        }

        UHCPlayer player = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        if (!GameValues.TEAM_MODE) {
            p.setPlayerListName(TextUtils.color(GameValues.TABLIST_SOLO_MODE
                    .replace("%player%", player.getName())
                    .replace("%team%", player.hasTeam() ? player.getTeam().getDisplayName() : "")
                    .replace("%rank%", player.getLuckPermsPrefix())
                    .replace("%uhc-level%", String.valueOf(player.getData().getUHCLevel())))
            );
        } else {
            p.setPlayerListName(TextUtils.color(GameValues.TABLIST_TEAM_MODE
                    .replace("%player%", player.getName())
                    .replace("%team%", player.hasTeam() ? player.getTeam().getDisplayName() : "")
                    .replace("%rank%", player.getLuckPermsPrefix())
                    .replace("%uhc-level%", String.valueOf(player.getData().getUHCLevel())))
            );
        }
    }
}