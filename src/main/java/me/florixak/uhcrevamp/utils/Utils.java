package me.florixak.uhcrevamp.utils;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class Utils {

	public static void broadcast(final String msg) {
		Bukkit.broadcastMessage(TextUtils.color(msg));
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getPlayerHead(final Player player, final String playerName) {
		final Material type = Material.matchMaterial(!UHCRevamp.useOldMethods ? "PLAYER_HEAD" : "SKULL_ITEM");
		final ItemStack item = new ItemStack(type, 1);

		if (UHCRevamp.useOldMethods) item.setDurability((short) 3);

		final SkullMeta meta = (SkullMeta) item.getItemMeta();
		if (playerName != null) meta.setDisplayName(TextUtils.color(playerName));
		if (UHCRevamp.useOldMethods) meta.setOwner(player.getName());
		else meta.setOwningPlayer(player);

		item.setItemMeta(meta);
		return item;
	}

	public static void skullTeleport(final Player p, final ItemStack item) {
		if (item.getType() != Material.AIR && item.getType() != null) {
			final SkullMeta meta = (SkullMeta) item.getItemMeta();
			if (meta.getDisplayName() != null) {
				if (Bukkit.getPlayer(meta.getDisplayName()) != null) {
					final Player player = Bukkit.getPlayer(meta.getDisplayName());
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

	public static HashMap<String, Integer> sortByValue(final HashMap<String, Integer> hm) {
		// Create a list from elements of HashMap
		final List<Map.Entry<String, Integer>> list =
				new LinkedList<>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(final Map.Entry<String, Integer> o1,
							   final Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to hashmap
		final HashMap<String, Integer> temp = new LinkedHashMap<>();
		for (final Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	public static <K, V> K getKeyByValue(final Map<K, V> map, final V value) {
		for (final Map.Entry<K, V> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

}