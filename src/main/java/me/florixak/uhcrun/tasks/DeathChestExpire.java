package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.game.deathChest.DeathChest;
import me.florixak.uhcrun.game.deathChest.DeathChestManager;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.TimeUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathChestExpire extends BukkitRunnable {

    private final DeathChestManager deathChestManager;
    private final DeathChest deathChest;
    private int expireTime;

    public DeathChestExpire(DeathChest deathChest) {
        this.deathChest = deathChest;
        this.deathChestManager = GameManager.getGameManager().getDeathChestManager();
        this.expireTime = deathChestManager.getExpireTime();
    }

    public int getExpireTime() {
        return this.expireTime;
    }

    @Override
    public void run() {
        if (expireTime <= 0) {
            cancel();
            deathChestManager.removeDeathChest(deathChest);
            return;
        }
        deathChest.getHologram().setText(deathChest.getHologram().getText()
                .replace("%player%", deathChest.getPlayer().getName())
                .replace("%countdown%", TimeUtils.getFormattedTime(expireTime)));
        this.expireTime--;
    }
}
