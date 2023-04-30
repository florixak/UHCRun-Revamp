package me.florixak.uhcrun.kits;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitsManager {

    private GameManager gameManager;
    private FileConfiguration config;

    public KitsManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    public void giveKit(UHCPlayer uhcPlayer) {

        uhcPlayer.sendMessage("You chose " + uhcPlayer.getKit() + " kit");

        switch (uhcPlayer.getKit()) {
            case NONE:
                break;
            case STARTER:
                getStarter(uhcPlayer);
                break;
            case MINER:
                getMiner(uhcPlayer);
                break;
            case ENCHANTER:
                getEnchanter(uhcPlayer);
                break;
            case HEALER:
                getHealer(uhcPlayer);
                break;
            case HORSE_RIDER:
                getHorseRider(uhcPlayer);
                break;
            default:
                uhcPlayer.sendMessage("ERROR with kits");
                break;
        }
    }

    public void getWaitingKit(UHCPlayer p) {
        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        ItemStack item;

        if (config.getBoolean("lobby-items.kits.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.kits").getString("material").toUpperCase()).get().parseItem();
            p.getPlayer().getInventory().setItem(config.getInt("lobby-items.kits.slot"), ItemUtils.item(item, TextUtils.color(config.getString("lobby-items.kits.display-name")), 1));
        }
        if (config.getBoolean("lobby-items.perks.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.perks").getString("material").toUpperCase()).get().parseItem();
            p.getPlayer().getInventory().setItem(config.getInt("lobby-items.perks.slot"), ItemUtils.item(item, TextUtils.color(config.getString("lobby-items.perks.display-name")), 1));
        }

        if (config.getBoolean("lobby-items.custom-crafts.enabled", true)) {
            item = XMaterial.matchXMaterial(config.getConfigurationSection("lobby-items.custom-crafts").getString("material").toUpperCase()).get().parseItem();
            p.getPlayer().getInventory().setItem(config.getInt("lobby-items.custom-crafts.slot"), ItemUtils.item(item, TextUtils.color(config.getString("lobby-items.custom-crafts.display-name")), 1));
        }
        p.getPlayer().getInventory().setItem(config.getInt("lobby-items.statistics.slot"),
                ItemUtils.item(Utils.getPlayerHead(p.getName(), p.getName()),
                TextUtils.color(config.getString("lobby-items.statistics.display-name")), 1));

    }
    public void getSpectatorKit(Player p) {

        if (config.getBoolean("spectator-items.spectator.enabled", true)) {
            // item = XMaterial.matchXMaterial(config.getConfigurationSection("spectator-items.spectator").getString("material").toUpperCase()).get().parseMaterial();
//            p.getInventory().setItem(config.getInt("spectator-items.spectator.slot"), ItemUtils.item(new ItemStack(item), TextUtils.color(config.getString("spectator-items.spectator.display-name")), 1));
            p.getInventory().addItem(ItemUtils.item(new ItemStack(XMaterial.COMPASS.parseMaterial()), TextUtils.color(config.getString("spectator-items.spectator.display-name")), 1));
        }
        if (config.getBoolean("spectator-items.leave.enabled", true)) {
//            item = XMaterial.matchXMaterial(config.getConfigurationSection("spectator-items.leave").getString("material").toUpperCase()).get().parseMaterial();
//            p.getInventory().setItem(config.getInt("spectator-items.leave.slot"), ItemUtils.item(new ItemStack(item), TextUtils.color(config.getString("spectator-items.leave.display-name")), 1));
            p.getInventory().addItem(ItemUtils.item(new ItemStack(XMaterial.RED_BED.parseMaterial()), TextUtils.color(config.getString("spectator-items.spectator.display-name")), 1));
        }
    }

    private void getStarter(UHCPlayer p) {
        ItemStack item;
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
                p.getPlayer().getInventory().addItem(item);
            }
        }
    }
    private void getMiner(UHCPlayer p) {

        ItemStack axe = new ItemStack(XMaterial.DIAMOND_AXE.parseMaterial());
        ItemStack pickaxe = new ItemStack(XMaterial.DIAMOND_PICKAXE.parseMaterial());
        ItemStack shovel = new ItemStack(XMaterial.DIAMOND_SHOVEL.parseMaterial());
        ItemStack food = new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 16);

        ItemUtils.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtils.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        ItemUtils.addEnchant(shovel, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(shovel, Enchantment.DURABILITY, 3, false);

        p.getPlayer().getInventory().addItem(axe);
        p.getPlayer().getInventory().addItem(pickaxe);
        p.getPlayer().getInventory().addItem(shovel);
        p.getPlayer().getInventory().addItem(food);
    }
    private void getEnchanter(UHCPlayer p) {

        ItemStack axe = new ItemStack(XMaterial.WOODEN_AXE.parseMaterial());
        ItemStack pickaxe = new ItemStack(XMaterial.WOODEN_PICKAXE.parseMaterial());
        ItemStack enchant_table = new ItemStack(XMaterial.ENCHANTING_TABLE.parseMaterial());
        ItemStack bookshelf = new ItemStack(XMaterial.BOOKSHELF.parseMaterial(), 8);
        ItemStack exp_bottle = new ItemStack(XMaterial.EXPERIENCE_BOTTLE.parseMaterial(), 32);
        ItemStack food = new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 16);

        ItemUtils.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtils.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        p.getPlayer().getInventory().addItem(axe);
        p.getPlayer().getInventory().addItem(pickaxe);
        p.getPlayer().getInventory().addItem(enchant_table);
        p.getPlayer().getInventory().addItem(bookshelf);
        p.getPlayer().getInventory().addItem(exp_bottle);
        p.getPlayer().getInventory().addItem(food);
    }
    private void getHealer(UHCPlayer p) {
        ItemStack axe = new ItemStack(XMaterial.STONE_AXE.parseMaterial());
        ItemStack pickaxe = new ItemStack(XMaterial.STONE_PICKAXE.parseMaterial());
        ItemStack food = new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 16);
        ItemStack gcarrot = new ItemStack(XMaterial.GOLDEN_CARROT.parseMaterial(), 12);
        ItemStack gapple = new ItemStack(XMaterial.GOLDEN_APPLE.parseMaterial(), 3);

        ItemUtils.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtils.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        p.getPlayer().getInventory().addItem(axe);
        p.getPlayer().getInventory().addItem(pickaxe);
        p.getPlayer().getInventory().addItem(food);
        p.getPlayer().getInventory().addItem(gcarrot);
        p.getPlayer().getInventory().addItem(gapple);
    }
    private void getHorseRider(UHCPlayer p) {
        ItemStack sword = new ItemStack(XMaterial.IRON_SWORD.parseMaterial());
        ItemStack pickaxe = new ItemStack(XMaterial.STONE_PICKAXE.parseMaterial());
        ItemStack axe = new ItemStack(XMaterial.STONE_AXE.parseMaterial());
        ItemStack food = new ItemStack(XMaterial.COOKED_BEEF.parseMaterial(), 16);
        ItemStack horse = ItemUtils.monsterEgg(EntityType.HORSE);
        ItemStack saddle = new ItemStack(XMaterial.SADDLE.parseMaterial());
        ItemStack horse_armor = new ItemStack(XMaterial.IRON_HORSE_ARMOR.parseMaterial());

        ItemUtils.addEnchant(axe, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(axe, Enchantment.DURABILITY, 3, false);

        ItemUtils.addEnchant(pickaxe, Enchantment.DIG_SPEED, 3, false);
        ItemUtils.addEnchant(pickaxe, Enchantment.DURABILITY, 3, false);

        p.getPlayer().getInventory().addItem(sword);
        p.getPlayer().getInventory().addItem(pickaxe);
        p.getPlayer().getInventory().addItem(axe);
        p.getPlayer().getInventory().addItem(food);
        p.getPlayer().getInventory().addItem(horse);
        p.getPlayer().getInventory().addItem(saddle);
        p.getPlayer().getInventory().addItem(horse_armor);
    }
}