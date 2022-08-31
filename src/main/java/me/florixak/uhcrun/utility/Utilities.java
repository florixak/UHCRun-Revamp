package me.florixak.uhcrun.utility;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.BroadcastMessageAction;
import me.florixak.uhcrun.action.actions.TitleAction;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.manager.KitsManager;
import me.florixak.uhcrun.manager.PlayerManager;
import me.florixak.uhcrun.manager.gameManager.GameManager;
import me.florixak.uhcrun.manager.gameManager.GameState;
import me.florixak.uhcrun.task.AutoBroadcastMessages;
import me.florixak.uhcrun.task.ActivityRewards;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Utilities {

    private UHCRun plugin;
    private FileConfiguration config, statistics, messages, permissions;
    private World world;
    private BroadcastMessageAction broadcastMessageAction;
    private String prefix;
    private TitleAction titleAction;

    public Utilities(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        this.statistics = plugin.getConfigManager().getFile(ConfigType.STATISTICS).getConfig();
        this.messages = plugin.getConfigManager().getFile(ConfigType.MESSAGES).getConfig();
        this.permissions = plugin.getConfigManager().getFile(ConfigType.PERMISSIONS).getConfig();
        this.world = Bukkit.getWorld(config.getString("game-world"));
        this.broadcastMessageAction = new BroadcastMessageAction();
        this.prefix = messages.getString("Messages.prefix");
        this.titleAction = new TitleAction();
    }


    public void setPlayersForGame(Player p) {

        if (PlayerManager.isCreator(p)) {
            PlayerManager.creator.remove(p.getUniqueId());
        }
        PlayerManager.alive.add(p.getUniqueId());
        PlayerManager.kills.put(p.getUniqueId(), 0);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        Location randomLocation = TeleportUtil.teleportToSafeLocation();
        p.teleport(randomLocation);

        GameManager.wereAlive = PlayerManager.alive.size();

        KitsManager.getKits();
    }
    public void setSpectator(Player p) {

        if (PlayerManager.isDead(p)) {
            p.hidePlayer(plugin, p);
        }

        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.getInventory().clear();

        p.setGameMode(GameMode.SURVIVAL);

        KitsManager.getSpectatorKit(p);

        p.setAllowFlight(true);
        p.setFlying(true);

        p.teleport(new Location(
                Bukkit.getWorld(config.getString("game-world")),
                p.getLocation().getX()+0,
                p.getLocation().getY()+10,
                p.getLocation().getZ()+0));
    }

    public void checkGame() {
        if (plugin.getGame().gameState == GameState.WAITING) {
            if (PlayerManager.online.size() >= config.getInt("min-players-to-start")) {
                for (UUID uuid : PlayerManager.online) {
                    Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(),
                            Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f);
                }
                plugin.getGame().setGameState(GameState.STARTING);
            }
            return;
        }
        if (PlayerManager.alive.size() >= 2) return;
        if (plugin.getGame().gameState == GameState.MINING
                || plugin.getGame().gameState == GameState.FIGHTING
                || plugin.getGame().gameState == GameState.DEATHMATCH) {
            plugin.getGame().setGameState(GameState.ENDING);
        }
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

        String winner = getWinner().getDisplayName();
        UUID uuid_winner = getWinner().getUniqueId();
        broadcastMessageAction.execute(plugin, null, Messages.WINNER.toString().replace("%winner%", winner));


        for (UUID uuid : PlayerManager.online) {
            money_for_kills = config.getDouble("coins-per-kill")*PlayerManager.kills.get(uuid);
            level_xp_for_kills = config.getDouble("player-level.level-xp-per-kill")*PlayerManager.kills.get(uuid);
            money_for_win = config.getDouble("coins-per-win");
            level_xp_for_win = config.getDouble("player-level.level-xp-per-win");

            money_for_lose = config.getDouble("coins-per-lose");
            level_xp_for_lose = config.getDouble("player-level.level-xp-per-lose");

            if (uuid == uuid_winner) {
                plugin.getStatisticManager().addWin(uuid, 1);
                plugin.getStatisticManager().addMoney(Bukkit.getPlayer(uuid), money_for_win+money_for_kills);
                plugin.getLevelManager().addPlayerLevel(uuid, level_xp_for_win+level_xp_for_kills);
                titleAction.execute(plugin, Bukkit.getPlayer(uuid), "Victory!");
                for (String reward : win_rewards) {
                    Bukkit.getPlayer(uuid).sendMessage(TextUtil.color(reward
                                    .replace("%coins-for-win%", String.valueOf(money_for_win))
                                    .replace("%coins-for-kills%", String.valueOf(money_for_kills))
                                    .replace("%level-xp-for-win%", String.valueOf(level_xp_for_win))
                                    .replace("%level-xp-for-kills%", String.valueOf(level_xp_for_kills))
                                    .replace("%prefix%", prefix)
                            )
                    );
                }
            } else {
                plugin.getStatisticManager().addMoney(Bukkit.getPlayer(uuid), money_for_lose+money_for_kills);
                plugin.getLevelManager().addPlayerLevel(uuid, level_xp_for_lose+level_xp_for_kills);
                titleAction.execute(plugin, Bukkit.getPlayer(uuid), "Game Over!");
                for (String reward : lose_rewards) {
                    Bukkit.getPlayer(uuid).sendMessage(TextUtil.color(reward
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
    public Player getWinner() {
        for (UUID uuid : PlayerManager.alive) {
            return Bukkit.getPlayer(uuid);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getPlayerHead(String player, String name) {
        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");

        Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
        ItemStack item = new ItemStack(type, 1);

        if (!isNewVersion) item.setDurability((short) 3);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (name != null) meta.setDisplayName(TextUtil.color(name));
        meta.setOwner(player);

        item.setItemMeta(meta);
        return item;
    }
    public void skullTeleport(Player p, ItemStack item) {
        if (item.getType() != Material.AIR && item.getType() != null) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta.getDisplayName() != null) {
                if (Bukkit.getPlayer(meta.getDisplayName()) != null) {
                    Player player = Bukkit.getPlayer(meta.getDisplayName());
                    if (player == null) {
                        p.closeInventory();
                        p.sendMessage(Messages.OFFLINE_PLAYER.toString());
                        return;
                    }
                    p.teleport(player);
                }
            }
        }
    }

    public void addPotion(Player p, PotionEffectType effect, int duration, int power) {
        p.addPotionEffect(new PotionEffect(effect, duration, power));
    }

    public void timber(Block block) {

        if (!(block.getType() == XMaterial.OAK_LOG.parseMaterial()
                || block.getType() == XMaterial.BIRCH_LOG.parseMaterial()
                || block.getType() == XMaterial.ACACIA_LOG.parseMaterial()
                || block.getType() == XMaterial.JUNGLE_LOG.parseMaterial()
                || block.getType() == XMaterial.SPRUCE_LOG.parseMaterial()
                || block.getType() == XMaterial.DARK_OAK_LOG.parseMaterial())) return;

        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOOD_BREAK, 2f, 1f);
        block.breakNaturally();

        timber(block.getLocation().add(0,1,0).getBlock());
        timber(block.getLocation().add(1,0,0).getBlock());
        timber(block.getLocation().add(0,1,1).getBlock());

        timber(block.getLocation().subtract(0,1,0).getBlock());
        timber(block.getLocation().subtract(1,0,0).getBlock());
        timber(block.getLocation().subtract(0,0,1).getBlock());
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
            plugin.getLogger().info(TextUtil.color("&aBorder was succesfully created!"));
            plugin.getBorderManager().removeBorder();
            plugin.getBorderManager().createBorder();
        }
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