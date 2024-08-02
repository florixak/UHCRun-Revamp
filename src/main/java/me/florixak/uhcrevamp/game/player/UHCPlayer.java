package me.florixak.uhcrevamp.game.player;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.kits.Kit;
import me.florixak.uhcrevamp.game.perks.Perk;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import me.florixak.uhcrevamp.utils.NMSUtils;
import me.florixak.uhcrevamp.utils.TeleportUtils;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;

public class UHCPlayer {

	private final UUID uuid;
	private final String name;
	private final PlayerData data;

	private PlayerState state;
	private int kills = 0;
	private int assists = 0;
	private Kit kit;
	private Perk perk;
	private UHCTeam team;
	private boolean hasWon = false;
	private long timePlayed;
	private final Map<UUID, Long> damageTrackers = new HashMap<>();
	private Location spawnLoc;

	private double moneyForGameResult = 0, moneyForKills = 0, moneyForAssists = 0, moneyForActivity = 0;
	private double uhcExpForGameResult = 0, uhcExpForKills = 0, uhcExpForAssists = 0, uhcExpForActivity = 0;

	public UHCPlayer(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.data = new PlayerData(this);
		setState(PlayerState.LOBBY);
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.uuid);
	}

	public String getName() {
		if (Bukkit.getPlayer(name) == null) {
			return getData().getName();
		}
		return this.name;
	}

	public boolean isOnline() {
		Player player = Bukkit.getPlayer(this.uuid);
		return player != null;
	}

	public void setState(PlayerState state) {
		if (state == this.state) return;
		this.state = state;
	}

	public PlayerState getState() {
		return this.state;
	}

	public PlayerData getData() {
		return this.data;
	}

	public void setWinner(boolean win) {
		if (this.hasWon == win) return;
		this.hasWon = win;
	}

	public boolean isWinner() {
		return this.hasWon;
	}

	public boolean isAlive() {
		return getState() == PlayerState.ALIVE;
	}

	public boolean isDead() {
		return getState() == PlayerState.DEAD;
	}

	public boolean isSpectator() {
		return getState() == PlayerState.SPECTATOR || getState() == PlayerState.DEAD;
	}

	public void setTeam(UHCTeam team) {
		this.team = team;
	}

	public UHCTeam getTeam() {
		return this.team;
	}

	public boolean hasTeam() {
		return getTeam() != null;
	}

	public int getKills() {
		return this.kills;
	}

	public void addKill() {
		this.kills++;
	}

	public int getAssists() {
		return this.assists;
	}

	public void addAssist() {
		this.assists++;
	}

	public boolean hasKit() {
		return this.kit != null;
	}

	public Kit getKit() {
		return this.kit;
	}

	public void setKit(Kit kit) {
		if (this.kit != kit) {
			this.kit = kit;
			sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", kit.getDisplayName()));
			GameManager.getGameManager().getSoundManager().playSelectSound(getPlayer());
		}
	}

	public boolean hasPerk() {
		return this.perk != null;
	}

	public Perk getPerk() {
		return this.perk;
	}

	public void setPerk(Perk perk) {
		if (this.perk != perk) {
			this.perk = perk;
			sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", perk.getDisplayName()));
			GameManager.getGameManager().getSoundManager().playSelectSound(getPlayer());
		}
	}

	public void setSpawnLocation(Location spawnLoc) {
		this.spawnLoc = spawnLoc;
	}

	public Location getSpawnLocation() {
		return this.spawnLoc;
	}

	public long getTimePlayed() {
		return this.timePlayed;
	}

	public void addTimePlayed(long time) {
		this.timePlayed += time;
	}

	public void revive() {
		setState(PlayerState.ALIVE);

		getPlayer().setHealth(getPlayer().getMaxHealth());
		getPlayer().setFoodLevel(20);
		getPlayer().setExhaustion(0);
		getPlayer().setFireTicks(0);
		clearPotions();
		clearInventory();

		//if (kit != null) getKit().giveKit(this);
		teleport(spawnLoc == null ? TeleportUtils.getSafeLocation() : spawnLoc);
	}

	public void die() {
		setState(PlayerState.DEAD);

		getPlayer().spigot().respawn();

		getPlayer().setHealth(getPlayer().getMaxHealth());
		getPlayer().setFoodLevel(20);
		getPlayer().setExhaustion(0);
		getPlayer().setFireTicks(0);
		clearPotions();
		clearInventory();

		setSpectator();
	}

	public void setSpectator() {
		if (state != PlayerState.DEAD) {
			setState(PlayerState.SPECTATOR);
		}
		setGameMode(GameMode.SPECTATOR);
		teleport(new Location(Bukkit.getWorld(GameValues.WORLD_NAME), 0, 100, 0));
	}


	//    Assists
	public void addDamage(UUID damager, long time) {
		damageTrackers.put(damager, time);
	}

	public List<UUID> getAssistants(long deathTime, long assistWindow, UUID killer) {
		return damageTrackers.entrySet().stream()
				.filter(entry -> entry.getValue() + assistWindow > deathTime && !entry.getKey().equals(killer))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	public void clearDamageTrackers() {
		damageTrackers.clear();
	}

	public void addMoneyForGameResult(double money) {
		this.moneyForGameResult += money;
	}

	public void addMoneyForKills(double money) {
		this.moneyForKills += money;
	}

	public void addMoneyForAssists(double money) {
		this.moneyForAssists += money;
	}

	public void addMoneyForActivity(double money) {
		this.moneyForActivity += money;
	}

	public void addUHCExpForGameResult(double uhcExp) {
		this.uhcExpForGameResult += uhcExp;
	}

	public void addUHCExpForKills(double uhcExp) {
		this.uhcExpForKills += uhcExp;
	}

	public void addUHCExpForAssists(double uhcExp) {
		this.uhcExpForAssists += uhcExp;
	}

	public void addUHCExpForActivity(double uhcExp) {
		this.uhcExpForActivity += uhcExp;
	}

	public double getMoneyForGameResult() {
		return this.moneyForGameResult;
	}

	public double getMoneyForKills() {
		return this.moneyForKills;
	}

	public double getMoneyForAssists() {
		return this.moneyForAssists;
	}

	public double getMoneyForActivity() {
		return this.moneyForActivity;
	}

	public double getUHCExpForGameResult() {
		return this.uhcExpForGameResult;
	}

	public double getUHCExpForKills() {
		return this.uhcExpForKills;
	}

	public double getUHCExpForAssists() {
		return this.uhcExpForAssists;
	}

	public double getUHCExpForActivity() {
		return uhcExpForActivity;
	}

	public boolean hasPermission(String permission) {
		return getPlayer().hasPermission(permission);
	}

	public void teleport(Location loc) {
		if (loc == null) return;
		getPlayer().teleport(loc);
	}

	public void clearInventory() {
		getPlayer().getInventory().clear();

		//clear player armor
		ItemStack[] emptyArmor = new ItemStack[4];
		for (int i = 0; i < emptyArmor.length; i++) {
			emptyArmor[i] = new ItemStack(Material.AIR);
		}
		getPlayer().getInventory().setArmorContents(emptyArmor);
	}

	public void giveExp(int exp) {
		getPlayer().giveExp(exp);
	}

	public void addEffect(XPotion potion, int duration, int level) {
		getPlayer().addPotionEffect(Objects.requireNonNull(potion.buildPotionEffect(duration * 20, level), "Cannot create potion from null."));
	}

	public void clearPotions() {
//		getPlayer().getActivePotionEffects().clear();
		for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
			getPlayer().removePotionEffect(effect.getType());
		}
	}

	public void kick(String message) {
		if (message == null || message.isEmpty() || !isOnline()) return;
		getPlayer().kickPlayer(TextUtils.color(message));
	}

	public void setGameMode(GameMode gameMode) {
		getPlayer().setGameMode(gameMode);
	}

	public void sendMessage(String message) {
		if (message == null || message.isEmpty() || !isOnline()) return;
		getPlayer().sendMessage(TextUtils.color(message));
	}

	public void sendMessage(String message, String... replacements) {
		if (message == null || message.isEmpty() || !isOnline() || replacements.length % 2 != 0) return;

		String messageToSend = TextUtils.color(message);
		for (int i = 0; i < replacements.length; i += 2) {
			messageToSend = messageToSend.replace(replacements[i], replacements[i + 1]);
		}
		sendMessage(messageToSend);
	}

	public void openInventory(Inventory inventory) {
		getPlayer().openInventory(inventory);
	}

	public void closeInventory() {
		getPlayer().closeInventory();
	}

	public void sendHotBarMessage(String message) {
		if (message == null || message.isEmpty() || !isOnline()) return;
		NMSUtils.sendHotBarMessageViaNMS(getPlayer(), TextUtils.color(message));
	}

	public void leaveTeam() {
		if (getTeam() == null) return;
		getTeam().removeMember(this);
	}

	public void reset() {
		this.hasWon = false;
		this.kills = 0;
		this.assists = 0;
		this.kit = null;
		this.perk = null;
		if (hasTeam()) getTeam().removeMember(this);
		this.team = null;
		this.spawnLoc = null;
		this.timePlayed = 0;
		this.damageTrackers.clear();
		this.moneyForGameResult = 0;
		this.moneyForKills = 0;
		this.moneyForAssists = 0;
		this.moneyForActivity = 0;
		this.uhcExpForGameResult = 0;
		this.uhcExpForKills = 0;
		this.uhcExpForAssists = 0;
		this.uhcExpForActivity = 0;
	}
}