package me.florixak.uhcrevamp.game.teams;

import me.florixak.uhcrevamp.config.Messages;
import me.florixak.uhcrevamp.game.player.UHCPlayer;
import me.florixak.uhcrevamp.utils.Utils;
import me.florixak.uhcrevamp.utils.text.TextUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UHCTeam {

    private final ItemStack displayItem;
    private final String name;
    private final int maxSize;

    private final String color;

    private List<UHCPlayer> members;

    public UHCTeam(ItemStack displayItem, String name, String color, int maxSize) {
        this.displayItem = displayItem;
        this.name = name;
        this.maxSize = maxSize;
        this.color = color;
        this.members = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return getColor() + this.name;
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public String getColor() {
        return this.color;
    }

    public UHCPlayer getLeader() {
        return getAliveMembers().get(0);
    }

    public boolean isFull() {
        return getMembers().size() >= this.maxSize;
    }

    public boolean isMember(UHCPlayer uhcPlayer) {
        return getMembers().contains(uhcPlayer);
    }

    public List<UHCPlayer> getMembers() {
        return this.members;
    }

    public String getMembersToString() {
        return getMembers().stream().map(UHCPlayer::getName).collect(Collectors.joining(", "));
    }

    private List<UHCPlayer> getMembers(Predicate<UHCPlayer> filter) {
        return getMembers().stream().filter(filter).collect(Collectors.toList());
    }

    public List<UHCPlayer> getAliveMembers() {
        return getMembers(UHCPlayer::isAlive);
    }

    public boolean isAlive() {
        return getAliveMembers() != null && getAliveMembers().size() != 0;
    }

    public int getKills() {
        return getMembers().stream().mapToInt(UHCPlayer::getKills).sum();
    }

    public void teleport(Location loc) {
        if (loc == null) return;
        for (UHCPlayer uhcPlayer : getAliveMembers()) {
            if (!uhcPlayer.isOnline()) return;
            uhcPlayer.getPlayer().teleport(loc);
        }
    }

    public void addMember(UHCPlayer uhcPlayer) {

        if (isMember(uhcPlayer)) {
            uhcPlayer.sendMessage(Messages.TEAM_ALREADY_IN.toString());
            return;
        }

        if (isFull() && !uhcPlayer.getPlayer().hasPermission("hoc.*")) {
            uhcPlayer.sendMessage(Messages.TEAM_FULL.toString());
            return;
        }
        if (uhcPlayer.hasTeam()) {
            uhcPlayer.getTeam().removeMember(uhcPlayer);
        }

        uhcPlayer.setTeam(this);
        this.members.add(uhcPlayer);

        uhcPlayer.sendMessage(Messages.TEAM_JOIN.toString().replace("%team%", uhcPlayer.getTeam().getDisplayName()));
    }

    public void removeMember(UHCPlayer uhcPlayer) {
        this.members.remove(uhcPlayer);
        uhcPlayer.setTeam(null);
    }

    public void sendHotBarMessage(String message) {
        if (message.isEmpty() || message == null) return;
        getMembers().stream().filter(UHCPlayer::isAlive).forEach(uhcPlayer -> Utils.sendHotBarMessage(uhcPlayer.getPlayer(), message));
    }

    public void sendMessage(String message) {
        if (message.isEmpty() || message == null) return;
        getMembers().stream().filter(UHCPlayer::isOnline).forEach(uhcPlayer -> uhcPlayer.sendMessage(TextUtils.color(message)));
    }

}
