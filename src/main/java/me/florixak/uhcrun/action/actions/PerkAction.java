package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.inventory.InventoryListener;
import me.florixak.uhcrun.perks.Perks;
import me.florixak.uhcrun.perks.PerksManager;
import me.florixak.uhcrun.utils.TextUtils;
import net.milkbowl.vault.economy.Economy;
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
        FileConfiguration statistics = plugin.getConfigManager().getFile(ConfigType.PLAYER_DATA).getConfig();
        FileConfiguration perks = plugin.getConfigManager().getFile(ConfigType.PERKS).getConfig();
        Economy economy = UHCRun.getVault();

        if (data.equals("none")) {

            if (PerksManager.getPerk(player.getUniqueId()) == Perks.NONE) return;

            PerksManager.registerPerk(player, Perks.NONE);
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

/*
        if (data.equals("strength")) {

            if (PerksManager.getPerk(player.getUniqueId()) == Perks.STRENGTH) return;

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
            PerksManager.registerPerk(player, Perks.STRENGTH);
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("regeneration")) {

            if (PerksManager.getPerk(player.getUniqueId()) == Perks.REGENERATION) return;

            if (plugin.getStatisticManager().haveRegeneration(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.regeneration.price");
                MoneyUtils.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addRegeneration(player.getUniqueId());
            }

            PerksManager.registerPerk(player, Perks.REGENERATION);
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("speed")) {

            if (PerksManager.getPerk(player.getUniqueId()) == Perks.SPEED) return;

            if (plugin.getStatisticManager().haveSpeed(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.speed.price");
                MoneyUtils.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addSpeed(player.getUniqueId());
            }

            PerksManager.registerPerk(player, Perks.SPEED);
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("invisible")) {

            if (PerksManager.getPerk(player.getUniqueId()) == Perks.INVISIBLE) return;

            if (plugin.getStatisticManager().haveInvisible(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.invisible.price");
                MoneyUtils.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addInvisible(player.getUniqueId());
            }

            PerksManager.registerPerk(player, Perks.INVISIBLE);
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("fire_resistance")) {

            if (PerksManager.getPerk(player.getUniqueId()) == Perks.FIRE_RESISTANCE) return;

            if (plugin.getStatisticManager().haveFireResistance(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.fire_resistance.price");
                MoneyUtils.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addFireResistance(player.getUniqueId());
            }

            PerksManager.registerPerk(player, Perks.FIRE_RESISTANCE);
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("ender_pearl")) {

            if (PerksManager.getPerk(player.getUniqueId()) == Perks.ENDER_PEARL) return;

            if (plugin.getStatisticManager().haveEnderPearl(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.ender_pearl.price");
                MoneyUtils.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addEnderPearl(player.getUniqueId());
            }

            PerksManager.registerPerk(player, Perks.ENDER_PEARL);
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("resistance")) {

            if (PerksManager.getPerk(player.getUniqueId()) == Perks.RESISTANCE) return;

            if (plugin.getStatisticManager().haveResistance(player.getUniqueId()) == false) {
                int amount = perks.getInt("items.resistance.price");
                MoneyUtils.takeMoney(player, amount);
            }

            if (config.getBoolean("lobby-items.perks.always-buy") == false) {
                plugin.getStatisticManager().addResistance(player.getUniqueId());
            }

            PerksManager.registerPerk(player, Perks.RESISTANCE);
            player.sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

 */
    }

}
