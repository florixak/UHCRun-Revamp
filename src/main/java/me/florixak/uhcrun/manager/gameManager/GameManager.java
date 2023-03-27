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
    public GameState gameState = GameState.WAITING;

    private FileConfiguration config, messages;

    public GameManager(UHCRun plugin){
        this.plugin = plugin;

        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.messages = plugin.getConfigManager().getFile(ConfigType.MESSAGES).getConfig();
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
                plugin.getPlayerManager().getPlayers().stream().filter(player -> player.isOnline()).forEach(this::setPlayersForGame);
                // plugin.getPlayerManager().getPlayers().stream().filter(player -> player.isOnline()).forEach(plugin.getKitsManager()::giveKits);
                plugin.getTeams().addToTeam();
                teleportPlayers();
                plugin.getTasks().startMiningCD();
                // SoundManager.playGameStarted(null, true);
                Utils.broadcast(Messages.GAME_STARTED.toString());
                Utils.broadcast(Messages.MINING.toString().replace("%countdown%", "" + TimeUtils.getFormattedTime(MiningCd.count)));
                break;

            case FIGHTING:
                Bukkit.getOnlinePlayers().forEach(player -> teleportPlayersAfterMining(player));
                plugin.getTasks().startFightingCD();
                Utils.broadcast(Messages.PVP.toString());
                Utils.broadcast(Messages.BORDER_SHRINK.toString());
                break;

            case DEATHMATCH:
                plugin.getTasks().startDeathmatchCD();
                Utils.broadcast(Messages.DEATHMATCH_STARTED.toString());
                Bukkit.getOnlinePlayers().forEach(player -> SoundManager.playDMBegan(player));
                break;

            case ENDING:
                plugin.getTasks().startEndingCD();
                Utils.broadcast(Messages.GAME_ENDED.toString());
                plugin.getServer().getPluginManager().callEvent(new GameEndEvent(getWinner()));
                break;
        }
    }

    public void checkGame() {

        if (isWaiting()) {
            int min = config.getInt("min-players-to-start");
            if (plugin.getPlayerManager().getAlive().size() >= min) {
                Bukkit.getOnlinePlayers().forEach(player -> SoundManager.playStartingSound(player));
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
//            if (PlayerManager.alive.size() < 2) { // vrátit 2
//                Bukkit.getScheduler().getPendingTasks().clear();
//                Bukkit.getScheduler().getActiveWorkers().clear();
//                setGameState(GameState.ENDING);
//            }
//        }


    }
    public void resetGame() {

        for (Player all : Bukkit.getOnlinePlayers()) {
            all.giveExp(-all.getTotalExperience());
            all.kickPlayer(Messages.RESTARTING.toString());
        }

        plugin.getPlayerManager().clearAlive();
        plugin.getPlayerManager().clearDead();
        plugin.getPlayerManager().clearPlayers();

        if (config.getBoolean("auto-map-reset", true)) {
            File propertiesFile = new File(Bukkit.getWorldContainer(), "server.properties");
            try (FileInputStream stream = new FileInputStream(propertiesFile)) {
                Properties properties = new Properties();
                properties.load(stream);

                // Getting and deleting the main world
                File world = new File(Bukkit.getWorldContainer(), properties.getProperty("level-name"));

                File nether = new File(Bukkit.getWorldContainer(), "world_nether");
                // Creating needed directories
                world.mkdirs();
                new File(world, "data").mkdirs();
                new File(world, "datapacks").mkdirs();
                new File(world, "playerdata").mkdirs();
                new File(world, "poi").mkdirs();
                new File(world, "region").mkdirs();

                new File(nether, "data").mkdirs();
                new File(nether, "datapacks").mkdirs();
                new File(nether, "playerdata").mkdirs();
                new File(nether, "poi").mkdirs();
                new File(nether, "region").mkdirs();
            } catch (IOException ignored) {
            }
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
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

    public void setPlayersForGame(UHCPlayer uhcPlayer) {

        /*if (PlayerManager.isCreator(player.getPlayer())) {
            PlayerManager.creator.remove(player.getUUID());
        }*/

        uhcPlayer.setState(PlayerState.PLAYING);
        plugin.getPlayerManager().addAlive(uhcPlayer);

        uhcPlayer.getPlayer().getInventory().clear();
        uhcPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
        uhcPlayer.getPlayer().setHealth(uhcPlayer.getPlayer().getHealthScale());
        uhcPlayer.getPlayer().setFoodLevel(20);

        plugin.getKitsManager().giveKit(uhcPlayer);

        // wereAlive = PlayerManager.alive.size();
    }

    public void teleportPlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(TeleportUtils.teleportToSafeLocation()));
    }
    public void teleportPlayersAfterMining(Player p) {

        double x, y, z;

        World world = Bukkit.getWorld(p.getLocation().getWorld().getName());
        x = p.getLocation().getX();
        y = 150;
        z = p.getLocation().getZ();


        Location location = new Location(world, x, y, z);
        y = location.getWorld().getHighestBlockYAt(location);
        location.setY(y);
        p.teleport(location);
    }

    public void setSpectator(UHCPlayer p) {

        p.setState(PlayerState.DEAD);

        p.getPlayer().setAllowFlight(true);
        p.getPlayer().setFlying(true);

        p.getPlayer().setGameMode(GameMode.SPECTATOR);

        // plugin.getKitsManager().getSpectatorKit(p.getPlayer());
        // new VanishUtils().toggleVanish(p.getPlayer());

        p.getPlayer().teleport(new Location(
                Bukkit.getWorld(config.getString("game-world")),
                p.getPlayer().getLocation().getX()+0,
                p.getPlayer().getLocation().getY()+10,
                p.getPlayer().getLocation().getZ()+0));

    }

    public void addKillTo(UHCPlayer p) {
        plugin.getStatistics().addKill(p); // TODO přidat na konec hry
        p.addKill();
    }
    public void addDeathTo(UHCPlayer p) {
        plugin.getStatistics().addDeath(p);
        plugin.getPlayerManager().removeAlive(p);
        plugin.getPlayerManager().addDead(p);
        p.setTeam(null);
    }

    public UHCPlayer getWinner() {
        for (UHCPlayer p : plugin.getPlayerManager().getAlive()) {
            if (!p.isOnline()) return null;
            return p;
        }
        return null;
    }
    public String getWinnerName() {
        if (getWinner() == null) return "NONE";
        return getWinner().getName();
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
    public void checkBorder() {
        if (!plugin.getBorderManager().exist()) {
            plugin.getLogger().info(TextUtils.color("&aBorder was succesfully created!"));
            plugin.getBorderManager().removeBorder();
            plugin.getBorderManager().createBorder();
        }
    }
    public void updateScoreboard(){
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().stream().filter(player -> player.isOnline()).forEach(plugin.getScoreboardManager()::updateScoreboard);
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}