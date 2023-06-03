package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.customDrop.CustomDrop;
import me.florixak.uhcrun.listener.events.GameEndEvent;
import me.florixak.uhcrun.listener.events.GameKillEvent;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class GameListener implements Listener {

    private final GameManager gameManager;
    private final FileConfiguration config;

    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    @EventHandler
    public void handleGameEnd(GameEndEvent event) {

        String winner = event.getWinner();
        List<String> gameResultsMsg = Messages.GAME_RESULTS.toList();
        List<UHCPlayer> top_killers = gameManager.getPlayerManager().getTopKillers();
        List<String> commands = config.getStringList("settings.end-game-commands");

        // Game results and top killers
        if (gameResultsMsg != null && !gameResultsMsg.isEmpty()) {
            for (String message : gameResultsMsg) {
                for (int i = 0; i < gameResultsMsg.size(); i++) {
                    UHCPlayer topKiller = i < top_killers.size() && top_killers.get(i) != null ? top_killers.get(i) : null;
                    boolean isUHCPlayer = topKiller != null;
                    message = message.replace("%winner%", winner)
                            .replace("%top-killer-" + (i+1) + "%", isUHCPlayer ? topKiller.getName() : "None")
                            .replace("%top-killer-" + (i+1) + "-kills%", isUHCPlayer ? String.valueOf(topKiller.getKills()) : "0")
                            .replace("%top-killer-" + (i+1) + "-team%", isUHCPlayer && gameManager.isTeamMode() ? topKiller.getTeam().getDisplayName() : "")
                            .replace("%top-killer-" + (i+1) + "-uhc-level%", isUHCPlayer ? String.valueOf(topKiller.getData().getUHCLevel()) : "0");
                }
                message = message.replace("%prefix%", Messages.PREFIX.toString());

                Utils.broadcast(TextUtils.color(message));
            }
        }

        // End game commands
        if (commands != null && !commands.isEmpty()) {
            for (String command : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        // Statistics
        for (UHCPlayer uhcPlayer : gameManager.getPlayerManager().getPlayers()) {

            if (gameManager.areStatisticsAddedOnEnd()) {
                uhcPlayer.getData().addStatistics();
            } else {
                uhcPlayer.getData().addGameResult();
            }
            if (uhcPlayer.isOnline()) {
                uhcPlayer.getData().showStatistics();
            }
        }
    }

    @EventHandler
    public void handleGameKill(GameKillEvent event) {
        UHCPlayer killer = event.getKiller();
        UHCPlayer victim = event.getVictim();

        if (killer != null) {
            killer.addKill();

            gameManager.getPerksManager().givePerk(killer);
            killer.getPlayer().giveExp(config.getInt("settings.rewards.kill.exp"));

            Utils.broadcast(Messages.KILL.toString().replace("%player%", victim.getName()).replace("%killer%", killer.getName()));
        } else {
            Utils.broadcast(Messages.DEATH.toString().replace("%player%", victim.getName()));
        }

        if (!gameManager.areStatisticsAddedOnEnd()) {
            if (killer != null) {
                killer.getData().addKills(1);
                killer.getData().addUHCExp(config.getDouble("settings.rewards.kill.uhc-exp"));
            }
            victim.getData().addDeaths(1);

            if (!victim.getTeam().isAlive() && gameManager.isTeamMode()) {
                victim.getData().addLose(1);
            }
        }

        if (!victim.getTeam().isAlive() && gameManager.isTeamMode()) {
            Utils.broadcast(Messages.TEAM_DEFEATED.toString().replace("%team%", victim.getTeam().getDisplayName()));
        }

        gameManager.getPlayerManager().clearPlayerInventory(victim.getPlayer());
        gameManager.getPlayerManager().setSpectator(victim);
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        Block block = event.getBlock();

        if (!gameManager.isPlaying() || uhcPlayer.isDead()) {
            event.setCancelled(true);
            p.sendMessage(Messages.CANT_BREAK.toString());
            return;
        }

        if (gameManager.isRandomDrop()) {
            event.setDropItems(false);
            event.setExpToDrop(0);

            int pick = new Random().nextInt(XMaterial.values().length);

            ItemStack drop_is = new ItemStack(XMaterial.values()[pick].parseMaterial());
            Location loc = block.getLocation();

            Location location = loc.add(0.5, 0.5, 0.5);
            Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, drop_is);
            return;
        }

        if (gameManager.getCustomDropManager().hasCustomDrop(block.getType())) {
            CustomDrop customDrop = gameManager.getCustomDropManager().getCustomDrop(block.getType());
            customDrop.dropItem(event, null);
        }
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent event) {
        UHCPlayer uhcPlayer = gameManager.getPlayerManager().getUHCPlayer(event.getPlayer().getUniqueId());
        if (!gameManager.isPlaying() || uhcPlayer.isDead()) {
            uhcPlayer.sendMessage(Messages.CANT_PLACE.toString());
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void handleEntityDrop(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Animals)) return;

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
    public void handleDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!gameManager.isPlaying() || gameManager.getGameState().equals(GameState.ENDING)) {
            event.setCancelled(true);
            return;
        }

        DamageCause cause = event.getCause();

        if (gameManager.getGameState() == GameState.MINING) {
            List<String> disabled_causes = config.getStringList("settings.game.disabled-in-mining");
            if (!disabled_causes.isEmpty() && disabled_causes != null) {
                for (String cause_name : disabled_causes) {
                    if (cause.name().equalsIgnoreCase(cause_name)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void handleEntityHitEntity(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player)) {
            if (!gameManager.isPlaying()) {
                event.setCancelled(true);
            }
            return;
        }

        Player damager = (Player) event.getDamager();
        UHCPlayer uhcPlayerD = gameManager.getPlayerManager().getUHCPlayer(damager.getUniqueId());

        if (!gameManager.isPlaying() || uhcPlayerD.isDead()) {
            event.setCancelled(true);
            return;
        }

        if (gameManager.getGameState().equals(GameState.MINING)
                || gameManager.getGameState().equals(GameState.ENDING)
                || !gameManager.isPvP()) {
            if (!(event.getEntity() instanceof Player)) return;
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Player) {
            Player entity = (Player) event.getEntity();
            UHCPlayer uhcPlayerE = gameManager.getPlayerManager().getUHCPlayer(entity.getUniqueId());

            if (uhcPlayerD.getTeam() == uhcPlayerE.getTeam() && config.getBoolean("settings.teams.friendly-fire")) {
                event.setCancelled(true);
                uhcPlayerD.sendMessage("Baka, this is your teammate...");
            }
        }
    }

    @EventHandler
    public void handleWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handleTimber(BlockBreakEvent event) {
        gameManager.getUtils().timber(event.getBlock());
    }

    @EventHandler
    public void handleHunger(FoodLevelChangeEvent event) {
        Player p = (Player) event.getEntity();
        UHCPlayer player = gameManager.getPlayerManager().getUHCPlayer(p.getUniqueId());
        if (!gameManager.isPlaying() || player.isDead()) {
            p.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!gameManager.isPlaying()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleBucketFill(PlayerBucketFillEvent event) {
        if (!gameManager.isPlaying()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleHealthRegain(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        RegainReason reason = event.getRegainReason();

        if (reason.equals(RegainReason.SATIATED)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleArrowHitHP(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        if (!(event.getEntity() instanceof Arrow) && !(event.getEntity() instanceof Snowball)) return;
        if (config.getBoolean("settings.game.projectile-hit-hp", false)) return;

        Player shooter = (Player) event.getEntity().getShooter();
        Player enemy = (Player) event.getHitEntity();

        shooter.sendMessage(Messages.SHOT_HP.toString()
                .replace("%player%", enemy.getDisplayName())
                .replace("%hp%", String.valueOf(enemy.getHealth())));
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

    @EventHandler
    public void handleExplode(EntityExplodeEvent event) {

        if (!gameManager.areExplosionsEnabled()) {
            event.setCancelled(true);
            return;
        }
    }
}