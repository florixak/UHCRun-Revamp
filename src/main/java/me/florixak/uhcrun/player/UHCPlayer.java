package me.florixak.uhcrun.player;

import me.florixak.uhcrun.kits.Kits;
import me.florixak.uhcrun.perks.Perks;
import me.florixak.uhcrun.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UHCPlayer {

    private final UUID uuid;
    private final String name;

    private PlayerState state;

    private String team;

    private int kills;
    private Kits kit;
    private Perks perk;

    public UHCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        setState(PlayerState.WAITING);

        this.kills = 0;
        this.kit = Kits.NONE;
        this.perk = Perks.NONE;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Player getPlayer() {
        Player player = Bukkit.getPlayer(this.uuid);
        return player != null ? player : null;
    }

    public String getName() {
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

    public boolean isWaiting() {
        return this.state == PlayerState.WAITING;
    }

    public boolean isPlaying() {
        return this.state == PlayerState.PLAYING;
    }

    public boolean isDead() {
        return this.state == PlayerState.DEAD;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeam() {
        return this.team;
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

    public Kits getKit() {
        return this.kit;
    }

    public void setKit(Kits kit) {
        this.kit = kit;
    }

    public boolean hasPerk() {
        return this.perk != null;
    }

    public Perks getPerk() {
        return this.perk;
    }

    public void setPerk(Perks perk) {
        this.perk = perk;
    }

    public void sendMessage(String message) {
        if (message.isEmpty() || message == null || !isOnline()) return;
        getPlayer().sendMessage(TextUtils.color(message));
    }

}
