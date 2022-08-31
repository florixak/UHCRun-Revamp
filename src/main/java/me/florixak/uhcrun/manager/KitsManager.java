package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.utility.ItemUtil;
import me.florixak.uhcrun.utility.TextUtil;
import me.florixak.uhcrun.utility.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class KitsManager {

    public static ArrayList<UUID> noKit = new ArrayList<UUID>();
    public static ArrayList<UUID> starter = new ArrayList<UUID>();
    public static ArrayList<UUID> miner = new ArrayList<UUID>();
    public static ArrayList<UUID> enchanter = new ArrayList<UUID>();
    public static ArrayList<UUID> healer = new ArrayList<UUID>();
    public static ArrayList<UUID> horse_rider = new ArrayList<UUID>();

    public static boolean haveNoKit(Player p) { return noKit.contains(p.getUniqueId()); }
    public static boolean haveStarter(Player p) { return starter.contains(p.getUniqueId()); }
    public static boolean haveMiner(Player p) { return miner.contains(p.getUniqueId()); }
    public static boolean haveEnchanter(Player p) { return enchanter.contains(p.getUniqueId()); }
    public static boolean haveHealer(Player p) { return healer.contains(p.getUniqueId()); }
    public static boolean haveHorseRider(Player p) { return horse_rider.contains(p.getUniqueId()); }

    public static void getKits() {
        Bukkit.getOnlinePlayers().stream().filter(player -> haveStarter(player)).forEach(KitsManager::getStarter);
        Bukkit.getOnlinePlayers().stream().filter(player -> haveMiner(player)).forEach(KitsManager::getMiner);
        Bukkit.getOnlinePlayers().stream().filter(player -> haveEnchanter(player)).forEach(KitsManager::getEnchanter);
        Bukkit.getOnlinePlayers().stream().filter(player -> haveHealer(player)).forEach(KitsManager::getHealer);
        Bukkit.getOnlinePlayers().stream().filter(player -> haveHorseRider(player)).forEach(KitsManager::getHorseRider);
    }

    public static void getCreatorKit(Player p) {
        p.getInventory().addItem(ItemUtil.item(new ItemStack(Material.STICK), "Set Waiting Lobby", 1, false));
        p.getInventory().addItem(ItemUtil.item(new ItemStack(Material.BLAZE_ROD), "Set Ending Lobby", 1, false));

    }
    public static void getWaitingKit(Player p) {
        FileConfiguration config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        FileConfiguration permissions = UHCRun.plugin.getConfigManager().getFile(ConfigType.PERMISSIONS).getConfig();
        ItemStack item;

        if (config.getBoolean("lobby-items.kits.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.kits").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.kits.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.kits.display-name")), 1, false));
        }
        if (config.getBoolean("lobby-items.perks.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.perks").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.perks.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.perks.display-name")), 1, false));
        }
        if (p.hasPermission(permissions.getString("permissions.start-item"))) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.start").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.start.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.start.display-name")), 1, false));
        }
        if (config.getBoolean("lobby-items.vote.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.vote").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.vote.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.vote.display-name")), 1, false));
        }
        if (config.getBoolean("lobby-items.custom-crafts.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.custom-crafts").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.custom-crafts.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.custom-crafts.display-name")), 1, false));
        }
        p.getInventory().setItem(config.getInt("lobby-items.statistics.slot"), ItemUtil.item(UHCRun.plugin.getUtilities().getPlayerHead(p.getName(), p.getName()), TextUtil.color(config.getString("lobby-items.statistics.display-name")), 1, false));

    }
    public static void getSpectatorKit(Player p) {
        FileConfiguration config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        ItemStack item;

        if (config.getBoolean("spectator-items.spectator.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("spectator-items.spectator").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("spectator-items.spectator.slot"), ItemUtil.item(item, TextUtil.color(config.getString("spectator-items.spectator.display-name")), 1, false));
        }
        if (config.getBoolean("spectator-items.leave.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("spectator-items.leave").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("spectator-items.leave.slot"), ItemUtil.item(item, TextUtil.color(config.getString("spectator-items.leave.display-name")), 1, false));
        }
    }

    public static void disbandKits(Player p) {
        if (KitsManager.haveStarter(p)) {
            KitsManager.starter.remove(p.getUniqueId());
        }
        if (KitsManager.haveMiner(p)) {
            KitsManager.miner.remove(p.getUniqueId());
        }
        if (KitsManager.haveEnchanter(p)) {
            KitsManager.enchanter.remove(p.getUniqueId());
        }
        if (KitsManager.haveHealer(p)) {
            KitsManager.healer.remove(p.getUniqueId());
        }
        if (KitsManager.haveHorseRider(p)) {
            KitsManager.horse_rider.remove(p.getUniqueId());
        }
        if (!KitsManager.haveNoKit(p)) {
            KitsManager.noKit.add(p.getUniqueId());
        }
    }
    public static void getStarter(Player p) {

        ItemStack axe = new ItemStack(Material.WOODEN_AXE);
        ItemStack pickaxe = new ItemStack(Material.WOODEN_PICKAXE);
        ItemStack shovel = new ItemStack(Material.WOODEN_SHOVEL);
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 16);

        ItemUtil.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtil.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        ItemUtil.addEnchant(shovel, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(shovel, Enchantment.DURABILITY, 3, false);

        p.getInventory().addItem(axe);
        p.getInventory().addItem(pickaxe);
        p.getInventory().addItem(shovel);
        p.getInventory().addItem(food);
    }
    public static void getMiner(Player p) {

        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemStack shovel = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 16);

        ItemUtil.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtil.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        ItemUtil.addEnchant(shovel, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(shovel, Enchantment.DURABILITY, 3, false);

        p.getInventory().addItem(axe);
        p.getInventory().addItem(pickaxe);
        p.getInventory().addItem(shovel);
        p.getInventory().addItem(food);
    }
    public static void getEnchanter(Player p) {

        ItemStack axe = new ItemStack(Material.WOODEN_AXE);
        ItemStack pickaxe = new ItemStack(Material.WOODEN_PICKAXE);
        ItemStack enchant_table = new ItemStack(Material.ENCHANTING_TABLE);
        ItemStack bookshelf = new ItemStack(Material.BOOKSHELF, 8);
        ItemStack exp_bottle = new ItemStack(Material.EXPERIENCE_BOTTLE, 32);
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 16);

        ItemUtil.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtil.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        p.getInventory().addItem(axe);
        p.getInventory().addItem(pickaxe);
        p.getInventory().addItem(enchant_table);
        p.getInventory().addItem(bookshelf);
        p.getInventory().addItem(exp_bottle);
        p.getInventory().addItem(food);
    }
    public static void getHealer(Player p) {
        ItemStack axe = new ItemStack(Material.STONE_AXE);
        ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE);
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 16);
        ItemStack gcarrot = new ItemStack(Material.GOLDEN_CARROT, 12);
        ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 3);

        ItemUtil.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtil.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        p.getInventory().addItem(axe);
        p.getInventory().addItem(pickaxe);
        p.getInventory().addItem(food);
        p.getInventory().addItem(gcarrot);
        p.getInventory().addItem(gapple);
    }
    public static void getHorseRider(Player p) {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE);
        ItemStack axe = new ItemStack(Material.STONE_AXE);
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 16);
        ItemStack horse = new ItemStack(Material.HORSE_SPAWN_EGG);
        ItemStack saddle = new ItemStack(Material.SADDLE);
        ItemStack horse_armor = new ItemStack(Material.IRON_HORSE_ARMOR);

        ItemUtil.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtil.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        p.getInventory().addItem(sword);
        p.getInventory().addItem(axe);
        p.getInventory().addItem(pickaxe);
        p.getInventory().addItem(food);
        p.getInventory().addItem(horse);
        p.getInventory().addItem(saddle);
        p.getInventory().addItem(horse_armor);
    }
}