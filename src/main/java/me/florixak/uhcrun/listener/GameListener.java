package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.inventory.InventoryItem;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.utility.ItemStackBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
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
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Random ran = new Random();
        int amount = 1;

        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING) {
            event.setCancelled(true);
            p.sendMessage(Messages.CANT_BREAK.toString());
            return;
        }

        if (config.getBoolean("custom-ore-drops", true)) {
            if (block.getType() == Material.COAL_ORE || block.getType() == Material.DEEPSLATE_COAL_ORE) {
                event.setDropItems(false);
                amount = ran.nextInt(8)+2;
                loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.TORCH, amount));
                p.giveExp(config.getInt("xp-per-coal-ore"));
                plugin.getSoundManager().playOreDestroySound(p);
            }
            if (block.getType() == Material.IRON_ORE || block.getType() == Material.DEEPSLATE_IRON_ORE) {
                event.setDropItems(false);
                amount = ran.nextInt(1)+2;
                loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.IRON_INGOT, amount));
                p.giveExp(config.getInt("xp-per-iron-ore"));
                plugin.getSoundManager().playOreDestroySound(p);
            }
            if (block.getType() == Material.GOLD_ORE || block.getType() == Material.DEEPSLATE_GOLD_ORE) {
                event.setDropItems(false);
                amount = ran.nextInt(1)+2;
                loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.GOLD_INGOT, amount));
                p.giveExp(config.getInt("xp-per-gold-ore"));
                plugin.getSoundManager().playOreDestroySound(p);
            }
            if (block.getType() == Material.REDSTONE_ORE || block.getType() == Material.DEEPSLATE_REDSTONE_ORE) {
                event.setDropItems(false);
                int random = ran.nextInt(5);

                switch (random) {
                    case 0:
                        amount = ran.nextInt(4)+2;
                        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.NETHER_WART, amount));
                        break;
                    case 1:
                        amount = ran.nextInt(1)+2;
                        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.BLAZE_POWDER, amount));
                        break;
                    case 2:
                        amount = ran.nextInt(2)+2;
                        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.SUGAR, amount));
                        break;
                    case 3:
                        amount = ran.nextInt(2)+2;
                        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.GLOWSTONE_DUST, amount));
                        break;
                    default:
                        amount = ran.nextInt(4)+2;
                        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.BREWING_STAND, amount));
                        break;
                }
                p.giveExp(config.getInt("xp-per-redstone-ore"));
                plugin.getSoundManager().playOreDestroySound(p);
            }
            if (block.getType() == Material.DIAMOND_ORE || block.getType() == Material.DEEPSLATE_DIAMOND_ORE) {
                event.setDropItems(false);
                amount = ran.nextInt(2)+2;
                loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.DIAMOND, amount));
                p.giveExp(config.getInt("xp-per-diamond-ore"));
                plugin.getSoundManager().playOreDestroySound(p);
            }
            if (block.getType() == Material.EMERALD_ORE || block.getType() == Material.DEEPSLATE_EMERALD_ORE) {
                event.setDropItems(false);
                p.giveExp(config.getInt("xp-per-emerald-ore"));
                plugin.getSoundManager().playOreDestroySound(p);
            }

            if (block.getType() == Material.OAK_LEAVES) {
                int random = ran.nextInt(100);

                if (random <= 40) {
                    amount = ran.nextInt(1)+1;
                    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, amount));
                }
            }
            if (block.getType() == Material.BIRCH_LEAVES) {
                int random = ran.nextInt(100);

                if (random <= 40) {
                    amount = ran.nextInt(1)+1;
                    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, amount));
                }
            }
            if (block.getType() == Material.JUNGLE_LEAVES) {
                int random = ran.nextInt(100);

                if (random <= 40) {
                    amount = ran.nextInt(1)+1;
                    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, amount));
                }
            }
            if (block.getType() == Material.ACACIA_LEAVES) {
                int random = ran.nextInt(100);

                if (random <= 40) {
                    amount = ran.nextInt(1)+1;
                    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, amount));
                }
            }
            if (block.getType() == Material.DARK_OAK_LEAVES) {
                int random = ran.nextInt(100);

                if (random <= 40) {
                    amount = ran.nextInt(1)+1;
                    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, amount));
                }
            }
            if (block.getType() == Material.SPRUCE_LEAVES) {
                int random = ran.nextInt(100);

                if (random <= 40) {
                    amount = ran.nextInt(1)+1;
                    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, amount));
                }
            }

            if (block.getType() == Material.GRAVEL) {
                event.setDropItems(false);
                amount = ran.nextInt(8)+4;
                loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.ARROW, amount));
                p.giveExp(config.getInt("xp-per-gravel"));
                plugin.getSoundManager().playOreDestroySound(p);
            }
            if (block.getType() == Material.SAND) {
                event.setDropItems(false);
                amount = ran.nextInt(2)+2;
                loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.GLASS, amount));
                p.giveExp(config.getInt("xp-per-gravel"));
                plugin.getSoundManager().playOreDestroySound(p);
            }

//            FileConfiguration cd = plugin.getConfigManager().getFile(ConfigType.CUSTOM_DROPS).getConfig();























        }





    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if ((plugin.getGame().gameState == GameState.WAITING)
                || (plugin.getGame().gameState == GameState.STARTING)
                || (plugin.getGame().gameState == GameState.ENDING)) {
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

        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING) {
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
        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING) {
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
        if (plugin.getGame().gameState == GameState.WAITING
                || plugin.getGame().gameState == GameState.STARTING
                || plugin.getGame().gameState == GameState.ENDING) {
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
        if (event.getEntity() instanceof Mob) {
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
