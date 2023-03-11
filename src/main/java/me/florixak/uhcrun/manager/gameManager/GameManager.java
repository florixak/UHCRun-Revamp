package me.florixak.uhcrun.manager.gameManager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.BroadcastMessageAction;
import me.florixak.uhcrun.action.actions.TitleAction;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.KitsManager;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.SoundManager;
import me.florixak.uhcrun.task.*;
import me.florixak.uhcrun.utils.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class GameManager {

    private final UHCRun plugin;
    public GameState gameState = GameState.WAITING;

    private FileConfiguration config, messages;
    private TitleAction titleAction;
    private String prefix;

    private MiningCd miningCountdown;
    private FightingCd fightingCountdown;
    private DeathMCd deathmatchCountdown;
    private EndingCd endingCountdown;

    // private Cuboid cuboid;
    private BroadcastMessageAction broadcastMessageAction;

    public GameManager(UHCRun plugin){
        this.plugin = plugin;
        this.broadcastMessageAction = new BroadcastMessageAction();

        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.messages = plugin.getConfigManager().getFile(ConfigType.MESSAGES).getConfig();
        this.prefix = messages.getString("Messages.prefix");
        this.titleAction = new TitleAction();

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
                plugin.getGame().removeScoreboard();
                break;

            case STARTING:
                plugin.getGame().removeScoreboard();
                new StartingCd(plugin).startCountdown();
                break;

            case MINING:
                plugin.getGame().removeScoreboard();
                Bukkit.getOnlinePlayers().stream().filter(player -> PlayerManager.isOnline(player)).forEach(this::setPlayersForGame);
                plugin.getKitsManager().getKits();
                plugin.getTeamManager().addToTeam();
                teleportPlayers();
                new MiningCd(plugin).startCountdown();
                plugin.getUtilities().broadcast(Messages.GAME_STARTED.toString());
                SoundManager.playGameStarted(null, true);
                plugin.getUtilities().broadcast(Messages.MINING.toString().replace("%countdown%", "" + TimeUtils.convertCountdown(MiningCd.count)));
                break;

            case FIGHTING:
                plugin.getGame().removeScoreboard();
                Bukkit.getOnlinePlayers().forEach(player -> teleportPlayersAfterMining(player));
                this.fightingCountdown = new FightingCd(this);
                this.fightingCountdown.runTaskTimer(plugin, 0, 20);
                plugin.getUtilities().broadcast(Messages.PVP.toString());
                plugin.getUtilities().broadcast(Messages.BORDER_SHRINK.toString());
                break;

            case DEATHMATCH:
                plugin.getGame().removeScoreboard();
                this.deathmatchCountdown = new DeathMCd(this);
                this.deathmatchCountdown.runTaskTimer(plugin, 0, 20);
                plugin.getUtilities().broadcast(Messages.DEATHMATCH_STARTED.toString());
                Bukkit.getOnlinePlayers().forEach(player -> SoundManager.playDMBegan(player));
                break;

            case ENDING:
                plugin.getGame().removeScoreboard();
                this.endingCountdown = new EndingCd(this);
                this.endingCountdown.runTaskTimer(plugin, 0, 20);
                plugin.getUtilities().broadcast(Messages.GAME_ENDED.toString());
                plugin.getGame().end();
                break;
        }
    }

    public void checkGame() {

        if (isWaiting()) {
            int min = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getInt("min-players-to-start");
            if (PlayerManager.online.size() >= min) {
                Bukkit.getOnlinePlayers().forEach(player -> SoundManager.playStartingSound(player));
                setGameState(GameState.STARTING);
                Bukkit.broadcastMessage(Messages.GAME_STARTING.toString().replace("%countdown%", "" + TimeUtils.convertCountdown(StartingCd.count)));
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

        PlayerManager.kills.clear();

        PlayerManager.alive.clear();
        PlayerManager.dead.clear();
        PlayerManager.creator.clear();
        PlayerManager.online.clear();

        KitsManager.noKit.clear();
        KitsManager.starter.clear();
        KitsManager.miner.clear();
        KitsManager.enchanter.clear();

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

    public int getWereAlive() {
        return PlayerManager.alive.size();
    }

    public void setSpectator(Player p) {

        p.teleport(new Location(
                Bukkit.getWorld(config.getString("game-world")),
                p.getLocation().getX()+0,
                p.getLocation().getY()+10,
                p.getLocation().getZ()+0));

        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);

        p.getInventory().clear();
        new VanishUtils().toggleVanish(p);

        KitsManager.getSpectatorKit(p);

        p.setAllowFlight(true);
        p.setFlying(true);
    }

    public void kill(Player p) {
        plugin.getStatisticManager().addKill(p.getUniqueId(), 1);
        PlayerManager.kills.put(p.getUniqueId(), PlayerManager.kills.get(p.getUniqueId())+1);
        p.giveExp(config.getInt("xp-per-kill"));
    }
    public void death(Player p) {
        PlayerManager.alive.remove(p.getUniqueId());
        PlayerManager.dead.add(p.getUniqueId());
        plugin.getStatisticManager().addDeath(p.getUniqueId(), 1);
        setSpectator(p);
    }
    public void end() {
        List<String> win_rewards = messages.getStringList("Messages.win-rewards");
        List<String> lose_rewards = messages.getStringList("Messages.lose-rewards");

        double money_for_win;
        double money_for_kills;
        double level_xp_for_win;
        double level_xp_for_kills;
        double money_for_lose;
        double level_xp_for_lose;

        broadcastMessageAction.execute(plugin, null, Messages.WINNER.toString().replace("%winner%", getWinnerName()));

        for (Player p : Bukkit.getOnlinePlayers()) {

            money_for_kills = config.getDouble("coins-per-kill")*PlayerManager.kills.get(p.getUniqueId());
            level_xp_for_kills = config.getDouble("player-level.level-xp-per-kill")*PlayerManager.kills.get(p.getUniqueId());
            money_for_win = config.getDouble("coins-per-win");
            level_xp_for_win = config.getDouble("player-level.level-xp-per-win");

            money_for_lose = config.getDouble("coins-per-lose");
            level_xp_for_lose = config.getDouble("player-level.level-xp-per-lose");


            if (p == getWinner()) {
                plugin.getStatisticManager().addWin(p.getUniqueId(), 1);
                plugin.getStatisticManager().addMoney(Bukkit.getPlayer(p.getUniqueId()), money_for_win + money_for_kills);
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
                plugin.getStatisticManager().addMoney(Bukkit.getPlayer(p.getUniqueId()), money_for_lose+money_for_kills);
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

    public Player getWinner() {
        for (UUID uuid : PlayerManager.alive) {
            if (uuid == null) return null;
            return Bukkit.getPlayer(uuid);
        }
        return null;
    }
    public String getWinnerName() {
        if (getWinner() == null) return "NONE";
        return getWinner().getDisplayName();
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
                Bukkit.getOnlinePlayers().stream().filter(player -> PlayerManager.isOnline(player)).forEach(plugin.getScoreboardManager()::updateScoreboard);
            }
        }.runTaskTimer(plugin, 0, 20);
    }
    public void removeScoreboard(){
        Bukkit.getOnlinePlayers().stream().filter(player -> PlayerManager.isOnline(player)).forEach(plugin.getScoreboardManager()::removeScoreboard);
    }
}