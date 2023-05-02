package me.florixak.uhcrun.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TabManager {

    private GameManager gameManager;
    private FileConfiguration config;

    public TabManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    public void setPlayerList(Player p) {

        if (!config.getBoolean("settings.addons.use-ProtocolLib")
                || !config.getBoolean("settings.tablist.enabled")) {
            return;
        }

        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        final PacketContainer pc = pm.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

        pc.getChatComponents()
                .write(0, WrappedChatComponent.fromText(TextUtils.color(config.getString("settings.tablist.header"))))
                .write(1, WrappedChatComponent.fromText(TextUtils.color(config.getString("settings.tablist.footer"))));

        try {
            pm.sendServerPacket(p, pc);
        } catch(Exception e) {
            e.printStackTrace();
        }

        UHCPlayer hocPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        p.setPlayerListName(TextUtils.color(
                        (hocPlayer.hasTeam() ? TextUtils.color(hocPlayer.getTeam().getDisplayName()) + " &8| &f" : " ") + hocPlayer.getName()
                )
        );
    }
}
