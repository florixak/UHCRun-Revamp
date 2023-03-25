package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.inventory.InventoryListener;
import me.florixak.uhcrun.kits.Kits;
import me.florixak.uhcrun.kits.KitsManager;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class KitsAction implements Action {


    @Override
    public String getIdentifier() {
        return "KIT";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {

        FileConfiguration config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        FileConfiguration statistics = plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();

        if (data.equals("none")) {
            if (KitsManager.getKit(player.getUniqueId()) == Kits.NONE) return;

            KitsManager.kits.put(player.getUniqueId(), Kits.NONE);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("starter")) {

            if (KitsManager.getKit(player.getUniqueId()) == Kits.STARTER) return;

            KitsManager.kits.put(player.getUniqueId(), Kits.STARTER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }
        if (data.equals("miner")) {

            if (KitsManager.getKit(player.getUniqueId()) == Kits.MINER) return;

            KitsManager.kits.put(player.getUniqueId(), Kits.MINER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("enchanter")) {

            if (KitsManager.getKit(player.getUniqueId()) == Kits.ENCHANTER) return;

            KitsManager.kits.put(player.getUniqueId(), Kits.ENCHANTER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("healer")) {

            if (KitsManager.getKit(player.getUniqueId()) == Kits.HEALER) return;

            KitsManager.kits.put(player.getUniqueId(), Kits.HEALER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("horse_rider")) {

            if (KitsManager.getKit(player.getUniqueId()) == Kits.HORSE_RIDER) return;

            KitsManager.kits.put(player.getUniqueId(), Kits.HORSE_RIDER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }
    }
}