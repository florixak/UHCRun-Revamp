package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnvilCommand implements CommandExecutor {

    private GameManager gameManager;

    public AnvilCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (!p.hasPermission("uhcrun.anvil")) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        // anvilAction.execute(plugin, p, null);
        return true;
    }
}
