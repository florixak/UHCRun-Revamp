package me.florixak.uhcrevamp.game.player;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import me.florixak.uhcrevamp.game.kits.Kit;
import me.florixak.uhcrevamp.game.perks.Perk;
import me.florixak.uhcrevamp.game.teams.UHCTeam;
import me.florixak.uhcrevamp.hook.LuckPermsHook;
import me.florixak.uhcrevamp.utils.TeleportUtils;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.XSeries.XPotion;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class UHCPlayer {

    private final UUID uuid;
    private final String name;

    private final PlayerData data;
    private PlayerState state;

    private UHCTeam team;

    private int kills;
    private int assists;
    private Kit kit;
    private Perk perk;
    private boolean hasWon;
    private long timePlayed;
    private final Map<UUID, Long> damageTrackers;
    private Location deathLoc;

    private double moneyForGameResult, moneyForKills, moneyForAssists, moneyForActivity;
    private double uhcExpForGameResult, uhcExpForKills, uhcExpForAssists, uhcExpForActivity;

    private String displayedStat;

    public UHCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.data = new PlayerData(this);

        setState(PlayerState.LOBBY);

        this.hasWon = false;
        this.kills = 0;
        this.assists = 0;
        this.kit = null;
        this.perk = null;
        this.team = null;
        this.deathLoc = null;
        this.damageTrackers = new HashMap<>();

        this.displayedStat = "Wins";

        this.moneyForGameResult = 0;
        this.moneyForKills = 0;
        this.moneyForAssists = 0;
        this.moneyForActivity = 0;
        this.uhcExpForGameResult = 0;
        this.uhcExpForKills = 0;
        this.uhcExpForAssists = 0;
        this.uhcExpForActivity = 0;
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

    public String getLuckPermsPrefix() {
        return LuckPermsHook.getPrefix(getPlayer());
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
        if (this.kit == kit) return;
        this.kit = kit;
        sendMessage(Messages.KITS_SELECTED.toString().replace("%kit%", kit.getDisplayName()));
        GameManager.getGameManager().getSoundManager().playSelectSound(getPlayer());
    }

    public boolean hasPerk() {
        return this.perk != null;
    }

    public Perk getPerk() {
        return this.perk;
    }

    public void setPerk(Perk perk) {
        if (this.perk == perk) return;
        this.perk = perk;
        sendMessage(Messages.PERKS_SELECTED.toString().replace("%perk%", perk.getDisplayName()));
        GameManager.getGameManager().getSoundManager().playSelectSound(getPlayer());
    }

    public void setDeathLocation(Location deathLoc) {
        this.deathLoc = deathLoc;
    }

    public long getTimePlayed() {
        return this.timePlayed;
    }

    public void addTimePlayed(long time) {
        this.timePlayed += time;
    }

    public Location getDeathLocation() {
        return this.deathLoc;
    }

    public void ready() {
        setState(PlayerState.ALIVE);

        setGameMode(GameMode.SURVIVAL);
        getPlayer().setHealth(getPlayer().getMaxHealth());
        getPlayer().setFoodLevel(20);

        clearInventory();

        if (GameValues.TEAM.TEAM_MODE && !hasTeam()) {
            GameManager.getGameManager().getTeamManager().joinRandomTeam(this);
        } else if (!GameValues.TEAM.TEAM_MODE) {

            UHCTeam uhcTeam = new UHCTeam(null, "", "&f", 1);
            GameManager.getGameManager().getTeamManager().addTeam(uhcTeam);
            setTeam(uhcTeam);
        }

        if (hasKit()) {
            if (!GameValues.KITS.BOUGHT_FOREVER) {
                getData().withdrawMoney(getKit().getCost());
                sendMessage(Messages.KITS_MONEY_DEDUCT.toString()
                        .replace("%previous-money%", String.valueOf((getData().getMoney() + getKit().getCost())))
                        .replace("%current-money%", String.valueOf(getData().getMoney()))
                        .replace("%kit-cost%", String.valueOf(getKit().getCost()))
                        .replace("%kit%", getKit().getDisplayName())
                );
            }
            getKit().giveKit(this);
        }
    }

    public void revive() {
        getPlayer().setHealth(getPlayer().getMaxHealth());
        getPlayer().setFoodLevel(20);
        getPlayer().setFireTicks(0);
        clearPotions();
        clearInventory();

        setState(PlayerState.ALIVE);

        //if (kit != null) getKit().giveKit(this);
        teleport(deathLoc == null ? TeleportUtils.getSafeLocation() : deathLoc);
    }

    public void die() {
        setDeathLocation(getPlayer().getLocation());

        getPlayer().spigot().respawn();

        getPlayer().setHealth(getPlayer().getMaxHealth());
        getPlayer().setFoodLevel(20);
        getPlayer().setFireTicks(0);
        clearPotions();
        clearInventory();

        teleport(new Location(
                Bukkit.getWorld(getDeathLocation().getWorld().getName()),
                getDeathLocation().getX() + 0,
                getDeathLocation().getY() + 10,
                getDeathLocation().getZ() + 0));

        setSpectator();
    }

    public void setSpectator() {
        if (state != PlayerState.DEAD) {
            setState(PlayerState.SPECTATOR);
            teleport(getPlayer().getLocation().getWorld().getBlockAt(0, 100, 0).getLocation());
        }
        setGameMode(GameMode.SPECTATOR);
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
        getPlayer().getActivePotionEffects().clear();
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
        Utils.sendHotBarMessage(getPlayer(), TextUtils.color(message));
    }

    public void sendTitle(String title) {
        if (title == null || title.isEmpty() || !isOnline()) return;
        String[] split_title = title.split("\n");
        getPlayer().sendTitle(TextUtils.color(split_title[0]), TextUtils.color(split_title[1]));
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
        getTeam().removeMember(this);
        this.deathLoc = null;
    }
}