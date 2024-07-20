package me.florixak.uhcrun.listener;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameState;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.customDrop.CustomDrop;
import me.florixak.uhcrun.game.player.PlayerManager;
import me.florixak.uhcrun.game.player.UHCPlayer;
import me.florixak.uhcrun.listener.events.GameEndEvent;
import me.florixak.uhcrun.listener.events.GameKillEvent;
import me.florixak.uhcrun.manager.lobby.LobbyType;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
    private final PlayerManager playerManager;

    private final boolean addUpStatsOnEnd;

    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.playerManager = gameManager.getPlayerManager();

        this.addUpStatsOnEnd = GameValues.STATISTICS.ADD_ON_END;
    }

    @EventHandler
    public void handleGameEnd(GameEndEvent event) {

        String winner = event.getWinner();
        List<String> gameResults = Messages.GAME_RESULTS.toList();
        List<UHCPlayer> topKillers = playerManager.getTopKillers();
        List<String> commands = config.getStringList("settings.end-game-commands");

        // Game results and top killers
        if (!gameResults.isEmpty()) {
            for (String message : gameResults) {
                for (int i = 0; i < gameResults.size(); i++) {
                    UHCPlayer topKiller = i < topKillers.size() && topKillers.get(i) != null ? topKillers.get(i) : null;
                    boolean isUHCPlayer = topKiller != null;
                    message = message.replace("%winner%", winner)
                            .replace("%top-killer-" + (i + 1) + "%", isUHCPlayer ? topKiller.getName() : "None")
                            .replace("%top-killer-" + (i + 1) + "-kills%", isUHCPlayer ? String.valueOf(topKiller.getKills()) : "0")
                            .replace("%top-killer-" + (i + 1) + "-team%", isUHCPlayer && GameValues.TEAM.TEAM_MODE ? topKiller.getTeam().getDisplayName() : "")
                            .replace("%top-killer-" + (i + 1) + "-uhc-level%", isUHCPlayer ? String.valueOf(topKiller.getData().getUHCLevel()) : "0");
                }
                message = message.replace("%prefix%", Messages.PREFIX.toString());

                Utils.broadcast(TextUtils.color(message));
            }
        }

        // Statistics
        for (UHCPlayer player : playerManager.getOnlineList()) {

            if (!addUpStatsOnEnd) player.getData().addWinOrLose();
            else player.getData().addStatistics();

            player.clearInventory();
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(gameManager.getLobbyManager().getLocation(LobbyType.ENDING));

            player.getData().showStatistics();
        }

        // End game commands
        if (!commands.isEmpty()) {
            for (String command : commands) {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }
        }
    }

    @EventHandler
    public void handleGameKill(GameKillEvent event) {
        UHCPlayer killer = event.getKiller();
        UHCPlayer victim = event.getVictim();

        victim.die();

        if (killer != null) {
            killer.addKill();
            killer.getPlayer().giveExp((int) GameValues.REWARDS.EXP_FOR_KILL);
            gameManager.getPerksManager().givePerk(killer);

            killer.sendMessage(Messages.REWARDS_KILL.toString()
                    .replace("%player%", victim.getName())
                    .replace("%money%", String.valueOf(GameValues.REWARDS.MONEY_FOR_ASSIST))
                    .replace("%uhc-exp%", String.valueOf(GameValues.REWARDS.UHC_EXP_FOR_ASSIST)));

            Utils.broadcast(Messages.KILL.toString()
                    .replace("%player%", victim.getName())
                    .replace("%killer%", killer.getName()));
        } else {
            Utils.broadcast(Messages.DEATH.toString()
                    .replace("%player%", victim.getName()));
        }

        if (victim.wasDamagedByMorePeople()) {
            UHCPlayer assistPlayer = victim.getKillAssistPlayer();

            if (assistPlayer.getUUID() == killer.getUUID()) {
                UHCRun.getInstance().getLogger().info("Chyba kill assistu!");
                return;
            }
            assistPlayer.addAssist();
            if (!addUpStatsOnEnd) {
                assistPlayer.getData().addAssists(1);
            }
            assistPlayer.giveExp((int) GameValues.REWARDS.EXP_FOR_ASSIST);
            assistPlayer.sendMessage(Messages.REWARDS_ASSIST.toString()
                    .replace("%player%", victim.getName())
                    .replace("%money%", String.valueOf(GameValues.REWARDS.MONEY_FOR_ASSIST))
                    .replace("%uhc-exp%", String.valueOf(GameValues.REWARDS.UHC_EXP_FOR_ASSIST)));
        }

        if (!addUpStatsOnEnd) {
            if (killer != null) {
                killer.getData().addKills(1);
            }
            victim.getData().addDeaths(1);
        }

        if (GameValues.TEAM.TEAM_MODE && !victim.getTeam().isAlive()) {
            Utils.broadcast(Messages.TEAM_DEFEATED.toString().replace("%team%", victim.getTeam().getDisplayName()));
        }
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        UHCPlayer uhcPlayer = playerManager.getUHCPlayer(p.getUniqueId());
        Block block = event.getBlock();

        if (!gameManager.isPlaying() || uhcPlayer.isDead() || gameManager.getGameState().equals(GameState.ENDING)) {
            event.setCancelled(true);
            uhcPlayer.sendMessage(Messages.CANT_BREAK.toString());
            return;
        }

        gameManager.timber(block);

        if (GameValues.GAME.RANDOM_DROPS_ENABLED) {
            event.setDropItems(false);
            event.setExpToDrop(0);

            int pick = new Random().nextInt(XMaterial.values().length);

            ItemStack drop_is = new ItemStack(XMaterial.values()[pick].parseMaterial());
            Location loc = block.getLocation();
            Location location = loc.add(0.5, 0.5, 0.5);

            Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, drop_is);
            return;
        }

        if (gameManager.getCustomDropManager().hasBlockCustomDrop(block.getType())) {
            CustomDrop customDrop = gameManager.getCustomDropManager().getCustomBlockDrop(block.getType());
            customDrop.dropBlockItem(event);
        }
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent event) {
        UHCPlayer uhcPlayer = playerManager.getUHCPlayer(event.getPlayer().getUniqueId());
        if (!gameManager.isPlaying() || uhcPlayer.isDead() || gameManager.getGameState().equals(GameState.ENDING)) {
            uhcPlayer.sendMessage(Messages.CANT_PLACE.toString());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleEntityDrop(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Animals)) return;

        if (gameManager.getCustomDropManager().hasMobCustomDrop(event.getEntityType())) {
            CustomDrop customDrop = gameManager.getCustomDropManager().getCustomMobDrop(event.getEntityType());
            customDrop.dropMobItem(event);
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
            if (!disabled_causes.isEmpty()) {
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
        UHCPlayer uhcPlayerD = playerManager.getUHCPlayer(damager.getUniqueId());

        if (!gameManager.isPlaying() || uhcPlayerD.isDead() || gameManager.getGameState().equals(GameState.ENDING)) {
            event.setCancelled(true);
            return;
        }

        if (gameManager.getGameState().equals(GameState.MINING) || !gameManager.isPvP()) {
            if (!(event.getEntity() instanceof Player)) return;
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Player) {
            Player entity = (Player) event.getEntity();
            UHCPlayer uhcPlayerE = playerManager.getUHCPlayer(entity.getUniqueId());

            uhcPlayerE.addKillAssistPlayer(uhcPlayerD);

            if (uhcPlayerD.getTeam() == uhcPlayerE.getTeam() && !GameValues.TEAM.FRIENDLY_FIRE) {
                event.setCancelled(true);
                uhcPlayerD.sendMessage("Baka, this is your teammate..");
            }
        }
    }

    @EventHandler
    public void handleWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handleBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!gameManager.isPlaying() || gameManager.getGameState().equals(GameState.ENDING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleBucketFill(PlayerBucketFillEvent event) {
        if (!gameManager.isPlaying() || gameManager.getGameState().equals(GameState.ENDING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleHunger(FoodLevelChangeEvent event) {
        Player p = (Player) event.getEntity();
        UHCPlayer player = playerManager.getUHCPlayer(p.getUniqueId());
        if (!gameManager.isPlaying() || player.isDead() || gameManager.getGameState().equals(GameState.ENDING)) {
            p.setFoodLevel(20);
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
        if (!GameValues.GAME.PROJECTILE_HIT_HP_ENABLED) return;
        if (!gameManager.isPlaying() || gameManager.getGameState().equals(GameState.ENDING)) return;

        Player shooter = (Player) event.getEntity().getShooter();
        Player enemy = (Player) event.getHitEntity();

        shooter.sendMessage(Messages.SHOT_HP.toString().replace("%player%", enemy.getDisplayName()).replace("%hp%", String.valueOf(enemy.getHealth())));
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
        if (GameValues.GAME.EXPLOSIONS_DISABLED) event.setCancelled(true);
    }
}