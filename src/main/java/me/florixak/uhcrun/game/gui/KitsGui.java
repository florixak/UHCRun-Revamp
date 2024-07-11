package me.florixak.uhcrun.game.gui;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitsGui extends Gui {
    private final List<Kit> kits;

    public KitsGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 3 * GameValues.COLUMNS, TextUtils.color(GameValues.INVENTORY.KITS_TITLE));
        this.kits = gameManager.getKitsManager().getKitsList();
    }

    @Override
    public void init() {
        super.init();
        ItemStack kitDisplayItem;

        for (int i = 0; i < kits.size(); i++) {
            Kit kit = kits.get(i);
            List<String> lore = new ArrayList<>();

            if (uhcPlayer.hasKit() && uhcPlayer.getKit().equals(kit)) {
                lore.add(Messages.KITS_INV_SELECTED.toString());
            } else {
                if (!GameValues.KITS.BOUGHT_FOREVER) {
                    lore.add(TextUtils.color(gameManager.getKitsManager().getKitCost(kit.getName())));
                } else {
                    if (uhcPlayer.getData().alreadyBoughtKit(kit)) {
                        lore.add(Messages.KITS_INV_CLICK_TO_SELECT.toString());
                    } else {
                        lore.add(TextUtils.color(gameManager.getKitsManager().getKitCost(kit.getName())));
                    }
                }
            }

            for (ItemStack item : kit.getItems()) {
                if (!item.getEnchantments().keySet().isEmpty()) {
                    List<Enchantment> enchantmentsList = ItemUtils.getEnchantments(item);
                    StringBuilder enchants = new StringBuilder();
                    for (int j = 0; j < enchantmentsList.size(); j++) {
                        enchants.append(TextUtils.toNormalCamelText(enchantmentsList.get(j).getName()) + " " + item.getEnchantments().get(enchantmentsList.get(j)));
                        if (j < enchantmentsList.size() - 1) enchants.append(", ");
                    }
                    lore.add(TextUtils.color("&7" + item.getAmount() + "x " + TextUtils.toNormalCamelText(item.getType().toString()) + " [" + enchants.toString() + "]"));
                } else {
                    lore.add(TextUtils.color("&7" + item.getAmount() + "x " + TextUtils.toNormalCamelText(item.getType().toString())));
                }
            }
            kitDisplayItem = this.createItem(kit.getDisplayItem(), kit.getDisplayName(), lore);

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
