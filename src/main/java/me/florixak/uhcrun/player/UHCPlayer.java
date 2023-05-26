package me.florixak.uhcrun.player;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.game.kits.Kit;
import me.florixak.uhcrun.game.perks.Perk;
import me.florixak.uhcrun.teams.UHCTeam;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UHCPlayer {

    private final UUID uuid;
    private final String name;

    private PlayerData data;
    private PlayerState state;

    private UHCTeam team;

    private int kills;
    private Kit kit;
    private Perk perk;
    private boolean hasWon;

    private String nickname;

    public UHCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.data = new PlayerData(this);

        setState(PlayerState.LOBBY);

        this.hasWon = false;
        this.kills = 0;
        this.kit = null;
        this.perk = null;
        this.nickname = null;
        this.team = null;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Player getPlayer() {
        Player player = Bukkit.getPlayer(this.uuid);
        return player != null ? player : null;
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
        return getState() == PlayerState.SPECTATOR;
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

    public boolean hasKit() {
        return this.kit != null;
    }
    public Kit getKit() {
        return this.kit;
    }
    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public boolean hasPerk() {
        return this.perk != null;
    }
    public Perk getPerk() {
        return this.perk;
    }
    public void setPerk(Perk perk) {
        this.perk = perk;
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
}