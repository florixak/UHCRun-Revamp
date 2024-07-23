package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.gui.StatisticsGui;
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

        if (args.length == 0) {
            new StatisticsGui(gameManager, gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId())).open();
        }
        return true;
    }
}
