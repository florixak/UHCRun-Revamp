package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AnvilCommand implements CommandExecutor {

    private UHCRun plugin;
    private AnvilAction anvilAction;
    private FileConfiguration permissions;

    public AnvilCommand(UHCRun plugin) {
        this.plugin = plugin;
        this.anvilAction = new AnvilAction();
        this.permissions = plugin.getConfigManager().getFile(ConfigType.PERMISSIONS).getConfig();
        plugin.getCommand("anvil").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (!p.hasPermission(permissions.getString("permissions.anvil-command"))) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        anvilAction.execute(plugin, p, null);
        return false;
    }
}
