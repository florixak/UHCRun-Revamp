package me.florixak.uhcrevamp.game;

import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class GameValues {

    private static final GameManager gameManager = GameManager.getGameManager();
    private static final FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

    public static final int ERROR_INT_VALUE = -1;

    public static final String WORLD_NAME = "UHCWorld";

    public static final GameSettings GAME = new GameSettings();
    public static final ChatConfig CHAT = new ChatConfig();
    public static final BorderConfig BORDER = new BorderConfig();
    public static final TeamConfig TEAM = new TeamConfig();
    public static final KitConfig KITS = new KitConfig();
    public static final PerkConfig PERKS = new PerkConfig();
    public static final StatisticConfig STATISTICS = new StatisticConfig();
    public static final RewardConfig REWARDS = new RewardConfig();
    public static final InventoryConfig INVENTORY = new InventoryConfig();
    public static final TablistConfig TABLIST = new TablistConfig();
    public static final ActivityRewardConfig ACTIVITY_REWARDS = new ActivityRewardConfig();
    public static final AddonConfig ADDONS = new AddonConfig();
    public static final DeathChestConfig DEATH_CHEST = new DeathChestConfig();

    public static final List<Material> WOOD_LOGS = Arrays.asList(
            XMaterial.OAK_LOG.parseMaterial(),
            XMaterial.BIRCH_LOG.parseMaterial(),
            XMaterial.ACACIA_LOG.parseMaterial(),
            XMaterial.JUNGLE_LOG.parseMaterial(),
            XMaterial.SPRUCE_LOG.parseMaterial(),
            XMaterial.DARK_OAK_LOG.parseMaterial());

    // Nested static classes for better organization of config sections
    public static class ChatConfig {
        public final String IN_GAME_FORMAT = getConfigString("settings.chat.in-game-format", "");
        public final String DEAD_FORMAT = getConfigString("settings.chat.dead-format", "");
        public final String LOBBY_FORMAT = getConfigString("settings.chat.lobby-format", "");
        public final List<String> BLOCKED_COMMANDS = getConfigStringList("settings.chat.blocked-commands");
    }

    public static class BorderConfig {
        public final double INIT_BORDER_SIZE = getConfigDouble("settings.border.size", 300);
        public final double BORDER_SPEED = getConfigDouble("settings.border.speed", 0);
    }

    public static class TeamConfig {
        public final boolean TEAM_MODE = getConfigBoolean("settings.teams.team-mode", true);
        public final boolean FRIENDLY_FIRE = getConfigBoolean("settings.teams.friendly-fire", false);
        public final int TEAM_SIZE = getConfigInt("settings.teams.max-size", 3);
    }

    public static class KitConfig {
        public final boolean ENABLED = getConfigBoolean("settings.kits.enabled", true);
        public final boolean BOUGHT_FOREVER = getConfigBoolean("settings.kits.bought-forever", true);
    }

    public static class PerkConfig {
        public final boolean ENABLED = getConfigBoolean("settings.perks.enabled", true);
        public final boolean BOUGHT_FOREVER = getConfigBoolean("settings.perks.bought-forever", true);
    }

    public static class StatisticConfig {
        public final boolean UHC_LEVEL_ENABLED = getConfigBoolean("settings.statistics.uhc-level.enabled", true);
        public final int FIRST_UHC_LEVEL = getConfigInt("settings.statistics.uhc-level.first-uhc-level", 0);
        public final double FIRST_REQUIRED_EXP = getConfigDouble("settings.statistics.uhc-level.first-required-exp", 100.0);
        public final double EXP_MULTIPLIER = getConfigDouble("settings.statistics.uhc-level.exp-multiplier", 3.75);
        public final String PLAYER_STATS_DIS_ITEM = getConfigString("settings.statistics.player-stats.display-item", "STONE");
        public final String PLAYER_STATS_CUST_NAME = getConfigString("settings.statistics.player-stats.custom-name", "YOUR STATS");
        public final List<String> PLAYER_STATS_LORE = getConfigStringList("settings.statistics.player-stats.lore");
        public final int PLAYER_STATS_SLOT = 13;
        public final String TOP_STATS_DIS_ITEM = getConfigString("settings.statistics.top-stats.display-item", "PAPER");
        public final String TOP_STATS_CUST_NAME = getConfigString("settings.statistics.top-stats.custom-name", "TOP STATS");
        public final List<String> TOP_STATS_LORE = getConfigStringList("settings.statistics.top-stats.lore");
        public final List<String> DISPLAYED_TOPS = getConfigStringList("settings.statistics.displayed-tops");
        public final int TOP_SLOT = 17;
    }

    public static class RewardConfig {
        private final double MULTIPLIER = getConfigDouble("settings.rewards.multiplier", 1);
        public final double BASE_REWARD = getConfigDouble("settings.rewards.base-reward", 100);
        public final double REWARD_COEFFICIENT = getConfigDouble("settings.rewards.reward-coefficient", 1);
        public final double COINS_FOR_WIN = getConfigDouble("settings.rewards.win.coins", 0) * MULTIPLIER;
        public final double UHC_EXP_FOR_WIN = getConfigInt("settings.rewards.win.uhc-exp", 0) * MULTIPLIER;
        public final double COINS_FOR_LOSE = getConfigDouble("settings.rewards.lose.coins", 0) * MULTIPLIER;
        public final double UHC_EXP_FOR_LOSE = getConfigInt("settings.rewards.lose.uhc-exp", 0) * MULTIPLIER;
        public final double COINS_FOR_KILL = getConfigDouble("settings.rewards.kill.coins", 0) * MULTIPLIER;
        public final double UHC_EXP_FOR_KILL = getConfigDouble("settings.rewards.kill.uhc-exp", 0) * MULTIPLIER;
        public final double EXP_FOR_KILL = getConfigDouble("settings.rewards.kill.exp", 0) * MULTIPLIER;
        public final double COINS_FOR_ASSIST = getConfigDouble("settings.rewards.assist.coins", 0) * MULTIPLIER;
        public final double UHC_EXP_FOR_ASSIST = getConfigDouble("settings.rewards.assist.uhc-exp", 0) * MULTIPLIER;
    }

    public static class InventoryConfig {
        public final String TEAMS_TITLE = getConfigString("settings.inventories.teams.display-name", "Teams");
        public final String KITS_TITLE = getConfigString("settings.inventories.kits.display-name", "Kits");
        public final String PERKS_TITLE = getConfigString("settings.inventories.perks.display-name", "Perks");
        public final String CUSTOM_RECIPES_TITLE = getConfigString("settings.inventories.custom-recipes.display-name", "Custom Recipes");
        public final String STATS_TITLE = getConfigString("settings.inventories.statistics.display-name", "Statistics");

        public final boolean CONFIRM_PURCHASE_ENABLED = getConfigBoolean("settings.inventories.confirm-purchase.enabled", true);
        public final String CONFIRM_PURCHASE_NAME = getConfigString("settings.inventories.confirm-purchase.confirm", "&aConfirm");
        public final String CANCEL_PURCHASE_NAME = getConfigString("settings.inventories.confirm-purchase.cancel", "&cCancel");
        public final String CONFIRM_PURCHASE_ITEM = getConfigString("settings.inventories.confirm-purchase.confirm-item", "BARRIER");
        public final String CANCEL_PURCHASE_ITEM = getConfigString("settings.inventories.confirm-purchase.cancel-item", "BARRIER");

        public final String BACK_TITLE = getConfigString("settings.inventories.back", "&7Back");
        public final String BACK_ITEM = getConfigString("settings.inventories.back-item", "DARK_OAK_BUTTON");
        public final String NEXT_TITLE = getConfigString("settings.inventories.next", "&7Next");
        public final String NEXT_ITEM = getConfigString("settings.inventories.next-item", "DARK_OAK_BUTTON");
        public final String PREVIOUS_TITLE = getConfigString("settings.inventories.previous", "&7Previous");
        public final String PREVIOUS_ITEM = getConfigString("settings.inventories.previous-item", "DARK_OAK_BUTTON");
        public final String CLOSE_TITLE = getConfigString("settings.inventories.close", "&cClose");
        public final String CLOSE_ITEM = getConfigString("settings.inventories.close-item", "BARRIER");

        public final int KITS_SLOTS = getConfigInt("settings.inventories.kits.slots", 45);
        public final int PERKS_SLOTS = getConfigInt("settings.inventories.perks.slots", 45);
        public final int CUSTOM_RECIPES_SLOTS = getConfigInt("settings.inventories.custom-recipes.slots", 45);
        public final int STATS_SLOTS = getConfigInt("settings.inventories.statistics.slots", 45);
        public final int TEAMS_SLOTS = getConfigInt("settings.inventories.teams.slots", 45);
        public final int CONFIRM_PURCHASE_SLOTS = getConfigInt("settings.inventories.confirm-purchase.slots", 9);

        public final String TEAMS_DIS_ITEM = getConfigString("settings.inventories.teams.display-item", "BARRIER");
        public final String KITS_DIS_ITEM = getConfigString("settings.inventories.kits.display-item", "BARRIER");
        public final String PERKS_DIS_ITEM = getConfigString("settings.inventories.perks.display-item", "BARRIER");
        public final String CUSTOM_RECIPES_DIS_ITEM = getConfigString("settings.inventories.custom-recipes.display-item", "BARRIER");
        public final String STATS_DIS_ITEM = getConfigString("settings.inventories.statistics.display-item", "BARRIER");
    }

    public static class ScoreboardConfig {
        public final boolean ENABLED = getConfigBoolean("settings.scoreboard.enabled", true);
        public final String TITLE = getConfigString("settings.scoreboard.title", "&2&lUHC");
        public final List<String> WAITING = getConfigStringList("settings.scoreboard.lines");
    }

    public static class TablistConfig {
        public final boolean ENABLED = getConfigBoolean("settings.tablist.enabled", true);
        public final String HEADER = getConfigString("settings.tablist.header", "Tablist Header");
        public final String FOOTER = getConfigString("settings.tablist.footer", "Tablist Footer");
        public final String SOLO_MODE = getConfigString("settings.tablist.solo-mode-player-list", "&f%player%");
        public final String TEAM_MODE = getConfigString("settings.tablist.team-mode-player-list", "%team% &f%player%");
    }

    public static class ActivityRewardConfig {
        public final boolean ENABLED = getConfigBoolean("settings.rewards.activity.enabled", true);
        public final int INTERVAL = getConfigInt("settings.rewards.activity.period", 300);
        public final double MONEY = getConfigDouble("settings.rewards.activity.money", 10);
        public final double EXP = getConfigDouble("settings.rewards.activity.uhc-exp", 20);
    }

    public static class GameSettings {
        public final boolean TELEPORT_AFTER_MINING = getConfigBoolean("settings.game.teleport-after-mining", true);
        public final boolean CUSTOM_DROPS_ENABLED = getConfigBoolean("settings.game.custom-drops", true);
        public final boolean EXPLOSIONS_DISABLED = getConfigBoolean("settings.game.no-explosions", true);
        public final boolean RANDOM_DROPS_ENABLED = getConfigBoolean("settings.game.random-drops", false);
        public final boolean NETHER_ENABLED = getConfigBoolean("settings.game.allow-nether", false);
        public final boolean PROJECTILE_HIT_HP_ENABLED = getConfigBoolean("settings.game.projectile-hit-hp", false);
        public final int PLAYERS_TO_START = getConfigInt("settings.game.players-to-start", 2);
        public final int STARTING_COUNTDOWN = getConfigInt("settings.game.countdowns.starting", 20);
        public final int STARTING_MESSAGE_AT = getConfigInt("settings.game.starting-message-at", 10);
        public final int MINING_COUNTDOWN = getConfigInt("settings.game.countdowns.mining", 600);
        public final int PVP_COUNTDOWN = getConfigInt("settings.game.countdowns.pvp", 600);
        public final int DEATHMATCH_COUNTDOWN = getConfigInt("settings.game.countdowns.deathmatch", 300);
        public final int ENDING_COUNTDOWN = getConfigInt("settings.game.countdowns.ending", 20);
        public final List<String> DISABLED_IN_MINING = getConfigStringList("settings.game.disabled-in-mining");
        public final int RESISTANCE_COUNTDOWN = getConfigInt("settings.game.countdowns.resistance", 10);
        public final int MAX_ORE_PER_CHUNK = getConfigInt("settings.game.max-ore-per-chunk", 10);
        public final List<String> DISABLED_BIOMES = getConfigStringList("settings.game.disabled-biomes");
    }

    public static class AddonConfig {
        public final boolean CAN_USE_VAULT = getConfigBoolean("settings.addons.use-Vault", true);
        public final boolean CAN_USE_LUCKPERMS = getConfigBoolean("settings.addons.use-LuckPerms", true);
        public final boolean CAN_USE_PLACEHOLDERAPI = getConfigBoolean("settings.addons.use-PlaceholderAPI", true);
        public final boolean CAN_USE_PROTOCOLLIB = getConfigBoolean("settings.addons.use-ProtocolLib", true);
        public final boolean CAN_USE_HEADDATABASE = getConfigBoolean("settings.addons.use-HeadDatabase", false);
    }

    public static class DeathChestConfig {
        public final boolean ENABLED = getConfigBoolean("settings.death-chest.enabled", true);
        public final String HOLOGRAM_TEXT = getConfigString("settings.death-chest.hologram-text", "&a%player%'s chest");
        public final int HOLOGRAM_EXPIRE_TIME = getConfigInt("settings.death-chest.expire", -1);
    }

    public static class Sounds {
        public static final String STARTED_SOUND = getConfigString("settings.sounds.started.sound", "");
        public static final float STARTED_VOLUME = (float) getConfigDouble("settings.sounds.started.volume", 1.0);
        public static final float STARTED_PITCH = (float) getConfigDouble("settings.sounds.started.pitch", 1.0);

        public static final String STARTING_SOUND = getConfigString("settings.sounds.starting.sound", "");
        public static final float STARTING_VOLUME = (float) getConfigDouble("settings.sounds.starting.volume", 1.0);
        public static final float STARTING_PITCH = (float) getConfigDouble("settings.sounds.starting.pitch", 1.0);

        public static final String WIN_SOUND = getConfigString("settings.sounds.win.sound", "");
        public static final float WIN_VOLUME = (float) getConfigDouble("settings.sounds.win.volume", 1.0);
        public static final float WIN_PITCH = (float) getConfigDouble("settings.sounds.win.pitch", 1.0);

        public static final String GAME_END_SOUND = getConfigString("settings.sounds.game-end.sound", "");
        public static final float GAME_END_VOLUME = (float) getConfigDouble("settings.sounds.game-end.volume", 1.0);
        public static final float GAME_END_PITCH = (float) getConfigDouble("settings.sounds.game-end.pitch", 1.0);

        public static final String DEATHMATCH_SOUND = getConfigString("settings.sounds.deathmatch.sound", "");
        public static final float DEATHMATCH_VOLUME = (float) getConfigDouble("settings.sounds.deathmatch.volume", 1.0);
        public static final float DEATHMATCH_PITCH = (float) getConfigDouble("settings.sounds.deathmatch.pitch", 1.0);

        public static final String DEATHMATCH_STARTING_SOUND = getConfigString("settings.sounds.deathmatch-starting.sound", "");
        public static final float DEATHMATCH_STARTING_VOLUME = (float) getConfigDouble("settings.sounds.deathmatch-starting.volume", 1.0);
        public static final float DEATHMATCH_STARTING_PITCH = (float) getConfigDouble("settings.sounds.deathmatch-starting.pitch", 1.0);

        public static final String UHC_LEVEL_UP_SOUND = getConfigString("settings.sounds.uhc-level-up.sound", "");
        public static final float UHC_LEVEL_UP_VOLUME = (float) getConfigDouble("settings.sounds.uhc-level-up.volume", 1.0);
        public static final float UHC_LEVEL_UP_PITCH = (float) getConfigDouble("settings.sounds.uhc-level-up.pitch", 1.0);

        public static final String KILL_SOUND = getConfigString("settings.sounds.kill.sound", "");
        public static final float KILL_VOLUME = (float) getConfigDouble("settings.sounds.kill.volume", 1.0);
        public static final float KILL_PITCH = (float) getConfigDouble("settings.sounds.kill.pitch", 1.0);

        public static final String SELECT_SOUND = getConfigString("settings.sounds.select.sound", "");
        public static final float SELECT_VOLUME = (float) getConfigDouble("settings.sounds.select.volume", 1.0);
        public static final float SELECT_PITCH = (float) getConfigDouble("settings.sounds.select.pitch", 1.0);

        public static final String BUY_SOUND = getConfigString("settings.sounds.buy.sound", "");
        public static final float BUY_VOLUME = (float) getConfigDouble("settings.sounds.buy.volume", 1.0);
        public static final float BUY_PITCH = (float) getConfigDouble("settings.sounds.buy.pitch", 1.0);

        public static final String PURCHASE_CANCEL_SOUND = getConfigString("settings.sounds.purchase-cancel.sound", "");
        public static final float PURCHASE_CANCEL_VOLUME = (float) getConfigDouble("settings.sounds.purchase-cancel.volume", 1.0);
        public static final float PURCHASE_CANCEL_PITCH = (float) getConfigDouble("settings.sounds.purchase-cancel.pitch", 1.0);
    }

    private static boolean getConfigBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    private static String getConfigString(String path, String def) {
        return config.getString(path, def);
    }

    private static int getConfigInt(String path, int def) {
        return config.getInt(path, def);
    }

    private static double getConfigDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    private static List<String> getConfigStringList(String path) {
        return config.getStringList(path);
    }
}
