package me.florixak.uhcrun.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage("Use help");
            return true;
        }


        if (args[0].equalsIgnoreCase("wlobby")) {

            if (args.length == 1) {
                p.sendMessage("Not completed command!");
                return true;
            }

            if (args[])



        }




        return true;
    }
}
