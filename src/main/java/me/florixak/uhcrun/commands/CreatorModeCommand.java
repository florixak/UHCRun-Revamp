package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.KitsManager;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.commandManager.CommandInfo;
import me.florixak.uhcrun.manager.commandManager.PluginCommand;
import me.florixak.uhcrun.manager.gameManager.GameState;
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

        Player p = (Player) sender;

        if (!p.hasPermission(permissions.getString("permissions.start-item"))) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (!(plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING)) {
            p.sendMessage(Messages.CREATOR_BEFORE_START.toString());
            return true;
        }

        if (PlayerManager.isCreator(p)) {
            PlayerManager.creator.remove(p.getUniqueId());
            p.sendMessage(Messages.CREATOR_OFF.toString());
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().clear();
            KitsManager.getWaitingKit(p);
            return true;
        }

        PlayerManager.creator.add(p.getUniqueId());
        p.sendMessage(Messages.CREATOR_ON.toString());

        p.setGameMode(GameMode.CREATIVE);
        p.getInventory().clear();
        KitsManager.getCreatorKit(p);
        return false;
    }
}
