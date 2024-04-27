package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.florixak.uhcrun.utils.Permissions.NICKNAME;

public class NicknameCommand implements CommandExecutor {

    private final PlayerManager playerManager;

    public NicknameCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        UHCPlayer uhcPlayer = playerManager.getUHCPlayer(player.getUniqueId());

        if (!player.hasPermission(NICKNAME.getPerm())) {
            uhcPlayer.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (command.getName().equalsIgnoreCase("nick")) {
            if (args.length == 0) {
                uhcPlayer.sendMessage(Messages.INVALID_CMD.toString());
            } else if (args.length == 1) {
                String nick = args[0];
                uhcPlayer.setNickname(nick);
            } else {
                uhcPlayer.sendMessage(Messages.INVALID_CMD.toString());
            }
        }

        if (command.getName().equalsIgnoreCase("unnick")) {
            if (!uhcPlayer.hasNickname()) {
                uhcPlayer.sendMessage(Messages.NICK_NOT_NICKED.toString());
                return true;
            }
            uhcPlayer.setNickname(null);
        }
        return true;
    }
}