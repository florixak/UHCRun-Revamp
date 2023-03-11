package me.florixak.uhcrun.action.actions;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.Action;
import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.inventory.InventoryListener;
import me.florixak.uhcrun.manager.KitsManager;
import me.florixak.uhcrun.utils.TextUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class KitsAction implements Action {


    @Override
    public String getIdentifier() {
        return "KIT";
    }

    @Override
    public void execute(UHCRun plugin, Player player, String data) {

        FileConfiguration config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        FileConfiguration statistics = plugin.getConfigManager().getFile(ConfigType.STATISTICS).getConfig();
        FileConfiguration kits = plugin.getConfigManager().getFile(ConfigType.KITS).getConfig();
        Economy economy = UHCRun.getEconomy();

        if (data.equals("none")) {
            if (KitsManager.haveNoKit(player)) return;

            KitsManager.noKit.add(player.getUniqueId());
            KitsManager.healer.remove(player.getUniqueId());
            KitsManager.horse_rider.remove(player.getUniqueId());
            KitsManager.enchanter.remove(player.getUniqueId());
            KitsManager.starter.remove(player.getUniqueId());
            KitsManager.miner.remove(player.getUniqueId());
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("starter")) {

            if (KitsManager.haveStarter(player)) return;

            if (plugin.getStatisticManager().haveStarter(player.getUniqueId()) == false) {
                int amount = kits.getInt("items.starter.price");
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

            if (config.getBoolean("lobby-items.kits.always-buy") == false) {
                plugin.getStatisticManager().addStarter(player.getUniqueId());
            }

            KitsManager.starter.add(player.getUniqueId());
            KitsManager.healer.remove(player.getUniqueId());
            KitsManager.horse_rider.remove(player.getUniqueId());
            KitsManager.enchanter.remove(player.getUniqueId());
            KitsManager.miner.remove(player.getUniqueId());
            KitsManager.noKit.remove(player.getUniqueId());
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }
        if (data.equals("miner")) {

            if (KitsManager.haveMiner(player)) return;

            if (plugin.getStatisticManager().haveMiner(player.getUniqueId()) == false) {
                int amount = kits.getInt("items.miner.price");
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
                        statistics.set("statistics." + player.getUniqueId() + ".money", plugin.getStatisticManager().getMoney(player.getUniqueId()) - amount);
                        plugin.getConfigManager().getFile(ConfigType.STATISTICS).save();
                    }
                }
            }

            if (config.getBoolean("lobby-items.kits.always-buy") == false) {
                plugin.getStatisticManager().addMiner(player.getUniqueId());
            }

            KitsManager.miner.add(player.getUniqueId());
            KitsManager.healer.remove(player.getUniqueId());
            KitsManager.horse_rider.remove(player.getUniqueId());
            KitsManager.enchanter.remove(player.getUniqueId());
            KitsManager.starter.remove(player.getUniqueId());
            KitsManager.noKit.remove(player.getUniqueId());
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("enchanter")) {

            if (KitsManager.haveEnchanter(player)) return;

            if (plugin.getStatisticManager().haveEnchanter(player.getUniqueId()) == false) {
                int amount = kits.getInt("items.enchanter.price");
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

            if (config.getBoolean("lobby-items.kits.always-buy") == false) {
                plugin.getStatisticManager().addEnchanter(player.getUniqueId());
            }

            KitsManager.enchanter.add(player.getUniqueId());
            KitsManager.healer.remove(player.getUniqueId());
            KitsManager.horse_rider.remove(player.getUniqueId());
            KitsManager.starter.remove(player.getUniqueId());
            KitsManager.miner.remove(player.getUniqueId());
            KitsManager.noKit.remove(player.getUniqueId());
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("healer")) {

            if (KitsManager.haveHealer(player)) return;

            if (plugin.getStatisticManager().haveHealer(player.getUniqueId()) == false) {
                int amount = kits.getInt("items.healer.price");
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

            if (config.getBoolean("lobby-items.kits.always-buy") == false) {
                plugin.getStatisticManager().addHealer(player.getUniqueId());
            }

            KitsManager.healer.add(player.getUniqueId());
            KitsManager.horse_rider.remove(player.getUniqueId());
            KitsManager.enchanter.remove(player.getUniqueId());
            KitsManager.starter.remove(player.getUniqueId());
            KitsManager.miner.remove(player.getUniqueId());
            KitsManager.noKit.remove(player.getUniqueId());
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }

        if (data.equals("horse_rider")) {

            if (KitsManager.haveHorseRider(player)) return;

            if (plugin.getStatisticManager().haveHorseRider(player.getUniqueId()) == false) {
                int amount = kits.getInt("items.horse_rider.price");
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

            if (config.getBoolean("lobby-items.kits.always-buy") == false) {
                plugin.getStatisticManager().addHorseRider(player.getUniqueId());
            }

            KitsManager.horse_rider.add(player.getUniqueId());
            KitsManager.healer.remove(player.getUniqueId());
            KitsManager.enchanter.remove(player.getUniqueId());
            KitsManager.starter.remove(player.getUniqueId());
            KitsManager.miner.remove(player.getUniqueId());
            KitsManager.noKit.remove(player.getUniqueId());
            player.sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", TextUtils.color(InventoryListener.itemStack.getItemMeta().getDisplayName() + "&f")));
        }
    }
}