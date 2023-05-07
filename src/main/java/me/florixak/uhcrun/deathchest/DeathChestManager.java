package me.florixak.uhcrun.deathchest;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeathChestManager {

    private GameManager gameManager;
    private FileConfiguration config;

    private List<DeathChest> deathChests;
    private int expireTime;

    public DeathChestManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

        this.deathChests = new ArrayList<>();
        this.expireTime = config.getInt("settings.death-chest.expire");
    }

    public int getExpireTime() {
        return this.expireTime;
    }

    public void createDeathChest(Player p) {
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        DeathChest deathChest = new DeathChest(p.getLocation(), uhcPlayer.getName(), p.getInventory().getContents());

        this.deathChests.add(deathChest);
    }

    public void removeDeathChest(DeathChest deathChest) {
        this.deathChests.remove(deathChest);
        deathChest.remove();
    }
}
