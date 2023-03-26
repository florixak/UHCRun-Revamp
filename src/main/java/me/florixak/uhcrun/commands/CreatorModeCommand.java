package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CreatorModeCommand implements CommandExecutor {

    private UHCRun plugin;
    private FileConfiguration permissions;

    public CreatorModeCommand(UHCRun plugin) {
        this.plugin = plugin;
        this.permissions = plugin.getConfigManager().getFile(ConfigType.PERMISSIONS).getConfig();
        plugin.getCommand("creator").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        UHCPlayer p = (UHCPlayer) sender;

        if (!p.getPlayer().hasPermission(permissions.getString("permissions.start-item"))) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (!(plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING)) {
            p.sendMessage(Messages.CREATOR_BEFORE_START.toString());
            return true;
        }

        /*
        TODO CREATOR
        if (PlayerManager.isCreator(p)) {
            p.sendMessage(Messages.CREATOR_OFF.toString());
            p.getPlayer().setGameMode(GameMode.ADVENTURE);
            p.getPlayer().getInventory().clear();
            plugin.getKitsManager().getWaitingKit(p);
            return true;
        }*/

        p.sendMessage(Messages.CREATOR_ON.toString());

        p.getPlayer().setGameMode(GameMode.CREATIVE);
        p.getPlayer().getInventory().clear();
        plugin.getKitsManager().getCreatorKit(p);
        return false;
    }
}
