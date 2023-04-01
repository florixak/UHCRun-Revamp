package me.florixak.uhcrun.commands;

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

        if (args.length == 0) {
            uhcPlayer.sendMessage("Use /nick %nick%");
        } else if (args.length == 1) {
            uhcPlayer.setCustomNick(args[0]);
        } else {
            uhcPlayer.sendMessage("Use /help");
        }
        return true;
    }
}
