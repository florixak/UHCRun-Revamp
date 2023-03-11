package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.utils.CustomDropUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GameListener implements Listener {

    private UHCRun plugin;
    private FileConfiguration config;
    private String world;

    public GameListener(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.world = config.getString("game-world");
    }

    @EventHandler
    public void blockDestroy(BlockBreakEvent event) {
        Player p = event.getPlayer();
//        Block block = event.getBlock();

        if (!plugin.getGame().isPlaying()) {
            event.setCancelled(true);
            p.sendMessage(Messages.CANT_BREAK.toString());
            return;
        }
        CustomDropUtils.dropItem(p, event);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (!plugin.getGame().isPlaying()) {
            p.sendMessage(Messages.CANT_PLACE.toString());
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void entityDrop(EntityDeathEvent event) {
        Random ran = new Random();
        int amount = 1;

        if (event.getEntity() instanceof Cow) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(Material.COOKED_BEEF, amount));
        }
        if (event.getEntity() instanceof Pig) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(Material.COOKED_BEEF, amount));
        }
        if (event.getEntity() instanceof Sheep) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(Material.COOKED_MUTTON, amount));
            event.getDrops().add(new ItemStack(Material.STRING, amount));
        }
        if (event.getEntity() instanceof Chicken) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(Material.COOKED_CHICKEN, amount));
            event.getDrops().add(new ItemStack(Material.STRING, amount));
        }
        if (event.getEntity() instanceof Rabbit) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(Material.COOKED_RABBIT, amount));
        }
    }

    @EventHandler
    public void timber(BlockBreakEvent event) {
        plugin.getUtilities().timber(event.getBlock());
    }

    @EventHandler
    public void damage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!plugin.getGame().isPlaying()) {
            event.setCancelled(true);
        }

        if (plugin.getGame().gameState == GameState.MINING) {
            if (event.getCause() == DamageCause.FIRE) {
                event.setCancelled(true);
            }
            if (event.getCause() == DamageCause.LAVA) {
                event.setCancelled(true);
            }
            if (event.getCause() == DamageCause.DROWNING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void entityHitEntity(EntityDamageByEntityEvent event) {
        if (!plugin.getGame().isPlaying()) {
            event.setCancelled(true);
        }
        if (plugin.getGame().gameState == GameState.MINING) {
            if (event.getEntity() instanceof Player) {
                if (event.getDamager() instanceof Player) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void hunger(FoodLevelChangeEvent event) {
        if (!plugin.getGame().isPlaying()) {
            Player p = (Player) event.getEntity();
            p.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING
                || plugin.getGame().gameState == GameState.DEATHMATCH
        ) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING
                || plugin.getGame().gameState == GameState.DEATHMATCH
        ) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void healthRegain(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void arrowHitHP(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            if (event.getEntity() instanceof Arrow) {
                Player shooter = (Player) event.getEntity().getShooter();
                Player enemy = (Player) event.getHitEntity();

                if (config.getBoolean("arrow-hit-hp", true)) {
                    shooter.sendMessage(Messages.SHOT_HP.toString()
                            .replace("%player%", enemy.getDisplayName())
                            .replace("%hp%", String.valueOf(enemy.getHealth())));
                }
            }
        }
    }

    @EventHandler
    public void noTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Monster) {
            if (event.getTarget() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void monsterSpawning(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Monster) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                event.setCancelled(true);
            }
        }
    }
}
