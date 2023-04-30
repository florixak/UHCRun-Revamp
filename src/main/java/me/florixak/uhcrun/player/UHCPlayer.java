package me.florixak.uhcrun.player;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.kits.KitType;
import me.florixak.uhcrun.perks.PerkType;
import me.florixak.uhcrun.teams.UHCTeam;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
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
    private KitType kit;
    private PerkType perk;
    private boolean hasWon;

    private String nickname;

    public UHCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.data = new PlayerData(this);

        setState(PlayerState.LOBBY);

        this.hasWon = false;
        this.kills = 0;
        this.kit = KitType.NONE;
        this.perk = PerkType.NONE;
        this.nickname = null;
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
    public KitType getKit() {
        return this.kit;
    }
    public void setKit(KitType kit) {
        this.kit = kit;
    }

    public boolean hasPerk() {
        return this.perk != null;
    }
    public PerkType getPerk() {
        return this.perk;
    }
    public void setPerk(PerkType perk) {
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
        getPlayer().sendTitle(TextUtils.color(split_title[0]),
                TextUtils.color(split_title[1]));
    }

}
