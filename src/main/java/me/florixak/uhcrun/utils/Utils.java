package me.florixak.uhcrun.utils;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.XSeries.XPotion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    private GameManager gameManager;
    private FileConfiguration config;

    public static DecimalFormat format = new DecimalFormat("##,###,##0.00");

    public Utils(GameManager gameManager) {
        this.gameManager = gameManager;
        this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
    }

    public void clearDrops() {
        List<Entity> entList = gameManager.getGameWorld().getEntities();

        for(Entity current : entList) {
            if (current instanceof Item) {
                current.remove();
            }
        }
    }

    public static void sendHotBarMessage(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }

        // Call the event, if cancelled don't send Action Bar
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + UHCRun.nmsver + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + UHCRun.nmsver + ".PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server." + UHCRun.nmsver + ".Packet");
            if (UHCRun.useOldMethods) {
                Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + UHCRun.nmsver + ".ChatSerializer");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + UHCRun.nmsver + ".IChatBaseComponent");
                Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + TextUtils.color(message) + "\"}"));
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + UHCRun.nmsver + ".ChatComponentText");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + UHCRun.nmsver + ".IChatBaseComponent");
                try {
                    Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + UHCRun.nmsver + ".ChatMessageType");
                    Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                    Object chatMessageType = null;
                    for (Object obj : chatMessageTypes) {
                        if (obj.toString().equals("GAME_INFO")) {
                            chatMessageType = obj;
                        }
                    }
                    Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(TextUtils.color(message));
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatCompontentText, chatMessageType);
                } catch (ClassNotFoundException cnfe) {
                    Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(TextUtils.color(message));
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatCompontentText, (byte) 2);
                }
            }
            Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public ItemStack getPlayerHead(Player player, String name) {
        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");

        Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
        ItemStack item = new ItemStack(type, 1);

        if (!isNewVersion) item.setDurability((short) 3);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (name != null) meta.setDisplayName(TextUtils.color(name));
        meta.setOwner(player.getName());

        item.setItemMeta(meta);
        return item;
    }
    public static void skullTeleport(Player p, ItemStack item) {
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