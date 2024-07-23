package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.utils.TimeUtils;
import me.florixak.uhcrevamp.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathmatchResist extends BukkitRunnable {

    private final GameManager gameManager;
    private static int countdown;

    public DeathmatchResist(GameManager gameManager) {
        this.gameManager = gameManager;
        countdown = gameManager.getDeathmatchManager().getPVPResistCD();
    }

    public static int getCountdown() {
        return countdown;
    }

    @Override
    public void run() {
        if (countdown <= 0) {
            cancel();
            gameManager.setPvP(true);
            Utils.broadcast(Messages.PVP.toString());
            return;
        }

        if (countdown <= 10) {
            Utils.broadcast(Messages.PVP_IN.toString()
                    .replace("%countdown%", TimeUtils.getFormattedTime(countdown)));
        }
        countdown--;
    }
}
