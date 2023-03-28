package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.inventory.InventoryListener;
import me.florixak.uhcrun.kits.KitType;
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
            if (uhcPlayer.getKit() == KitType.NONE) return;

            uhcPlayer.setKit(KitType.NONE);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("starter")) {

            if (uhcPlayer.getKit() == KitType.STARTER) return;

            uhcPlayer.setKit(KitType.STARTER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }
        if (data.equals("miner")) {

            if (uhcPlayer.getKit() == KitType.MINER) return;

            uhcPlayer.setKit(KitType.MINER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("enchanter")) {

            if (uhcPlayer.getKit() == KitType.ENCHANTER) return;

            uhcPlayer.setKit(KitType.ENCHANTER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("healer")) {

            if (uhcPlayer.getKit() == KitType.HEALER) return;

            uhcPlayer.setKit(KitType.HEALER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("horse_rider")) {

            if (uhcPlayer.getKit() == KitType.HORSE_RIDER) return;

            uhcPlayer.setKit(KitType.HORSE_RIDER);
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }
    }
}