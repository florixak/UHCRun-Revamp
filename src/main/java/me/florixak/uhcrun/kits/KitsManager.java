package me.florixak.uhcrun.kits;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
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
        ItemStack item;
        for (String selector : config.getConfigurationSection("settings.selectors").getKeys(false)) {
            if (config.getBoolean("settings.selectors." + selector + ".enabled", false)) return;
            item = XMaterial.matchXMaterial(config.getConfigurationSection("settings.selectors." + selector)
                    .getString("material").toUpperCase()).get().parseItem();
            p.getPlayer().getInventory().setItem(config.getInt("settings.selectors." + selector + ".slot"),
                    ItemUtils.getItem(item, config.getString("settings.selectors." + selector + ".display-name"), 1));
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