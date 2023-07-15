package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.game.GameConst;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingCD extends BukkitRunnable {

    private static int countdown;

    public EndingCD() {
        countdown = GameConst.ENDING_COUNTDOWN;
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