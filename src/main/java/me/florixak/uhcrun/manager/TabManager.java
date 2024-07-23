package me.florixak.uhcrun.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.hook.ProtocolLibHook;
import me.florixak.uhcrun.utils.placeholderapi.PlaceholderUtil;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.entity.Player;

public class TabManager {

    public TabManager() {
    }

    public void setPlayerList(Player p) {
        if (!GameValues.TABLIST.ENABLED) return;

        if (!ProtocolLibHook.hasProtocolLib() || !GameValues.ADDONS.CAN_USE_PROTOCOLLIB) {
            return;
        }

        ProtocolManager pm = ProtocolLibHook.getProtocolManager();
        final PacketContainer pc = pm.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

        pc.getChatComponents()
                .write(0, WrappedChatComponent.fromText(TextUtils.color(GameValues.TABLIST.HEADER)))
                .write(1, WrappedChatComponent.fromText(TextUtils.color(GameValues.TABLIST.FOOTER)));

        try {
            pm.sendServerPacket(p, pc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String tablist = !GameValues.TEAM.TEAM_MODE ? GameValues.TABLIST.SOLO_MODE : GameValues.TABLIST.TEAM_MODE;
        p.setPlayerListName(TextUtils.color(PlaceholderUtil.setPlaceholders(tablist, p)));
    }
}