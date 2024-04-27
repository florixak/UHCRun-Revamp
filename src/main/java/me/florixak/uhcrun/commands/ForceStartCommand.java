package me.florixak.uhcrun.commands;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static me.florixak.uhcrun.utils.Permissions.Commands.FORCE_START;

public class ForceStartCommand implements CommandExecutor {

    private final GameManager gameManager;

    public ForceStartCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission(FORCE_START.getPermission())) {
            sender.sendMessage(Messages.NO_PERM.toString());
            return true;
        }

        if (!gameManager.getGameState().equals(GameState.LOBBY)) {
            if (gameManager.getGameState().equals(GameState.STARTING)) {
                sender.sendMessage(Messages.GAME_ALREADY_STARTING.toString());
                return true;
            }
            sender.sendMessage(Messages.CANT_USE_NOW.toString());
            return true;
        }

        gameManager.setForceStarted(true);
        gameManager.setGameState(GameState.STARTING);
        return true;
    }
}