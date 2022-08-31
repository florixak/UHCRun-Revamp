package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.inventory.InventoryListener;
import me.florixak.uhcrun.manager.PerksManager;
import me.florixak.uhcrun.utility.MoneyUtil;
import me.florixak.uhcrun.utility.TextUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PerkAction implements Action {

    @Override
    public String getIdentifier() {
        return "PERK";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {

        FileConfiguration config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        FileConfiguration statistics = plugin.getConfigManager().getFile(ConfigType.STATISTICS).getConfig();
        FileConfiguration perks = plugin.getConfigManager().getFile(ConfigType.PERKS).getConfig();
        Economy economy = UHCRun.getEconomy();

        if (data.equals("none")) {

            if (PerksManager.haveNoPerk(player)) return;

            PerksManager.disbandPerks(player);
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtil.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }


        if (data.equals("strength")) {

            if (PerksManager.haveStrength(player)) return;

            if (plugin.getStatisticManager().haveStrength(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.strength.price");
                if (config.getBoolean("use-Vault", true)) {
                    EconomyResponse r = economy.withdrawPlayer(player, amount);
                    if (!r.transactionSuccess()) {
                        player.sendMessage(Messages.NO_MONEY.toString());
                        return;
                    }
                    statistics.set("statistics." + player.getUniqueId() + ".money", economy.getBalance(player));
                    plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
                } else if (config.getBoolean("MySQL.enabled", true)) {
                    if (plugin.data.getMoney(player.getUniqueId()) < amount) {
                        player.sendMessage(Messages.NO_MONEY.toString());
                        return;
                    } else {
                        plugin.data.addMoney(player.getUniqueId(), -amount);
                    }
                } else {
                    if (statistics.getInt("statistics." + player.getUniqueId() + ".money") < amount) {
                        player.sendMessage(Messages.NO_MONEY.toString());
                        return;
                    } else {
                        statistics.set("statistics." + player.getUniqueId() + ".money", plugin.getStatisticManager().getMoney(player.getUniqueId())-amount);
                        plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
                    }
                }
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addStrength(player.getUniqueId());
            }

            PerksManager.strength.add(player.getUniqueId());
            PerksManager.regeneration.remove(player.getUniqueId());
            PerksManager.speed.remove(player.getUniqueId());
            PerksManager.noPerk.remove(player.getUniqueId());
            PerksManager.ender_pearl.remove(player.getUniqueId());
            PerksManager.fire_resistance.remove(player.getUniqueId());
            PerksManager.invisible.remove(player.getUniqueId());
            PerksManager.resistance.remove(player.getUniqueId());
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtil.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("regeneration")) {

            if (PerksManager.haveRegeneration(player)) return;

            if (plugin.getStatisticManager().haveRegeneration(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.regeneration.price");
                MoneyUtil.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addRegeneration(player.getUniqueId());
            }

            PerksManager.disbandPerks(player);
            PerksManager.regeneration.add(player.getUniqueId());
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtil.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("speed")) {

            if (PerksManager.haveSpeed(player)) return;

            if (plugin.getStatisticManager().haveSpeed(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.speed.price");
                MoneyUtil.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addSpeed(player.getUniqueId());
            }

            PerksManager.speed.add(player.getUniqueId());
            PerksManager.strength.remove(player.getUniqueId());
            PerksManager.regeneration.remove(player.getUniqueId());
            PerksManager.noPerk.remove(player.getUniqueId());
            PerksManager.ender_pearl.remove(player.getUniqueId());
            PerksManager.fire_resistance.remove(player.getUniqueId());
            PerksManager.invisible.remove(player.getUniqueId());
            PerksManager.resistance.remove(player.getUniqueId());
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtil.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("invisible")) {

            if (PerksManager.haveInvisible(player)) return;

            if (plugin.getStatisticManager().haveInvisible(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.invisible.price");
                MoneyUtil.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addInvisible(player.getUniqueId());
            }

            PerksManager.disbandPerks(player);
            PerksManager.invisible.add(player.getUniqueId());
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtil.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("fire_resistance")) {

            if (PerksManager.haveFireResistance(player)) return;

            if (plugin.getStatisticManager().haveFireResistance(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.fire_resistance.price");
                MoneyUtil.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addFireResistance(player.getUniqueId());
            }

            PerksManager.disbandPerks(player);
            PerksManager.fire_resistance.add(player.getUniqueId());
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtil.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("ender_pearl")) {

            if (PerksManager.haveEnderPearl(player)) return;

            if (plugin.getStatisticManager().haveEnderPearl(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.ender_pearl.price");
                MoneyUtil.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addEnderPearl(player.getUniqueId());
            }

            PerksManager.disbandPerks(player);
            PerksManager.ender_pearl.add(player.getUniqueId());
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtil.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("resistance")) {

            if (PerksManager.haveResistance(player)) return;

            if (plugin.getStatisticManager().haveResistance(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.resistance.price");
                MoneyUtil.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addResistance(player.getUniqueId());
            }

            PerksManager.disbandPerks(player);
            PerksManager.resistance.add(player.getUniqueId());
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtil.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }
    }
}
