package me.florixak.uhcrevamp.listener;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customDrop.CustomDrop;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.game.player.PlayerManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.listener.events.GameEndEvent;
import me.florixak.uhcrevamp.listener.events.GameKillEvent;
import me.florixak.uhcrevamp.utils.RandomUtils;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class GameListener implements Listener {

    private final GameManager gameManager;
    private final FileConfiguration config;
    private final PlayerManager playerManager;

    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.playerManager = gameManager.getPlayerManager();
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

            player.getData().addWinOrLose();

            player.clearInventory();
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(gameManager.getLobbyManager().getLocation("ending"));

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
            killer.getPerk().givePerk(killer);

            killer.sendMessage(Messages.REWARDS_KILL.toString()
                    .replace("%player%", victim.getName())
                    .replace("%money%", String.valueOf(GameValues.REWARDS.COINS_FOR_ASSIST))
                    .replace("%uhc-exp%", String.valueOf(GameValues.REWARDS.UHC_EXP_FOR_ASSIST)));

            Utils.broadcast(Messages.KILL.toString()
                    .replace("%player%", victim.getName())
                    .replace("%killer%", killer.getName()));

            long deathTime = System.currentTimeMillis();
            long assistWindow = 60000; // 60 seconds before death
            List<UUID> assistants = victim.getAssistants(deathTime, assistWindow, killer.getUUID());

            for (UUID assistantId : assistants) {
                UHCPlayer assistant = playerManager.getUHCPlayer(assistantId);
                if (assistant != null) {
                    assistant.addAssist();
                }
            }

            victim.clearDamageTrackers();
        } else {
            Utils.broadcast(Messages.DEATH.toString()
                    .replace("%player%", victim.getName()));
        }


        if (killer != null) {
            killer.getData().addKills(1);
        }
        victim.getData().addDeaths(1);


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
            block.getDrops().clear();
            event.setExpToDrop(0);

            int randomMaterialIndex = RandomUtils.getRandom().nextInt(XMaterial.values().length);
            Material material = XMaterial.values()[randomMaterialIndex].parseMaterial();
            if (material == null) return;

            ItemStack dropIs = new ItemStack(material);
            Location loc = block.getLocation();
            Location location = loc.add(0.5, 0.5, 0.5);

            Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, dropIs);
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
            if (gameManager.getCurrentCountdown() >= (GameValues.GAME.MINING_COUNTDOWN - 60)) {
                if (cause.equals(DamageCause.FALL)) {
                    event.setCancelled(true);
                }
            }
            List<String> disabledCauses = GameValues.GAME.DISABLED_IN_MINING;
            if (!disabledCauses.isEmpty()) {
                for (String causeName : disabledCauses) {
                    if (cause.name().equalsIgnoreCase(causeName)) {
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

        if (!gameManager.isPlaying() || uhcPlayerD.isDead() || gameManager.isEnding()) {
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

            if (uhcPlayerD.getTeam() == uhcPlayerE.getTeam() && !GameValues.TEAM.FRIENDLY_FIRE) {
                event.setCancelled(true);
                uhcPlayerD.sendMessage(Messages.TEAM_NO_FRIENDLY_FIRE.toString());
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
        if (!gameManager.isPlaying() || player.isDead() || gameManager.isEnding()) {
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
    public void handleProjectileHit(EntityDamageByEntityEvent event) {
        if (!GameValues.GAME.PROJECTILE_HIT_HP_ENABLED) return;
        if (!gameManager.isPlaying()) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (event.getDamager() instanceof Snowball) {
            Snowball snowball = (Snowball) event.getDamager();
            if (!(snowball.getShooter() instanceof Player)) return;

            Player shooter = (Player) snowball.getShooter();
            Player enemy = (Player) event.getEntity();
            handleProjectileHit(shooter, enemy);

        } else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (!(arrow.getShooter() instanceof Player)) return;

            Player shooter = (Player) arrow.getShooter();
            Player enemy = (Player) event.getEntity();
            handleProjectileHit(shooter, enemy);
        }
    }

    private void handleProjectileHit(Player shooter, Player enemy) {
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

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack[] matrix1D = event.getInventory().getMatrix();
        ItemStack[][] matrix2D = new ItemStack[3][3]; // Assuming a 3x3 crafting table

        // Convert 1D array to 2D array safely
        for (int i = 0; i < matrix1D.length && i < 9; i++) { // Ensure not to exceed bounds of either array
            matrix2D[i / 3][i % 3] = matrix1D[i];
        }
        // Iterate over all custom crafts to find a match
        for (CustomRecipe customRecipe : gameManager.getRecipeManager().getRecipeList()) {
            if (customRecipe.matches(matrix2D)) {
                event.setResult(Event.Result.DENY);
                event.setCurrentItem(customRecipe.getResult());
                Bukkit.getLogger().info("Crafted custom item: " + customRecipe.getResult().getType());
                return; // Stop checking after finding the first matching recipe
            }
        }
    }
}