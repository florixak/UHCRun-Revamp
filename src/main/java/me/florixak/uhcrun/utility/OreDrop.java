package me.florixak.uhcrun.utility;

import me.florixak.uhcrun.UHCRun;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.bukkit.plugin.java.JavaPlugin;


public class OreDrop {

    private final ItemStack ITEM_STACK;

    private static final UHCRun PLUGIN = JavaPlugin.getPlugin(UHCRun.class);

    public OreDrop(ItemStack item) {
        this.ITEM_STACK = item;
    }

    public static OreDrop getItemStack(ConfigurationSection section, Player player) {
        ItemStack item = XMaterial.matchXMaterial(section.getString("material").toUpperCase()).get().parseItem();

//        if (item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
//            if (section.contains("base64")) {
//                item = ((HeadHook) PLUGIN.getHookManager().getPluginHook("BASE64")).getHead(section.getString("base64"));
//            } else if (section.contains("hdb") && PLUGIN.getHookManager().isHookEnabled("HEAD_DATABASE")) {
//                item = ((HeadHook) PLUGIN.getHookManager().getPluginHook("HEAD_DATABASE")).getHead(section.getString("hdb"));
//            }
//        }

        OreDrop builder = new OreDrop(item);

        if (section.contains("amount")) {
            builder.withAmount(section.getInt("amount"));
        }

        if (section.contains("drops")) {

        }


        return builder;
    }

    public static OreDrop getItemStack(ConfigurationSection section) {
        return getItemStack(section, null);
    }

    public OreDrop withAmount(int amount) {
        ITEM_STACK.setAmount(amount);
        return this;
    }

    public OreDrop getDrops(ConfigurationSection section) {
        int size = section.getKeys(false).size();
        for (int i = 0; i < size; i++) {
            ItemStack item = XMaterial.matchXMaterial(section.getString("material").toUpperCase()).get().parseItem();




        }





        return this;
    }


    public ItemStack build() {
        return ITEM_STACK;
    }
}
