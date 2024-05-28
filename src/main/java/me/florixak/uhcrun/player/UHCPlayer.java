package me.florixak.uhcrun.player;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.GameValues;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.game.perks.Perk;
import me.florixak.uhcrun.hook.LuckPermsHook;
import me.florixak.uhcrun.teams.UHCTeam;
import me.florixak.uhcrun.utils.TeleportUtils;
import me.florixak.uhcrun.utils.Utils;
import me.florixak.uhcrun.utils.XSeries.XPotion;
import me.florixak.uhcrun.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    private List<UHCPlayer> assistsList;
    private Location deathLoc;

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
        this.assistsList = new ArrayList<>();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public String getName() {
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
        sendMessage(Messages.KITS_SELECTED.toString()
                .replace("%kit%", kit.getName()));
        sendMessage(Messages.KITS_MONEY_DEDUCT_INFO.toString());
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
    }

    public void setDeathLocation(Location deathLoc) {
        this.deathLoc = deathLoc;
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

        if (GameValues.TEAM_MODE && !hasTeam()) {
            GameManager.getGameManager().getTeamManager().joinRandomTeam(this);
        } else if (!GameValues.TEAM_MODE) {
            UHCTeam uhcTeam = new UHCTeam(null, "", "&f", 1);
            GameManager.getGameManager().getTeamManager().addTeam(uhcTeam);
            setTeam(uhcTeam);
        }

        if (hasKit()) {
            sendMessage(Messages.KITS_MONEY_DEDUCT.toString()
                    .replace("%money%", String.valueOf(getData().getMoney()))
                    .replace("%current-money%", String.valueOf(getData().getMoney()-getKit().getCost()))
                    .replace("%kit-cost%", String.valueOf(getKit().getCost()))
                    .replace("%kit%", getKit().getName())
            );
            getData().withdrawMoney(getKit().getCost());
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

        getPlayer().setHealth(getPlayer().getMaxHealth());
        getPlayer().setFoodLevel(20);
        getPlayer().setFireTicks(0);
        clearPotions();
        clearInventory();

        getPlayer().spigot().respawn();

        teleport(new Location(
                Bukkit.getWorld(getDeathLocation().getWorld().getName()),
                getDeathLocation().getX()+0,
                getDeathLocation().getY()+10,
                getDeathLocation().getZ()+0));

        setSpectator();
    }

    public void setSpectator() {
        if (state != PlayerState.DEAD) {
            setState(PlayerState.SPECTATOR);
            teleport(getPlayer().getLocation().getWorld().getBlockAt(0, 100, 0).getLocation());
        }
        setGameMode(GameMode.SPECTATOR);
    }

    public boolean wasDamagedByMorePeople() {
        return this.assistsList.size() > 1;
    }
    public UHCPlayer getKillAssistPlayer() {
        return this.assistsList.get(this.assistsList.size()-2);
    }
    public void addKillAssistPlayer(UHCPlayer uhcPlayer) {
        this.assistsList.add(uhcPlayer);
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
        for(int i = 0; i < emptyArmor.length; i++){
            emptyArmor[i] = new ItemStack(Material.AIR);
        }
        getPlayer().getInventory().setArmorContents(emptyArmor);
    }
    public void giveExp(int exp) {
        getPlayer().giveExp(exp);
    }
    public void addPotion(XPotion potion, int duration, int power) {
        getPlayer().addPotionEffect(Objects.requireNonNull(potion.buildPotionEffect(duration, power), "Cannot create potion from null."));
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
        this.assistsList = null;
    }
}