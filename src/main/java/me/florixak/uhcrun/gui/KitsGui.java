package me.florixak.uhcrun.gui;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.game.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XSeries.XEnchantment;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitsGui extends Gui {
    private final List<Kit> kitsList;

    public KitsGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 3 * GameValues.COLUMNS, TextUtils.color(GameValues.INVENTORY.KITS_TITLE));
        this.kitsList = gameManager.getKitsManager().getKitsList();
    }

    @Override
    public void init() {
        super.init();
        ItemStack kitDisplayItem;

        for (int i = 0; i < kitsList.size(); i++) {
            Kit kit = kitsList.get(i);
            List<String> lore = new ArrayList<>();

            if (uhcPlayer.hasKit() && uhcPlayer.getKit().equals(kit)) {
                lore.add(Messages.KITS_INV_SELECTED.toString());
            } else {
                if (!GameValues.KITS.BOUGHT_FOREVER) {
                    lore.add(kit.getFormattedCost());
                } else {
                    if (uhcPlayer.getData().hasKitBought(kit) || kit.isFree()) {
                        lore.add(Messages.KITS_INV_CLICK_TO_SELECT.toString());
                    } else {
                        lore.add(kit.getFormattedCost());
                    }
                }
            }

            for (ItemStack item : kit.getItems()) {
                if (!item.getEnchantments().keySet().isEmpty()) {
                    List<Enchantment> enchantmentsList = ItemUtils.getEnchantments(item);
                    StringBuilder enchants = new StringBuilder();
                    for (int j = 0; j < enchantmentsList.size(); j++) {
                        String enchantment = XEnchantment.matchXEnchantment(enchantmentsList.get(j)).name();
                        enchants.append(TextUtils.toNormalCamelText(enchantment) + " " + item.getEnchantments().get(enchantmentsList.get(j)));
                        if (j < enchantmentsList.size() - 1) enchants.append(", ");
                    }
                    lore.add(TextUtils.color("&7" + item.getAmount() + "x " + TextUtils.toNormalCamelText(item.getType().toString()) + " [" + enchants.toString() + "]"));
                } else {
                    lore.add(TextUtils.color("&7" + item.getAmount() + "x " + TextUtils.toNormalCamelText(item.getType().toString())));
                }
            }
            kitDisplayItem = ItemUtils.createItem(kit.getDisplayItem().getType(), kit.getDisplayName(), 1, lore);

            getInventory().setItem(i, kitDisplayItem);
        }
    }

    @Override
    public void open() {
        if (!GameValues.KITS.ENABLED) {
            uhcPlayer.sendMessage(Messages.KITS_DISABLED.toString());
            return;
        }
        super.open();
    }
}
