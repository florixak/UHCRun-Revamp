package me.florixak.uhcrun.game.kits;

import me.florixak.uhcrun.game.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.manager.gui.Gui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitsGui extends Gui {

    public KitsGui() {
        super(27, "Kits");
    }

    @Override
    public void init() {
        super.init();
        ItemStack kit_item;
        List<Kit> kits = GameManager.getGameManager().getKitsManager().getKits();
        Player p = getWhoOpen();
        UHCPlayer uhcPlayer = GameManager.getGameManager().getPlayerManager().getUHCPlayer(p.getUniqueId());

        for (int i = 0; i < kits.size(); i++) {
            Kit kit = kits.get(i);
            List<String> lore = new ArrayList<>();

            if (uhcPlayer.hasKit() && uhcPlayer.getKit().equals(kit)) {
                lore.add(TextUtils.color("&aSelected"));
            } else {
                lore.add(TextUtils.color(GameManager.getGameManager().getKitsManager().getKitCost(kit.getName())));
            }

            for (ItemStack item : kit.getItems()) {
                if (!item.getEnchantments().keySet().isEmpty()) {
                    List<Enchantment> enchantmentsList = ItemUtils.getEnchantments(item);
                    StringBuilder enchants = new StringBuilder();
                    for (int j = 0; j < enchantmentsList.size(); j++) {
                        enchants.append(TextUtils.toNormalCamelText(enchantmentsList.get(j).getName()) + " " + item.getEnchantments().get(enchantmentsList.get(j)));
                        if (j < enchantmentsList.size()-1) enchants.append(", ");
                    }
                    lore.add(TextUtils.color("&7" + item.getAmount() + "x " + TextUtils.toNormalCamelText(item.getType().toString()) + " [" + enchants.toString() + "]"));
                } else {
                    lore.add(TextUtils.color("&7" + item.getAmount() + "x " + TextUtils.toNormalCamelText(item.getType().toString())));
                }
            }
            kit_item = this.createItem(kit.getDisplayItem(), kit.getName(), lore);

            getInventory().setItem(i, kit_item);
        }
    }

    @Override
    public void openInv(Player p) {
        if (!GameManager.getGameManager().areKitsEnabled()) {
            p.sendMessage(Messages.KITS_DISABLED.toString());
            return;
        }
        super.openInv(p);
    }
}
