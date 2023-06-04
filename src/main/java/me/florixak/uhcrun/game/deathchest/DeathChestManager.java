package me.florixak.uhcrun.game.deathchest;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DeathChestManager {

    private final GameManager gameManager;
    private final FileConfiguration config;

    private List<DeathChest> deathChests;
    private String hologramText;

    public DeathChestManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

        this.deathChests = new ArrayList<>();
        this.hologramText = config.getString("settings.death-chest.hologram-text");
    }

    public int getExpireTime() {
        return config.getInt("settings.death-chest.expire");
    }
    public boolean willExpire() {
        return getExpireTime() != -1;
    }

    public String getHologramText() {
        return hologramText;
    }

    public void createDeathChest(Player p, List<ItemStack> items) {
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        DeathChest deathChest = new DeathChest(uhcPlayer, p.getLocation(), uhcPlayer.getName(), items, willExpire());

        this.deathChests.add(deathChest);
    }

    public void removeDeathChest(DeathChest deathChest) {
        if (deathChest == null) return;
        deathChest.remove();
        this.deathChests.remove(deathChest);
    }

    public void onDisable() {
        for (DeathChest deathChest : deathChests) {
            removeDeathChest(deathChest);
        }
    }
}
