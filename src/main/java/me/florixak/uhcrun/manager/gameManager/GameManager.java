package me.florixak.uhcrun.manager.gameManager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.events.GameEndEvent;
import me.florixak.uhcrun.player.PlayerManager;
import me.florixak.uhcrun.manager.SoundManager;
import me.florixak.uhcrun.player.PlayerState;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.task.*;
import me.florixak.uhcrun.utils.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class GameManager {

    private final UHCRun plugin;
    private GameState gameState = GameState.WAITING;

    // TODO managery sem

    private FileConfiguration config, messages;

    public GameManager(UHCRun plugin){
        this.plugin = plugin;

        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.messages = plugin.getConfigManager().getFile(ConfigType.MESSAGES).getConfig();

        plugin.getRecipeManager().registerRecipes();

        plugin.getScoreboardManager().updateScoreboard();
        plugin.getBorderManager().checkBorder();

        setOreSpawn();

        runActivityRewards();
        runAutoBroadcast();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState){
        if (this.gameState == gameState) return;
        this.gameState = gameState;

        switch (gameState) {

            case WAITING:
                plugin.getTasks().stopStartingCD();
                break;

            case STARTING:
                plugin.getTasks().startStartingCD();
                break;

            case MINING:
                plugin.getPlayerManager().getPlayersList().forEach(plugin.getPlayerManager()::setPlayersForGame);
                plugin.getPlayerManager().getPlayersList().forEach(plugin.getKitsManager()::giveKit);
                plugin.getTeams().addToTeam();
                plugin.getPlayerManager().teleportPlayers();
                plugin.getTasks().startMiningCD();
                Utils.broadcast(Messages.MINING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(MiningCd.count)));
                break;

            case FIGHTING:
                plugin.getPlayerManager().getPlayersList().forEach(player -> plugin.getPlayerManager().teleportPlayersAfterMining(player));
                plugin.getTasks().startFightingCD();
                Utils.broadcast(Messages.PVP.toString());
                Utils.broadcast(Messages.BORDER_SHRINK.toString());
                break;

            case DEATHMATCH:
                plugin.getTasks().startDeathmatchCD();
                Utils.broadcast(Messages.DEATHMATCH_STARTED.toString());
                Bukkit.getOnlinePlayers().forEach(player -> plugin.getSoundManager().playDMBegan(player));
                break;

            case ENDING:
                plugin.getTasks().startEndingCD();
                Utils.broadcast(Messages.GAME_ENDED.toString());
                plugin.getServer().getPluginManager().callEvent(new GameEndEvent(plugin.getPlayerManager().getWinner()));
                break;
        }
    }

    public void checkGame() {

        if (isWaiting()) {
            int min = config.getInt("min-players-to-start");
            if (plugin.getPlayerManager().getAliveList().size() >= min) {
                Bukkit.getOnlinePlayers().forEach(player -> plugin.getSoundManager().playStartingSound(player));
                setGameState(GameState.STARTING);
                Utils.broadcast(Messages.GAME_STARTING.toString()
                        .replace("%countdown%", "" + TimeUtils.getFormattedTime(StartingCd.count)));
            }
            return;
        }
//        if (isStarting()) {
//            int min = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getInt("min-players-to-start");
//            if (PlayerManager.online.size() < min) {
//                setGameState(GameState.WAITING);
//                Bukkit.broadcastMessage(Messages.GAME_STARTING_CANCELED.toString());
//            }
//            return;
//        }
//        if (isPlaying()) {
//            if (PlayerManager.alive.size() < 2) {
//                Bukkit.getScheduler().getPendingTasks().clear();
//                Bukkit.getScheduler().getActiveWorkers().clear();
//                setGameState(GameState.ENDING);
//            }
//        }
    }

    public void onDisable() {

        for (Player all : Bukkit.getOnlinePlayers()) {
            all.giveExp(-all.getTotalExperience());
            all.kickPlayer(Messages.RESTARTING.toString());
        }

        plugin.getPlayerManager().clearAlive();
        plugin.getPlayerManager().clearDead();
        plugin.getPlayerManager().clearPlayers();
        plugin.getTeams().onDisable();

        if (config.getBoolean("auto-map-reset", true)) {
            File world = Bukkit.getWorldContainer();
            world.delete();
        }
    }

    public boolean isWaiting() {
        return gameState == GameState.WAITING;
    }
    public boolean isStarting() {
        return gameState == GameState.STARTING;
    }
    public boolean isPlaying() {
        return (gameState == GameState.MINING) || (gameState == GameState.FIGHTING) || (gameState == GameState.DEATHMATCH);
    }
    public boolean isEnding() {
        return gameState == GameState.ENDING;
    }

    public void setOreSpawn() {
        OreUtils oreUtility = new OreUtils();
        FileConfiguration config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        World world = Bukkit.getWorld(config.getString("game-world"));
        Random random = new Random();
        int border = config.getInt("border.size");
        Location loc;
        for (int i = 0; i < 500; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.DIAMOND_ORE);
            oreUtility.generateVein(Material.DIAMOND_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 600; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.IRON_ORE);
            oreUtility.generateVein(Material.IRON_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 600; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.COAL_ORE);
            oreUtility.generateVein(Material.COAL_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 500; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.EMERALD_ORE);
            oreUtility.generateVein(Material.EMERALD_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 500; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.GOLD_ORE);
            oreUtility.generateVein(Material.GOLD_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 500; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.REDSTONE_ORE);
            oreUtility.generateVein(Material.REDSTONE_ORE, world.getBlockAt(loc), amount);
        }
        for (int i = 0; i < 200; i++) {
            int amount = random.nextInt(3) + 2;
            loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            while (world.getBlockAt(loc).getType().equals(Material.WATER)) {
                loc = new Location(world, random.nextInt(border), random.nextInt(55), random.nextInt(border));
            }
            world.getBlockAt(loc).setType(Material.OBSIDIAN);
            oreUtility.generateVein(Material.OBSIDIAN, world.getBlockAt(loc), amount);
        }
    }


    public void runActivityRewards() {
        if (config.getBoolean("rewards-per-time.enabled", true)) {
            int interval = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getInt("rewards-per-time.interval")*20;
            new ActivityRewards(plugin).runTaskTimer(plugin, 0, interval);
        }
    }
    public void runAutoBroadcast() {
        if (config.getBoolean("auto-broadcast.enabled", true)) {
            int interval = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getInt("auto-broadcast.interval")*20;
            new AutoBroadcastMessages(plugin).runTaskTimer(plugin, 0, interval);
        }
    }


}