package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.WorkbenchAction;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.gameManager.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements CommandExecutor {

    private UHCRun plugin;
    private WorkbenchAction workbenchAction;
    private FileConfiguration permissions;

    public WorkbenchCommand(UHCRun plugin) {
        this.plugin = plugin;
        this.workbenchAction = new WorkbenchAction();
        this.permissions = plugin.getConfigManager().getFile(ConfigType.PERMISSIONS).getConfig();
        plugin.getCommand("workbench").setExecutor(this);
        plugin.getCommand("wb").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (!p.hasPermission(permissions.getString("permissions.workbench-command"))) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING || plugin.getGame().gameState == GameState.ENDING) {
            return true;
        }

        workbenchAction.execute(plugin, p, null);
        return false;
    }
}
