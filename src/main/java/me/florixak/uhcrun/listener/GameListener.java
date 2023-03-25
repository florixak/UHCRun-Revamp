package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.TitleAction;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.events.GameEndEvent;
import me.florixak.uhcrun.events.GameKillEvent;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.perks.PerksManager;
import me.florixak.uhcrun.utils.CustomDropUtils;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.Bukkit;
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

import java.util.List;
import java.util.Random;

public class GameListener implements Listener {

    private UHCRun plugin;
    private FileConfiguration config, messages;
    private String world;
    private TitleAction titleAction;
    private String prefix;

    public GameListener(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.messages = plugin.getConfigManager().getFile(ConfigType.MESSAGES).getConfig();
        this.world = config.getString("game-world");
        this.prefix = messages.getString("Messages.prefix");
        this.titleAction = new TitleAction();
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        Player winner = event.getWinner();

        List<String> win_rewards = messages.getStringList("Messages.win-rewards");
        List<String> lose_rewards = messages.getStringList("Messages.lose-rewards");

        double money_for_win;
        double money_for_kills;
        double level_xp_for_win;
        double level_xp_for_kills;
        double money_for_lose;
        double level_xp_for_lose;

        Utils.broadcast(Messages.WINNER.toString().replace("%winner%", winner.getDisplayName()));

        for (Player p : Bukkit.getOnlinePlayers()) {

            money_for_kills = config.getDouble("coins-per-kill") * PlayerManager.kills.get(p.getUniqueId());
            level_xp_for_kills = config.getDouble("player-level.level-xp-per-kill")*PlayerManager.kills.get(p.getUniqueId());
            money_for_win = config.getDouble("coins-per-win");
            level_xp_for_win = config.getDouble("player-level.level-xp-per-win");

            money_for_lose = config.getDouble("coins-per-lose");
            level_xp_for_lose = config.getDouble("player-level.level-xp-per-lose");

            if (p == winner) {
                plugin.getStatistics().addWin(p.getUniqueId());
                plugin.getStatistics().addMoney(Bukkit.getPlayer(p.getUniqueId()), money_for_win + money_for_kills);
                plugin.getLevelManager().addPlayerLevel(p.getUniqueId(), level_xp_for_win + level_xp_for_kills);
                titleAction.execute(plugin, p, "Victory!");
                for (String reward : win_rewards) {
                    p.sendMessage(TextUtils.color(reward
                                    .replace("%coins-for-win%", String.valueOf(money_for_win))
                                    .replace("%coins-for-kills%", String.valueOf(money_for_kills))
                                    .replace("%level-xp-for-win%", String.valueOf(level_xp_for_win))
                                    .replace("%level-xp-for-kills%", String.valueOf(level_xp_for_kills))
                                    .replace("%prefix%", prefix)
                            )
                    );
                }
            }
            else {
                plugin.getStatistics().addMoney(Bukkit.getPlayer(p.getUniqueId()), money_for_lose+money_for_kills);
                plugin.getLevelManager().addPlayerLevel(p.getUniqueId(), level_xp_for_lose+level_xp_for_kills);
                titleAction.execute(plugin, p, "Game Over!");
                for (String reward : lose_rewards) {
                    p.sendMessage(TextUtils.color(reward
                                    .replace("%coins-for-lose%", String.valueOf(money_for_lose))
                                    .replace("%coins-for-kills%", String.valueOf(money_for_kills))
                                    .replace("%level-xp-for-lose%", String.valueOf(level_xp_for_lose))
                                    .replace("%level-xp-for-kills%", String.valueOf(level_xp_for_kills))
                                    .replace("%prefix%", prefix)
                            )
                    );
                }
                if (PlayerManager.isDead(p)) {
                    p.showPlayer(plugin, p);
                }
            }
        }
    }

    @EventHandler
    public void onGameKill(GameKillEvent event) {
        Player killer = event.getKiller();
        Player victim = event.getVictim();

        victim.getInventory().clear();

        victim.setHealth(victim.getMaxHealth());
        victim.setFoodLevel(20);
        victim.setLevel(0);
        victim.setTotalExperience(0);
        victim.giveExp(-victim.getTotalExperience());

        if (killer instanceof Player) {
            plugin.getGame().addKillTo(killer);
            PerksManager.givePerk(killer);

            killer.giveExp(config.getInt("xp-per-kill"));

            Utils.broadcast(Messages.KILL.toString().replace("%player%", victim.getDisplayName()).replace("%killer%", killer.getDisplayName()));
        } else {
            Utils.broadcast(Messages.DEATH.toString().replace("%player%", victim.getDisplayName()));
        }

        plugin.getGame().setSpectator(victim);

        plugin.getGame().addDeathTo(victim);
        plugin.getGame().checkGame();
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
