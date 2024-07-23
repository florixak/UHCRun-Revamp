package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingCD extends BukkitRunnable {

    private static int countdown;

    public EndingCD() {
        countdown = GameValues.GAME.ENDING_COUNTDOWN;
    }

    public static int getCountdown() {
        return countdown;
    }

    @Override
    public void run() {

        if (countdown <= 0) {
            cancel();
            Bukkit.shutdown();
            return;
        }
        countdown--;
    }
}