package me.florixak.uhcrun.manager.commandManager;

import me.florixak.uhcrun.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public abstract class PluginCommand implements CommandExecutor {

    private final CommandInfo commandInfo;

    public PluginCommand(){
        commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        Objects.requireNonNull(commandInfo, "Command must have CommandInfo annotations");
    }

    public CommandInfo getCommandInfo() {
        return commandInfo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        if (commandInfo.requiresPlayer()){
            if (!(sender instanceof Player)){
                sender.sendMessage(Messages.NOT_PLAYER.toString());
                return true;
            }

            execute((Player) sender, args);
            return true;
        }

        if (!commandInfo.permission().isEmpty()){
            if (!sender.hasPermission(commandInfo.permission())){
                sender.sendMessage(Messages.NO_PERM.toString());
                return true;
            }
        }

        execute(sender, args);
        return true;
    }

    public void execute(Player player, String[] args){}
    public void execute(CommandSender sender, String[] args){}

}