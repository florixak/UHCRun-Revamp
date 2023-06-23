package me.florixak.uhcrun.tasks;

import me.florixak.uhcrun.game.deathchest.DeathChest;
import me.florixak.uhcrun.game.deathchest.DeathChestManager;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.TimeUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathChestExpire extends BukkitRunnable {

    /*

    [15:57:31 WARN]: [UHCRun] Task #28 for UHCRun v1.0 generated an exception
        java.lang.ClassCastException: class org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock cannot be cast to class org.bukkit.block.Chest (org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock and org.bukkit.block.Chest are in unnamed module of loader 'app')
        at me.florixak.uhcrun.game.deathchest.DeathChest.removeChest(DeathChest.java:104) ~[?:?]
        at me.florixak.uhcrun.game.deathchest.DeathChestManager.removeDeathChest(DeathChestManager.java:49) ~[?:?]
        at me.florixak.uhcrun.tasks.DeathChestExpire.run(DeathChestExpire.java:29) ~[?:?]
        at org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftTask.run(CraftTask.java:64) ~[patched_1.12.2.jar:git-Paper-1620]
        at org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftScheduler.mainThreadHeartbeat(CraftScheduler.java:423) ~[patched_1.12.2.jar:git-Paper-1620]
        at net.minecraft.server.v1_12_R1.MinecraftServer.D(MinecraftServer.java:840) ~[patched_1.12.2.jar:git-Paper-1620]
        at net.minecraft.server.v1_12_R1.DedicatedServer.D(DedicatedServer.java:423) ~[patched_1.12.2.jar:git-Paper-1620]
        at net.minecraft.server.v1_12_R1.MinecraftServer.C(MinecraftServer.java:774) ~[patched_1.12.2.jar:git-Paper-1620]
        at net.minecraft.server.v1_12_R1.MinecraftServer.run(MinecraftServer.java:666) ~[patched_1.12.2.jar:git-Paper-1620]
        at java.lang.Thread.run(Thread.java:833) [?:?]

     */

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
