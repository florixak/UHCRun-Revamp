package me.florixak.uhcrun.manager.gameManager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.BroadcastMessageAction;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.KitsManager;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.SoundManager;
import me.florixak.uhcrun.task.*;
import me.florixak.uhcrun.utility.Cuboid;
import me.florixak.uhcrun.utility.OreUtility;
import me.florixak.uhcrun.utility.TeleportUtil;
import me.florixak.uhcrun.utility.TimeConvertor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.UUID;

public class GameManager {

    private final UHCRun plugin;
    public GameState gameState = GameState.WAITING;

    private StartingCountdown startingCountdown;
    private MiningCountdown miningCountdown;
    private FightingCountdown fightingCountdown;
    private DeathmatchCountdown deathmatchCountdown;
    private EndingCountdown endingCountdown;

    private Cuboid cuboid;
    private BroadcastMessageAction broadcastMessageAction;

    public int wereAlive;

    public GameManager(UHCRun plugin){
        this.plugin = plugin;
        this.wereAlive = PlayerManager.alive.size();
        this.broadcastMessageAction = new BroadcastMessageAction();

//        World w = Bukkit.getWorld("world");
//        int spawnx = (int) w.getSpawnLocation().getX();
//        int spawnz = (int) w.getSpawnLocation().getZ();
//        int y = w.getHighestBlockYAt(spawnx, spawnz)+4;
//
//        Cuboid cuboid1 = new Cuboid(new Location(w, spawnx, y, spawnz), new Location(w, spawnx-10, y+4, spawnz+10));
//        Cuboid cuboid2 = new Cuboid(new Location(w, spawnx+1, y+1, spawnz+1), new Location(w, spawnx-9, y+4, spawnz+9));
//
//        this.cuboid = cuboid1;
//        for (Block block : cuboid1.getBlocks()) {
//            block.setType(Material.GLASS);
//        }
//        for (Block block : cuboid2.getBlocks()) {
//            block.setType(Material.AIR);
//        }
    }

    public void setGameState(GameState gameState){
        if (this.gameState == gameState) return;
        this.gameState = gameState;

        switch (gameState) {

            case WAITING:
                if (!this.startingCountdown.isCancelled()) this.startingCountdown.cancel();
                plugin.getUtilities().removeScoreboard();
                break;

            case STARTING:
                plugin.getUtilities().removeScoreboard();
                this.startingCountdown = new StartingCountdown(this);
                this.startingCountdown.runTaskTimer(plugin, 0, 20);
                break;

            case MINING:
                plugin.getUtilities().removeScoreboard();
                Bukkit.getOnlinePlayers().stream().filter(player -> PlayerManager.isOnline(player)).forEach(this::setPlayersForGame);
                teleportPlayers();
                this.miningCountdown = new MiningCountdown(this);
                this.miningCountdown.runTaskTimer(plugin, 0, 20);
                Bukkit.broadcastMessage(Messages.GAME_STARTED.toString());
                plugin.getSoundManager().playGameStarted(null, true);
                Bukkit.broadcastMessage(Messages.MINING.toString().replace("%countdown%", "" + TimeConvertor.convertCountdown(MiningCountdown.count)));
                break;

            case FIGHTING:
                plugin.getUtilities().removeScoreboard();
                Bukkit.getOnlinePlayers().forEach(player -> teleportPlayersAfterMining(player));
                this.fightingCountdown = new FightingCountdown(this);
                this.fightingCountdown.runTaskTimer(plugin, 0, 20);
                Bukkit.broadcastMessage(Messages.PVP.toString());
                Bukkit.broadcastMessage(Messages.BORDER_SHRINK.toString());
                break;

            case DEATHMATCH:
                plugin.getUtilities().removeScoreboard();
                this.deathmatchCountdown = new DeathmatchCountdown(this);
                this.deathmatchCountdown.runTaskTimer(plugin, 0, 20);
                Bukkit.broadcastMessage(Messages.DEATHMATCH_STARTED.toString());
                Bukkit.getOnlinePlayers().forEach(player -> plugin.getSoundManager().playDeathmatchBegan(player));
                break;

            case ENDING:
                plugin.getUtilities().removeScoreboard();
                this.endingCountdown = new EndingCountdown(this);
                this.endingCountdown.runTaskTimer(plugin, 0, 20);
                Bukkit.broadcastMessage(Messages.GAME_ENDED.toString());
                plugin.getUtilities().end();
                break;
        }
    }

    public void checkGame() {

        if (isWaiting()) {
            int min = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getInt("min-players-to-start");
            if (PlayerManager.online.size() >= min) {
                Bukkit.getOnlinePlayers().forEach(player -> plugin.getSoundManager().playStartingSound(player));
                setGameState(GameState.STARTING);
                Bukkit.broadcastMessage(Messages.GAME_STARTING.toString().replace("%countdown%", "" + TimeConvertor.convertCountdown(StartingCountdown.count)));
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
        OreUtility oreUtility = new OreUtility();
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

    public void setPlayersForGame(Player p) {

        if (PlayerManager.isCreator(p)) {
            PlayerManager.creator.remove(p.getUniqueId());
        }
        PlayerManager.alive.add(p.getUniqueId());
        PlayerManager.kills.put(p.getUniqueId(), 0);
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(p.getHealthScale());
        p.setFoodLevel(20);
        p.getInventory().clear();

        wereAlive = PlayerManager.alive.size();

        KitsManager.getKits();
    }
    public void teleportPlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(TeleportUtil.teleportToSafeLocation()));
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

    public int getWereAlive() {
        return wereAlive;
    }
}