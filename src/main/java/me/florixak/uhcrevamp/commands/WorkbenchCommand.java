package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.florixak.uhcrevamp.game.Permissions.WORKBENCH;

public class WorkbenchCommand implements CommandExecutor {

    private final GameManager gameManager;

    public WorkbenchCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (!p.hasPermission(WORKBENCH.getPerm())) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (!gameManager.isPlaying()) {
            p.sendMessage(Messages.CANT_USE_NOW.toString());
            return true;
        }

        p.openWorkbench(null, true);
        return false;
    }
}
