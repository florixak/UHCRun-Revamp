package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static me.florixak.uhcrevamp.game.Permissions.FORCE_START;

public class ForceStartCommand implements CommandExecutor {

    private final GameManager gameManager;

    public ForceStartCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission(FORCE_START.getPerm())) {
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
        } else {
            if (gameManager.getPlayerManager().getOnlinePlayers().size() < GameValues.GAME.PLAYERS_TO_START) {
                sender.sendMessage(Messages.NOT_ENOUGH_PLAYERS.toString());
                return true;
            }

        }

        Utils.broadcast(Messages.GAME_FORCE_STARTED.toString());
        gameManager.setGameState(GameState.STARTING);
        return true;
    }
}