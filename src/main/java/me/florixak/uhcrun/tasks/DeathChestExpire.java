package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.deathchest.DeathChest;
import me.florixak.uhcrun.deathchest.DeathChestManager;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathChestExpire extends BukkitRunnable {

    private DeathChestManager deathChestManager;
    private DeathChest deathChest;
    public int expireTime;

    public DeathChestExpire(DeathChest deathChest) {
        this.deathChest = deathChest;
        this.deathChestManager = GameManager.getGameManager().getDeathChestManager();
        expireTime = deathChestManager.getExpireTime();
    }

    @Override
    public void run() {
        if (expireTime <= 0) {
            cancel();
            GameManager.getGameManager().getDeathChestManager().removeDeathChest(deathChest);
            return;
        }
        expireTime--;
    }
}
