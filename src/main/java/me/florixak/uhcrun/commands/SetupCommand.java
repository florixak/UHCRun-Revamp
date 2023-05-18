package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

    private GameManager gameManager;

    public SetupCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (!p.hasPermission("uhcrun.setup")) {
            p.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (args.length == 0) {
            p.sendMessage("Use help");
        }
        else if (args[0].equalsIgnoreCase("wait")) {
            if (args.length == 1) {
                p.sendMessage("Uncompleted command!");
            } else if (args[1].equalsIgnoreCase("set")) {

                p.sendMessage("Waiting lobby set!");
            } else if (args[1].equalsIgnoreCase("remove")) {

                p.sendMessage("Waiting lobby set!");
            } else {
                p.sendMessage("Uncompleted command! 2");
            }
        }
        else if (args[0].equalsIgnoreCase("end")) {
            if (args.length == 1) {
                p.sendMessage("Uncompleted command!");
            } else if (args[1].equalsIgnoreCase("set")) {

                p.sendMessage("Ending lobby set!");
            } else if (args[1].equalsIgnoreCase("remove")) {

                p.sendMessage("Ending lobby set!");
            } else {
                p.sendMessage("Uncompleted command! 2");
            }
        }
        return true;
    }
}
