package me.florixak.uhcrevamp.commands;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static me.florixak.uhcrevamp.game.Permissions.FORCE_SKIP;

public class ForceSkipCommand implements CommandExecutor {

    private final GameManager gameManager;

    public ForceSkipCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission(FORCE_SKIP.getPerm())) {
            sender.sendMessage(Messages.NO_PERM.toString());
            return true;
        }
        switch (gameManager.getGameState()) {
            case STARTING:
                Utils.broadcast(Messages.GAME_PHASE_FORCE_SKIPPED.toString().replace("%phase%", GameState.STARTING.toString()));
                Bukkit.getScheduler().getActiveWorkers().clear();
                Bukkit.getScheduler().getPendingTasks().clear();
                gameManager.setGameState(GameState.MINING);
                break;
            case MINING:
                Utils.broadcast(Messages.GAME_PHASE_FORCE_SKIPPED.toString().replace("%phase%", GameState.MINING.toString()));
                gameManager.setGameState(GameState.PVP);
                break;
            case PVP:
                Utils.broadcast(Messages.GAME_PHASE_FORCE_SKIPPED.toString().replace("%phase%", GameState.PVP.toString()));
                gameManager.setGameState(GameState.DEATHMATCH);
                break;
            case DEATHMATCH:
                Utils.broadcast(Messages.GAME_PHASE_FORCE_SKIPPED.toString().replace("%phase%", GameState.DEATHMATCH.toString()));
                gameManager.setGameState(GameState.ENDING);
                break;
            default:
                sender.sendMessage(Messages.CANT_USE_NOW.toString());
                break;
        }
        return true;
    }
}