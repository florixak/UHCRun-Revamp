package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.gui.Gui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatisticsCommand implements CommandExecutor {
    
    private final GameManager gameManager;
    
    public StatisticsCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_PLAYER.toString());
            return true;
        }

        Player p = (Player) sender;
        Gui stats_gui = gameManager.getGuiManager().getInventory("statistics");

        if (args.length == 0) {
            stats_gui.openInv(p);
        } else {
            stats_gui.openInv(p);
        }


        return true;
    }
}
