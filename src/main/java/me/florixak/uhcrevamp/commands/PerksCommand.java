package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.perks.Perk;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.MenuManager;
import me.florixak.uhcrevamp.gui.menu.PerksMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerksCommand implements CommandExecutor {

    private final GameManager gameManager;

    public PerksCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_PLAYER.toString());
            return true;
        }

        Player p = (Player) sender;
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());

        if (gameManager.isPlaying()) {
            p.sendMessage(Messages.CANT_USE_NOW.toString());
            return true;
        }

        if (args.length == 0) {
            new PerksMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
        } else if (args.length == 1) {
            Perk perk = gameManager.getPerksManager().getPerk(args[0]);
            if (perk == null) {
                new PerksMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
                return true;
            }
            uhcPlayer.setPerk(perk);
        } else {
            new PerksMenu(MenuManager.getMenuUtils(uhcPlayer)).open();
        }
        return true;
    }
}