package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.TimeUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathmatchResist extends BukkitRunnable {

    private GameManager gameManager;
    public static int countdown;

    public DeathmatchResist(GameManager gameManager) {
        this.gameManager = gameManager;
        countdown = gameManager.getDeathmatchManager().getPVPResistCD();
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
