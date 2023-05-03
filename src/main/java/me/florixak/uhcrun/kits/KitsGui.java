package me.florixak.uhcrun.kits;

import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.gui.Gui;
import me.florixak.uhcrun.utils.ItemUtils;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
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

        for (int i = 0; i < kits.size(); i++) {
            Kit kit = kits.get(i);
            List<String> lore = new ArrayList<>();
            lore.add(TextUtils.color(GameManager.getGameManager().getKitsManager().getKitCost(kit.getName())));
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

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() != getInventory()) return;
        event.setCancelled(true);
    }

    @Override
    public void openInv(Player p) {
        if (!GameManager.getGameManager().areKitsEnabled()) {
            p.sendMessage("Kits are disabled!");
            return;
        }
        super.openInv(p);
    }
}
