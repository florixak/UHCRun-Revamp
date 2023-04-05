package me.florixak.uhcrun.utils;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.XSeries.XPotion;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    private UHCRun plugin;
    private FileConfiguration config;

    public static DecimalFormat format = new DecimalFormat("##,###,##0.00");

    public Utils(UHCRun plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    public static String formatMoney(UUID uuid){
        return format.format(UHCRun.getInstance().getStatistics().getMoney(uuid));
    }

    public static String getLuckPermsPrefix(Player player) {
        String prefix = LuckPermsProvider.get().getUserManager().getUser(player.getName())
                .getCachedData().getMetaData().getPrefix();
        return prefix;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getPlayerHead(String player, String name) {
        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");

        Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
        ItemStack item = new ItemStack(type, 1);

        if (!isNewVersion) item.setDurability((short) 3);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (name != null) meta.setDisplayName(TextUtils.color(name));
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

    public static void broadcast(String msg) {
        Bukkit.broadcastMessage(TextUtils.color(msg));
    }

    public void addPotion(Player p, XPotion potion, int duration, int power) {
        p.addPotionEffect(potion.buildPotionEffect(duration, power));
    }

    public static void dropItem(UHCPlayer p, BlockBreakEvent event) {
        FileConfiguration config = UHCRun.getInstance().getConfigManager().getFile(ConfigType.CUSTOM_DROPS).getConfig();
        Location loc = event.getBlock().getLocation();

        ItemStack drop;
        Material drop_item;
        Random ran = new Random();
        int size;
        int xp;
        int amount;

        for (String block : config.getConfigurationSection("custom-drops").getKeys(false)) {
            Material material = XMaterial.matchXMaterial(config.getConfigurationSection("custom-drops." + block).getString("material").toUpperCase()).get().parseMaterial();
            if (event.getBlock().getType() == material) {

                event.setDropItems(false);
                event.setExpToDrop(0);

                size = config.getStringList("custom-drops." + block + ".drops").size();

                amount = config.getInt("custom-drops." + block + ".max-drop");
                drop_item = XMaterial.matchXMaterial(config.getConfigurationSection("custom-drops." + block).getStringList("drops").get(ran.nextInt(size)).toUpperCase()).get().parseMaterial();
                if (drop_item != null && amount != 0) {
                    drop = new ItemStack(drop_item, ran.nextInt(amount)+1);
                    loc.getWorld().dropItemNaturally(loc, drop);
                }
                xp = config.getInt("custom-drops." + block + ".xp");
                p.getPlayer().giveExp(xp);
                if (xp > 0) {
                    UHCRun.getInstance().getSoundManager().playOreDestroySound(p.getPlayer());
                }

                break;
            }
        }
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


}