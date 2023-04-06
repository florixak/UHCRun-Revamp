package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.TitleAction;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.events.GameEndEvent;
import me.florixak.uhcrun.events.GameKillEvent;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
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
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class GameListener implements Listener {

    private UHCRun plugin;
    private FileConfiguration config, messages;
    private TitleAction titleAction;
    private String prefix;

    public GameListener(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.messages = plugin.getConfigManager().getFile(ConfigType.MESSAGES).getConfig();
        this.prefix = messages.getString("Messages.prefix");
        this.titleAction = new TitleAction();
    }

    @EventHandler
    public void handleGameEnd(GameEndEvent event) {
        UHCPlayer winner = event.getWinner();

        List<String> win_rewards = messages.getStringList("Messages.win-rewards");
        List<String> lose_rewards = messages.getStringList("Messages.lose-rewards");

        double money_for_win;
        double money_for_kills;
        double level_xp_for_win;
        double level_xp_for_kills;
        double money_for_lose;
        double level_xp_for_lose;

        for (UHCPlayer p : plugin.getPlayerManager().getPlayersList()) {

            money_for_kills = config.getDouble("coins-per-kill") * p.getKills();
            level_xp_for_kills = config.getDouble("player-level.level-xp-per-kill")*p.getKills();
            money_for_win = config.getDouble("coins-per-win");
            level_xp_for_win = config.getDouble("player-level.level-xp-per-win");

            money_for_lose = config.getDouble("coins-per-lose");
            level_xp_for_lose = config.getDouble("player-level.level-xp-per-lose");

            if (p.isDead()) {
                p.getPlayer().showPlayer(plugin, p.getPlayer());
            }

            if (p == winner) {
                plugin.getStatistics().addWin(p);
                plugin.getStatistics().addMoney(p, money_for_win + money_for_kills);
                plugin.getLevelManager().addPlayerLevel(p, level_xp_for_win + level_xp_for_kills);
                titleAction.execute(plugin, p.getPlayer(), "Victory!");
                Utils.broadcast(Messages.WINNER.toString().replace("%winner%", p != null ? p.getName() : "NONE"));
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
                plugin.getStatistics().addMoney(p, money_for_lose+money_for_kills);
                plugin.getLevelManager().addPlayerLevel(p, level_xp_for_lose+level_xp_for_kills);
                titleAction.execute(plugin, p.getPlayer(), "Game Over!");
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
            }
        }
    }

    @EventHandler
    public void handleGameKill(GameKillEvent event) {
        UHCPlayer killer = event.getKiller();
        UHCPlayer victim = event.getVictim();

        if (killer instanceof Player) {
            plugin.getStatistics().addKill(killer);
            plugin.getPerksManager().givePerk(killer);

            killer.getPlayer().giveExp(victim.getPlayer().getTotalExperience()/3);

            Utils.broadcast(Messages.KILL.toString().replace("%player%", victim.getName()).replace("%killer%", killer.getName()));
        } else {
            Utils.broadcast(Messages.DEATH.toString().replace("%player%", victim.getName()));
        }

        plugin.getPlayerManager().clearPlayerInventory(victim.getPlayer());

        victim.getPlayer().setHealth(20);
        victim.getPlayer().setFoodLevel(20);
        victim.getPlayer().setLevel(0);
        victim.getPlayer().giveExp(-victim.getPlayer().getTotalExperience());
        victim.getPlayer().setTotalExperience(0);

        plugin.getStatistics().addDeath(victim);
        plugin.getPlayerManager().setSpectator(victim);

        plugin.getGame().checkGame();
    }

    @EventHandler
    public void handleBlockDestroy(BlockBreakEvent event) {
        UHCPlayer p = plugin.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());
//        Block block = event.getBlock();

        if (!plugin.getGame().isPlaying() || p.isDead()) {
            event.setCancelled(true);
            p.sendMessage(Messages.CANT_BREAK.toString());
            return;
        }
        Utils.dropItem(p, event);
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent event) {
        UHCPlayer p = plugin.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());
        if (!plugin.getGame().isPlaying() || p.isDead()) {
            p.sendMessage(Messages.CANT_PLACE.toString());
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void handleEntityDrop(EntityDeathEvent event) {
        Random ran = new Random();
        int amount = 1;

        if (event.getEntity() instanceof Cow) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), amount));
        }
        if (event.getEntity() instanceof Pig) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), amount));
        }
        if (event.getEntity() instanceof Sheep) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(XMaterial.COOKED_MUTTON.parseMaterial(), amount));
            event.getDrops().add(new ItemStack(XMaterial.STRING.parseMaterial(), amount));
        }
        if (event.getEntity() instanceof Chicken) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(XMaterial.COOKED_CHICKEN.parseMaterial(), amount));
            event.getDrops().add(new ItemStack(XMaterial.STRING.parseMaterial(), amount));
        }
        if (event.getEntity() instanceof Rabbit) {
            event.getDrops().clear();
            amount = ran.nextInt(3)+1;
            event.getDrops().add(new ItemStack(XMaterial.COOKED_RABBIT.parseMaterial(), amount));
        }
    }

    @EventHandler
    public void handleWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handleTimber(BlockBreakEvent event) {
        plugin.getUtilities().timber(event.getBlock());
    }

    @EventHandler
    public void handleDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!plugin.getGame().isPlaying()) {
            event.setCancelled(true);
            return;
        }

        DamageCause cause = event.getCause();
        if (plugin.getGame().getGameState() == GameState.MINING) {
            if (cause.equals(DamageCause.FIRE) || cause.equals(DamageCause.LAVA) || cause.equals(DamageCause.DROWNING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleEntityHitEntity(EntityDamageByEntityEvent event) {

        Player damager = (Player) event.getDamager();
        UHCPlayer uhcPlayerD = plugin.getPlayerManager().getUHCPlayer(damager.getUniqueId());

        if (!plugin.getGame().isPlaying() || uhcPlayerD.isDead()) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getGame().getGameState().equals(GameState.MINING) || plugin.getGame().isEnding()) {
            if (!(event.getEntity() instanceof Player)) return;
            event.setCancelled(true);
            return;
        }

        Player entity = (Player) event.getEntity();
        UHCPlayer uhcPlayerE = plugin.getPlayerManager().getUHCPlayer(entity.getUniqueId());

        if (uhcPlayerD.getTeam() == uhcPlayerE.getTeam()) {
            event.setCancelled(true);
            uhcPlayerD.sendMessage("Baka, this is your teammate...");
        }
    }

    @EventHandler
    public void handleHunger(FoodLevelChangeEvent event) {
        Player p = (Player) event.getEntity();
        UHCPlayer player = plugin.getPlayerManager().getUHCPlayer(p.getUniqueId());
        if (!plugin.getGame().isPlaying() || player.isDead()) {
            p.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!plugin.getGame().isPlaying()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleBucketFill(PlayerBucketFillEvent event) {
        if (!plugin.getGame().isPlaying()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleHealthRegain(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleArrowHitHP(ProjectileHitEvent event) {

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
    public void handleMonsterTargeting(EntityTargetEvent event) {
        if (event.getEntity() instanceof Monster) {
            if (event.getTarget() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleMonsterSpawning(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Monster) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                event.setCancelled(true);
            }
        }
    }
}
