package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CustomNickCommand implements CommandExecutor {

    private PlayerManager playerManager;

    public CustomNickCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        UHCPlayer uhcPlayer = playerManager.getUHCPlayer(player.getUniqueId());

        if (!player.hasPermission("uhcrun.nick")) {
            uhcPlayer.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (command.getName().equalsIgnoreCase("nick")) {
            if (args.length == 0) {
                uhcPlayer.sendMessage(Messages.INVALID_CMD.toString());
            } else if (args.length == 1) {
                String nick = args[0];
                uhcPlayer.setCustomNick(nick);
            } else {
                uhcPlayer.sendMessage(Messages.INVALID_CMD.toString());
            }
        }

        if (command.getName().equalsIgnoreCase("unnick")) {
            if (!uhcPlayer.hasCustomNick()) {
                uhcPlayer.sendMessage(Messages.NO_NICK.toString());
                return true;
            }
            uhcPlayer.sendMessage(Messages.UNNICK.toString());
            uhcPlayer.setCustomNick(null);
        }
        return true;
    }
}