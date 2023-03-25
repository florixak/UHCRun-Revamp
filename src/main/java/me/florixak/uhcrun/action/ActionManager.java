package me.florixak.uhcrun.action;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.action.actions.*;
import me.florixak.uhcrun.utils.placeholderapi.PlaceholderUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionManager {

    private UHCRun plugin;
    private Map<String, Action> actions;

    public static ItemStack item;

    public ActionManager(UHCRun plugin) {
        this.plugin = plugin;
        actions = new HashMap<>();
        load();
    }

    private void load() {
        registerAction(
                new TitleAction(),
//                new CommandAction(),
//                new ConsoleCommandAction(),
//                new SoundAction(),
//                new PotionEffectAction(),
                new BungeeAction(),
                new CloseInventoryAction(),
                new MenuAction(),
                new KitsAction(),
                new PerkAction(),
                new WorkbenchAction(),
                new VoteAction()
        );
    }

    public void registerAction(Action... actions) {
        Arrays.asList(actions).forEach(action -> this.actions.put(action.getIdentifier(), action));
    }

    public void executeActions(Player player, List<String> items) {
        items.forEach(item -> {

            String actionName = StringUtils.substringBetween(item, "[", "]").toUpperCase();
            Action action = actionName.isEmpty() ? null : actions.get(actionName);

            if (action != null) {
                item = item.contains(" ") ? item.split(" ", 2)[1] : "";
                item = PlaceholderUtil.setPlaceholders(item, player);

                action.execute(plugin, player, item);
            }
        });
    }
}