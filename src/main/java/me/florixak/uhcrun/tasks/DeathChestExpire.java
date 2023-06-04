package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.game.deathchest.DeathChest;
import me.florixak.uhcrun.game.deathchest.DeathChestManager;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.TimeUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathChestExpire extends BukkitRunnable {

    private final DeathChestManager deathChestManager;
    private final DeathChest deathChest;
    public static int expireTime;

    public DeathChestExpire(DeathChest deathChest) {
        this.deathChest = deathChest;
        this.deathChestManager = GameManager.getGameManager().getDeathChestManager();
        expireTime = deathChestManager.getExpireTime();
    }

    @Override
    public void run() {
        if (expireTime <= 0) {
            cancel();
            if (deathChest.canExpire()) {
                deathChestManager.removeDeathChest(deathChest);
            }
            return;
        }
        expireTime--;
        deathChest.getHologram().setText(deathChest.getHologram().getText()
                .replace("%player%", deathChest.getPlayer().getName())
                .replace("%countdown%", TimeUtils.getFormattedTime(expireTime)));
    }
}
