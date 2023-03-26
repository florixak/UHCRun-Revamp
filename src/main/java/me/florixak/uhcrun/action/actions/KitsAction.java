package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.inventory.InventoryListener;
import me.florixak.uhcrun.kits.Kits;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.entity.Player;

public class KitsAction implements Action {

    @Override
    public String getIdentifier() {
        return "KIT";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {

        UHCPlayer uhcPlayer = plugin.getPlayerManager().getUHCPlayer(player.getUniqueId());

        if (data.equals("none")) {
            if (uhcPlayer.getKit() == Kits.NONE) return;

            uhcPlayer.setKit(Kits.NONE);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("starter")) {

            if (uhcPlayer.getKit() == Kits.STARTER) return;

            uhcPlayer.setKit(Kits.STARTER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }
        if (data.equals("miner")) {

            if (uhcPlayer.getKit() == Kits.MINER) return;

            uhcPlayer.setKit(Kits.MINER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("enchanter")) {

            if (uhcPlayer.getKit() == Kits.ENCHANTER) return;

            uhcPlayer.setKit(Kits.ENCHANTER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("healer")) {

            if (uhcPlayer.getKit() == Kits.HEALER) return;

            uhcPlayer.setKit(Kits.HEALER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("horse_rider")) {

            if (uhcPlayer.getKit() == Kits.HORSE_RIDER) return;

            uhcPlayer.setKit(Kits.HORSE_RIDER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }
    }
}