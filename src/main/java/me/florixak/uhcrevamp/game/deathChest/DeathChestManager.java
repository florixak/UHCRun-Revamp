package me.florixak.uhcrevamp.game.deathChest;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DeathChestManager {

    private final GameManager gameManager;

    private final List<DeathChest> deathChests;

    public DeathChestManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.deathChests = new ArrayList<>();
    }

    public int getExpireTime() {
        return GameValues.DEATH_CHEST.HOLOGRAM_EXPIRE_TIME;
    }

    public boolean canExpire() {
        return getExpireTime() != -1;
    }

    public void createDeathChest(Player p, List<ItemStack> items) {
        if (gameManager.getPlayerManager().getAlivePlayers().size() <= 1) return;
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        DeathChest deathChest = new DeathChest(uhcPlayer, uhcPlayer.getDeathLocation(), items, canExpire());
        this.deathChests.add(deathChest);
    }

    public void removeDeathChest(DeathChest deathChest) {
        if (deathChest == null) return;
        deathChest.removeChest();
        this.deathChests.remove(deathChest);
    }

    public void onDisable() {
        if (deathChests == null || deathChests.isEmpty()) return;
        for (DeathChest deathChest : deathChests) {
            removeDeathChest(deathChest);
        }
    }
}
