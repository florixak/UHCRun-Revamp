package me.florixak.uhcrun.player;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.game.perks.Perk;
import me.florixak.uhcrun.teams.UHCTeam;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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

    private String nickname;
    private boolean sinceStart;

    public UHCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.data = new PlayerData(this);

        setState(PlayerState.LOBBY);
        setSinceStart(true);

        this.hasWon = false;
        this.kills = 0;
        this.assists = 0;
        this.kit = null;
        this.perk = null;
        this.nickname = null;
        this.team = null;
        this.assistsList = new ArrayList<>();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public String getName() {
        if (hasNickname()) return this.nickname;
        return this.name;
    }

    public String getLuckPermsPrefix() {
        if (!GameManager.getGameManager().areLuckPermsEnabled()) return "";
        User user = UHCRun.getLuckPerms().getUserManager().getUser(getUUID());
        if (user == null) return "";
        String prefix = user.getCachedData().getMetaData().getPrefix();
        if (prefix == null || prefix.isEmpty()) return "";
        return prefix;
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
        sendMessage(Messages.KITS_MONEY_DEDUCT.toString());
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

    public boolean wasDamagedByMorePeople() {
        return !this.assistsList.isEmpty() && this.assistsList.size() >= 2;
    }
    public UHCPlayer getKillAssistPlayer() {
        return this.assistsList.get(this.assistsList.size()-2);
    }
    public void addKillAssistPlayer(UHCPlayer uhcPlayer) {
        this.assistsList.add(uhcPlayer);
    }

    public boolean hasNickname() {
        return this.nickname != null;
    }
    public void setNickname(String nickname) {

        if (nickname == null) {
            sendMessage(Messages.NICK_UNNICKED.toString());
            this.nickname = null;
            return;
        }

        if (this.nickname == nickname) return;

        if (nickname.length() < 4) {
            sendMessage(Messages.NICK_MIN_CHARS.toString());
            return;
        }
        sendMessage(Messages.NICK_NICKED.toString().replace("%nick%", nickname));
        this.nickname = nickname;
    }

    public boolean isSinceStart() {
        return this.sinceStart;
    }
    public void setSinceStart(boolean b) {
        this.sinceStart = b;
    }

    public void sendMessage(String message) {
        if (message.isEmpty() || message == null || !isOnline()) return;
        getPlayer().sendMessage(TextUtils.color(message));
    }
    public void sendHotBarMessage(String message) {
        if (message.isEmpty() || message == null || !isOnline()) return;
        Utils.sendHotBarMessage(getPlayer(), TextUtils.color(message));
    }
    public void sendTitle(String title) {
        if (title.isEmpty() || title == null || !isOnline()) return;
        String[] split_title = title.split("\n");
        getPlayer().sendTitle(TextUtils.color(split_title[0]), TextUtils.color(split_title[1]));
    }
    public void teleport(Location loc) {
        if (loc == null) return;
        getPlayer().teleport(loc);
    }
}