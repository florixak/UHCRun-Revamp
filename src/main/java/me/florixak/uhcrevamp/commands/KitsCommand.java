package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.kits.Kit;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.gui.KitsGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitsCommand implements CommandExecutor {

    private final GameManager gameManager;

    public KitsCommand(GameManager gameManager) {
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
            new KitsGui(gameManager, uhcPlayer).open();
        } else if (args.length == 1) {
            Kit kit = gameManager.getKitsManager().getKit(args[0]);
            if (kit == null) {
                new KitsGui(gameManager, uhcPlayer).open();
                return true;
            }
            uhcPlayer.setKit(kit);
        } else {
            new KitsGui(gameManager, uhcPlayer).open();
        }
        return true;
    }
}