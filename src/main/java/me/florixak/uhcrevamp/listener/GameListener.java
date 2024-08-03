package me.florixak.uhcrevamp.listener;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.config.ConfigType;
import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameState;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.customDrop.CustomDrop;
import me.florixak.uhcrevamp.game.customRecipes.CustomRecipe;
import me.florixak.uhcrevamp.game.player.PlayerManager;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.listener.events.GameEndEvent;
import me.florixak.uhcrevamp.listener.events.GameKillEvent;
import me.florixak.uhcrevamp.utils.MathUtils;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.utils.placeholderapi.PlaceholderUtil;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GameListener implements Listener {

	private final GameManager gameManager;
	private final FileConfiguration config;
	private final PlayerManager playerManager;

	public GameListener(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.config = gameManager.getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
		this.playerManager = gameManager.getPlayerManager();
	}

	@EventHandler
	public void handleGameEnd(final GameEndEvent event) {

		final String winner = event.getWinner();
		final List<String> gameResults = Messages.GAME_RESULTS.toList();
		final List<UHCPlayer> topKillers = playerManager.getTopKillers();
		final List<String> commands = config.getStringList("settings.end-game-commands");

		// Game results and top killers
		if (!gameResults.isEmpty()) {
			for (String message : gameResults) {
				for (int i = 0; i < gameResults.size(); i++) {
					final UHCPlayer topKiller = i < topKillers.size() && topKillers.get(i) != null ? topKillers.get(i) : null;
					final boolean isUHCPlayer = topKiller != null;
					message = message.replace("%winner%", winner)
							.replace("%top-killer-" + (i + 1) + "%", isUHCPlayer ? topKiller.getName() : "None")
							.replace("%top-killer-" + (i + 1) + "-kills%", isUHCPlayer ? String.valueOf(topKiller.getKills()) : "0")
							.replace("%top-killer-" + (i + 1) + "-team%", isUHCPlayer && GameValues.TEAM.TEAM_MODE ? topKiller.getTeam() != null ? topKiller.getTeam().getDisplayName() : "" : "")
							.replace("%top-killer-" + (i + 1) + "-uhc-level%", isUHCPlayer ? String.valueOf(topKiller.getData().getUHCLevel()) : "0");
				}
				message = message.replace("%prefix%", Messages.PREFIX.toString());

				Utils.broadcast(TextUtils.color(message));
			}
		}

		// Statistics
		for (final UHCPlayer player : playerManager.getPlayers()) {

			player.getData().saveStatistics();
			if (player.getPlayer() == null) continue;
			player.clearInventory();
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(gameManager.getLobbyManager().getEndingLobbyLocation());

			player.getData().showStatistics();
			if (GameValues.TITLE.ENABLED) {
				final int fadeIn = GameValues.TITLE.FADE_IN * 20;
				final int stay = GameValues.TITLE.STAY * 20;
				final int fadeOut = GameValues.TITLE.FADE_OUT * 20;
				if (player.isWinner())
					UHCRevamp.getInstance().getVersionUtils().sendTitle(player.getPlayer(), Messages.TITLE_WIN.toString(), Messages.SUBTITLE_WIN.toString(), fadeIn, stay, fadeOut);
				else {
					UHCRevamp.getInstance().getVersionUtils().sendTitle(player.getPlayer(), Messages.TITLE_LOSE.toString(), Messages.SUBTITLE_LOSE.toString(), fadeIn, stay, fadeOut);
				}
			}
		}

		// End game commands
		if (!commands.isEmpty()) {
			for (final String command : commands) {
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
			}
		}
	}

	@EventHandler
	public void handleGameKill(final GameKillEvent event) {
		final UHCPlayer killer = event.getKiller();
		final UHCPlayer victim = event.getVictim();

		if (killer == null) {
			Utils.broadcast(PlaceholderUtil.setPlaceholders(Messages.DEATH.toString(), victim.getPlayer()));
			if (gameManager.getDamageTrackerManager().isInTracker(victim)) {
				final UHCPlayer attacker = gameManager.getDamageTrackerManager().getAttacker(victim);
				gameManager.getDamageTrackerManager().onDead(victim);
				attacker.kill(victim);
			}
			return;
		}

		if (gameManager.getDamageTrackerManager().isInTracker(victim)) {
			final UHCPlayer attacker = gameManager.getDamageTrackerManager().getAttacker(victim);
			final UHCPlayer assistant = gameManager.getDamageTrackerManager().getAssistant(victim);
			gameManager.getDamageTrackerManager().onDead(victim);

			if (assistant != null) {
				assistant.assist(victim);
			}
			attacker.kill(victim);
		}
		Utils.broadcast(Messages.KILL.toString()
				.replace("%player%", victim.getName())
				.replace("%killer%", killer.getName()));
	}

	@EventHandler
	public void handleBlockBreak(final BlockBreakEvent event) {
		final Player p = event.getPlayer();
		final UHCPlayer uhcPlayer = playerManager.getUHCPlayer(p.getUniqueId());
		final Block block = event.getBlock();

		if (!gameManager.isPlaying() || uhcPlayer.isDead() || gameManager.getGameState().equals(GameState.ENDING)) {
			event.setCancelled(true);
			uhcPlayer.sendMessage(Messages.CANT_BREAK.toString());
			return;
		}
		if (GameValues.GAME.ENABLE_TREE_CAPITATOR) {
			gameManager.timber(block, event);
		}

		if (GameValues.GAME.RANDOM_DROPS_ENABLED) {
			block.getDrops().clear();
			event.setExpToDrop(0);

			final int randomMaterialIndex = MathUtils.getRandom().nextInt(XMaterial.values().length);
			final Material material = XMaterial.values()[randomMaterialIndex].parseMaterial();
			if (material == null) return;

			final ItemStack dropIs = new ItemStack(material);
			final Location loc = block.getLocation();
			final Location location = loc.add(0.5, 0.5, 0.5);

			Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, dropIs);
			return;
		}

		if (GameValues.GAME.CUSTOM_DROPS_ENABLED) {
			if (!gameManager.getCustomDropManager().getAppleChanceMap().isEmpty() && block.getType().name().contains("LEAVES")) {
				final ItemStack apple = new ItemStack(gameManager.getCustomDropManager().pickAppleToDrop(gameManager.getCustomDropManager().getAppleChanceMap()));
				if (apple.getType().equals(XMaterial.AIR.parseMaterial())) return;
				final Location location = block.getLocation().add(0.5, 0.5, 0.5);
				Bukkit.getWorld(block.getWorld().getName()).dropItem(location, apple);
				return;
			}
			if (UHCRevamp.useOldMethods) {
				if (block.getType().name().contains("REDSTONE") && block.getType().name().contains("ORE")) {
					final CustomDrop redstoneOreDrop = gameManager.getCustomDropManager().getCustomBlockDrop("REDSTONE", true);
					if (redstoneOreDrop != null) {
						redstoneOreDrop.dropItem(event);
						return;
					}
					return;
				} else if (block.getType().name().contains("LAPIS") && block.getType().name().contains("ORE")) {
					final CustomDrop lapisOreDrop = gameManager.getCustomDropManager().getCustomBlockDrop("LAPIS", true);
					if (lapisOreDrop != null) {
						lapisOreDrop.dropItem(event);
//						if (UHCRevamp.useOldMethods)
//							UHCRevamp.getInstance().getVersionUtils().giveLapis(p, MathUtils.randomInteger(6, 12));
						return;
					}
					return;
				}
			}
			if (gameManager.getCustomDropManager().hasBlockCustomDrop(block.getType())) {
				final CustomDrop customDrop = gameManager.getCustomDropManager().getCustomBlockDrop(block.getType());
				customDrop.dropItem(event);
			}
		}
	}

	@EventHandler
	public void handleBlockPlace(final BlockPlaceEvent event) {
		final UHCPlayer uhcPlayer = playerManager.getUHCPlayer(event.getPlayer().getUniqueId());
		if (!gameManager.isPlaying() || uhcPlayer.isDead() || gameManager.isEnding()) {
			uhcPlayer.sendMessage(Messages.CANT_PLACE.toString());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleWeatherChange(final WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void handleHunger(final FoodLevelChangeEvent event) {
		final Player p = (Player) event.getEntity();
		final UHCPlayer player = playerManager.getUHCPlayer(p.getUniqueId());
		if (!gameManager.isPlaying() || player.isDead() || gameManager.isEnding()) {
			p.setFoodLevel(20);
			p.setExhaustion(0);
			event.setCancelled(true);
		}
		if (gameManager.isPlaying()) {
			p.setExhaustion(0);
		}
	}

	@EventHandler
	public void onCraftItem(final CraftItemEvent event) {
		final ItemStack[] matrix1D = event.getInventory().getMatrix();
		final ItemStack[][] matrix2D = new ItemStack[3][3]; // Assuming a 3x3 crafting table

		// Convert 1D array to 2D array safely
		for (int i = 0; i < matrix1D.length && i < 9; i++) { // Ensure not to exceed bounds of either array
			matrix2D[i / 3][i % 3] = matrix1D[i];
		}
		// Iterate over all custom crafts to find a match
		for (final CustomRecipe customRecipe : gameManager.getRecipeManager().getRecipeList()) {
			if (customRecipe.matches(matrix2D)) {
//                event.setResult(Event.Result.DENY);
				event.setCurrentItem(customRecipe.getResult());
//                Bukkit.getLogger().info("Crafted custom item: " + customRecipe.getResult().getType());
				return; // Stop checking after finding the first matching recipe
			}
		}
		// This code will not be executed if a match is found
	}
}