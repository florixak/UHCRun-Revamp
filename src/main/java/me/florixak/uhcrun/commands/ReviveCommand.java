package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.florixak.uhcrun.game.Permissions.REVIVE;

public class ReviveCommand implements CommandExecutor {

    private final GameManager gameManager;

    public ReviveCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission(REVIVE.getPerm())) {
            sender.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (!gameManager.isPlaying() || gameManager.isEnding()) {
            sender.sendMessage(Messages.CANT_USE_NOW.toString());
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot revive herself...");
                return true;
            }
            UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(((Player) sender).getUniqueId());

            if (uhcPlayer.isAlive()) {
                sender.sendMessage("You are alive!");
                return true;
            }
            uhcPlayer.revive();
            sender.sendMessage("You revived yourself!");
        } else if (args.length == 1) {
            UHCPlayer target = gameManager.getPlayerManager().getUHCPlayer(args[0]);
            if (!target.isOnline()) {
                sender.sendMessage(Messages.OFFLINE_PLAYER.getPath());
                return true;
            }
            if (target.isAlive()) {
                sender.sendMessage("Player is alive!");
                return true;
            }
            target.revive();
            target.sendMessage("You were revived by " + sender.getName());
            sender.sendMessage(target.getName() + " was revived!");
        } else {
            sender.sendMessage("Wrong usage... /revive %player%");
        }
        return true;
    }
}
