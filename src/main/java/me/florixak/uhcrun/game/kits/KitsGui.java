package me.florixak.uhcrun.game.kits;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.gui.Gui;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.XSeries.XMaterial;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitsGui extends Gui {

    private final List<Kit> kits;

    public KitsGui(GameManager gameManager, UHCPlayer uhcPlayer) {
        super(gameManager, uhcPlayer, 9 * 3, TextUtils.color(GameValues.INV_KITS_TITLE));
        this.kits = gameManager.getKitsManager().getKits();
    }

    @Override
    public void init() {
        super.init();
        ItemStack kitDisplayItem;

        for (int i = 0; i < kits.size(); i++) {
            Kit kit = kits.get(i);
            List<String> lore = new ArrayList<>();

            if (uhcPlayer.hasKit() && uhcPlayer.getKit().equals(kit)) {
                lore.add(TextUtils.color("&aSelected"));
            } else {
                lore.add(TextUtils.color(gameManager.getKitsManager().getKitCost(kit.getName())));
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
            kitDisplayItem = this.createItem(XMaterial.matchXMaterial(kit.getDisplayItem()), kit.getName(), lore);

            getInventory().setItem(i, kitDisplayItem);
        }
    }

    @Override
    public void open() {
        if (!GameValues.KITS_ENABLED) {
            uhcPlayer.sendMessage(Messages.KITS_DISABLED.toString());
            return;
        }
        super.open();
    }
}
