package me.florixak.uhcrun.manager;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.utility.ItemUtil;
import me.florixak.uhcrun.utility.TextUtil;
import me.florixak.uhcrun.utility.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public void getKits() {
        Bukkit.getOnlinePlayers().stream().filter(player -> haveStarter(player)).forEach(this::getStarter);
        Bukkit.getOnlinePlayers().stream().filter(player -> haveMiner(player)).forEach(this::getMiner);
        Bukkit.getOnlinePlayers().stream().filter(player -> haveEnchanter(player)).forEach(this::getEnchanter);
        Bukkit.getOnlinePlayers().stream().filter(player -> haveHealer(player)).forEach(this::getHealer);
        Bukkit.getOnlinePlayers().stream().filter(player -> haveHorseRider(player)).forEach(this::getHorseRider);
    }

    public static void getCreatorKit(Player p) {
        p.getInventory().addItem(ItemUtil.item(new ItemStack(Material.STICK), "Set Waiting Lobby", 1));
        p.getInventory().addItem(ItemUtil.item(new ItemStack(Material.BLAZE_ROD), "Set Ending Lobby", 1));

    }

    public static void getWaitingKit(Player p) {
        FileConfiguration config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        FileConfiguration permissions = UHCRun.plugin.getConfigManager().getFile(ConfigType.PERMISSIONS).getConfig();
        ItemStack item;

        if (config.getBoolean("lobby-items.kits.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.kits").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.kits.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.kits.display-name")), 1));
        }
        if (config.getBoolean("lobby-items.perks.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.perks").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.perks.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.perks.display-name")), 1));
        }
        if (p.hasPermission(permissions.getString("permissions.start-item"))) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.start").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.start.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.start.display-name")), 1));
        }
        if (config.getBoolean("lobby-items.vote.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.vote").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.vote.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.vote.display-name")), 1));
        }
        if (config.getBoolean("lobby-items.custom-crafts.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.custom-crafts").getString("material").toUpperCase()).get().parseItem();
            p.getInventory().setItem(config.getInt("lobby-items.custom-crafts.slot"), ItemUtil.item(item, TextUtil.color(config.getString("lobby-items.custom-crafts.display-name")), 1));
        }
        p.getInventory().setItem(config.getInt("lobby-items.statistics.slot"), ItemUtil.item(UHCRun.plugin.getUtilities().getPlayerHead(p.getName(), p.getName()), TextUtil.color(config.getString("lobby-items.statistics.display-name")), 1));

    }
    public static void getSpectatorKit(Player p) {
        FileConfiguration config = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        Material item;

        if (config.getBoolean("spectator-items.spectator.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("spectator-items.spectator").getString("material").toUpperCase()).get().parseMaterial();
            p.getInventory().setItem(config.getInt("spectator-items.spectator.slot"), ItemUtil.item(new ItemStack(item), TextUtil.color(config.getString("spectator-items.spectator.display-name")), 1));
        }
        if (config.getBoolean("spectator-items.leave.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("spectator-items.leave").getString("material").toUpperCase()).get().parseMaterial();
            p.getInventory().setItem(config.getInt("spectator-items.leave.slot"), ItemUtil.item(new ItemStack(item), TextUtil.color(config.getString("spectator-items.leave.display-name")), 1));
        }
    }

    public static void getKit(Player p) {
        FileConfiguration config = UHCRun.plugin.getConfigManager().getFile(ConfigType.KITS).getConfig();
        for (String kit : config.getConfigurationSection("items").getKeys(false)) {
            if (kit.length() > 0) return;


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

    private void getStarter(Player p) {

        ItemStack item;

//        ItemStack axe = new ItemStack(XMaterial.WOODEN_AXE.parseMaterial());
//        ItemStack pickaxe = new ItemStack(XMaterial.WOODEN_PICKAXE.parseMaterial());
//        ItemStack shovel = new ItemStack(XMaterial.WOODEN_SHOVEL.parseMaterial());
//        ItemStack food = new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 16);
//
//        ItemUtil.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
//        ItemUtil.addEnchant(axe, Enchantment.DURABILITY, 3, false);
//
//        ItemUtil.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
//        ItemUtil.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);
//
//        ItemUtil.addEnchant(shovel, Enchantment.DIG_SPEED, 3, false);
//        ItemUtil.addEnchant(shovel, Enchantment.DURABILITY, 3, false);

        FileConfiguration config = UHCRun.plugin.getConfigManager().getFile(ConfigType.KITS).getConfig();

        for (String kit : config.getConfigurationSection("items").getKeys(false)) {
            if (config.getStringList("items." + kit + ".items") == null) return;
            for (String items : config.getConfigurationSection("items." + kit + ".items").getKeys(false)) {
                item = XMaterial.matchXMaterial(config.getConfigurationSection("items." + kit + ".items").getString(items).toLowerCase()).get().parseItem();
                ItemMeta meta = item.getItemMeta();
                for (String enchants : config.getConfigurationSection("items." + kit + ".items." + items + ".enchants").getKeys(false)) {
                    meta.addEnchant(Enchantment.getByName(enchants.toUpperCase()), config.getInt("items." + kit + ".items." + enchants), true);
                }
                item.setAmount(config.getInt("items." + kit + ".items." + items + ".amount"));
                item.setItemMeta(meta);
                p.getInventory().addItem(item);
            }
        }
//        p.getInventory().addItem(axe);
//        p.getInventory().addItem(pickaxe);
//        p.getInventory().addItem(shovel);
//        p.getInventory().addItem(food);
    }
    private void getMiner(Player p) {

        ItemStack axe = new ItemStack(XMaterial.DIAMOND_AXE.parseMaterial());
        ItemStack pickaxe = new ItemStack(XMaterial.DIAMOND_PICKAXE.parseMaterial());
        ItemStack shovel = new ItemStack(XMaterial.DIAMOND_SHOVEL.parseMaterial());
        ItemStack food = new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 16);

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
    private void getEnchanter(Player p) {

        ItemStack axe = new ItemStack(XMaterial.WOODEN_AXE.parseMaterial());
        ItemStack pickaxe = new ItemStack(XMaterial.WOODEN_PICKAXE.parseMaterial());
        ItemStack enchant_table = new ItemStack(XMaterial.ENCHANTING_TABLE.parseMaterial());
        ItemStack bookshelf = new ItemStack(XMaterial.BOOKSHELF.parseMaterial(), 8);
        ItemStack exp_bottle = new ItemStack(XMaterial.EXPERIENCE_BOTTLE.parseMaterial(), 32);
        ItemStack food = new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 16);

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
    private void getHealer(Player p) {
        ItemStack axe = new ItemStack(XMaterial.STONE_AXE.parseMaterial());
        ItemStack pickaxe = new ItemStack(XMaterial.STONE_PICKAXE.parseMaterial());
        ItemStack food = new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 16);
        ItemStack gcarrot = new ItemStack(XMaterial.GOLDEN_CARROT.parseMaterial(), 12);
        ItemStack gapple = new ItemStack(XMaterial.GOLDEN_APPLE.parseMaterial(), 3);

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
    private void getHorseRider(Player p) {
        ItemStack sword = new ItemStack(XMaterial.IRON_SWORD.parseMaterial());
        ItemStack pickaxe = new ItemStack(XMaterial.STONE_PICKAXE.parseMaterial());
        ItemStack axe = new ItemStack(XMaterial.STONE_AXE.parseMaterial());
        ItemStack food = new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 16);
        ItemStack horse = ItemUtil.monsterEgg(EntityType.HORSE);
        ItemStack saddle = new ItemStack(XMaterial.SADDLE.parseMaterial());
        ItemStack horse_armor = new ItemStack(XMaterial.IRON_HORSE_ARMOR.parseMaterial());

        ItemUtil.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtil.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtil.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        p.getInventory().addItem(sword);
        p.getInventory().addItem(pickaxe);
        p.getInventory().addItem(axe);
        p.getInventory().addItem(food);
        p.getInventory().addItem(horse);
        p.getInventory().addItem(saddle);
        p.getInventory().addItem(horse_armor);
    }
}