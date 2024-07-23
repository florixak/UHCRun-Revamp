package me.florixak.uhcrevamp.tasks;

import me.florixak.uhcrevamp.game.deathchest.DeathChest;
import me.florixak.uhcrevamp.utils.TimeUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathChestExpire extends BukkitRunnable {

    private final DeathChest deathChest;
    private int expireTime;

    public DeathChestExpire(DeathChest deathChest) {
        this.deathChest = deathChest;
        this.expireTime = deathChest.getExpireTime();
    }

    public int getExpireTime() {
        return this.expireTime;
    }

    @Override
    public void run() {
        if (expireTime <= 0) {
            cancel();
            deathChest.removeChest();
            return;
        }
        deathChest.getHologram().setText(deathChest.getHologram().getText()
                .replace("%player%", deathChest.getPlayer().getName())
                .replace("%countdown%", TimeUtils.getFormattedTime(expireTime)));
        this.expireTime--;
    }
}