package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.deathchest.DeathChest;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathChestExpire extends BukkitRunnable {

    private DeathChest deathChest;
    public int expireTime;

    public DeathChestExpire(DeathChest deathChest) {
        this.deathChest = deathChest;
        this.expireTime = GameManager.getGameManager().getDeathChestManager().getExpireTime();
    }

    @Override
    public void run() {
        if (expireTime <= 0 || deathChest.isEmpty()) {
            cancel();
            GameManager.getGameManager().getDeathChestManager().removeDeathChest(deathChest);
            UHCRun.getInstance().getLogger().info("Chest expired!");
            return;
        }
        expireTime--;
    }
}
