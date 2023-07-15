package me.florixak.uhcrun.teams;

import me.florixak.uhcrun.game.Messages;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UHCTeam {

    private final ItemStack display_item;
    private final String name;
    private final int size;

    private final String color;

    private List<UHCPlayer> members;

    public UHCTeam(ItemStack display_item, String name, String color, int size) {
        this.display_item = display_item;
        this.name = name;
        this.size = size;
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
        return this.display_item;
    }

    public int getSize() {
        return size;
    }
    public String getColor() {
        return this.color;
    }

    public UHCPlayer getLeader() {
        return getAliveMembers().get(0);
    }

    public boolean isFull() {
        return getMembers().size() >= this.size;
    }
    public boolean isMember(UHCPlayer uhcPlayer) {
        return getMembers().contains(uhcPlayer);
    }

    public int getKills() {
        return getMembers().stream().mapToInt(UHCPlayer::getKills).sum();
    }

    public List<UHCPlayer> getMembers() {
        return this.members;
    }
    public String getMembersToString() {
        StringBuilder members = new StringBuilder();

        for (int i = 0; i < getMembers().size(); i++) {
            members.append(getMembers().get(i).getName());
            if (i < getMembers().size()-1) members.append(", ");
        }

        return members.toString();
    }
    private List<UHCPlayer> getMembers(Predicate<UHCPlayer> filter){
        return getMembers().stream().filter(filter).collect(Collectors.toList());
    }
    public List<UHCPlayer> getAliveMembers() {
        return getMembers(UHCPlayer::isAlive);
    }

    public boolean isAlive() {
        return getAliveMembers() != null && getAliveMembers().size() != 0;
    }

    public void teleport(Location loc) {
        if (loc == null) return;
        for (UHCPlayer uhcPlayer : getAliveMembers()) {
            if (!uhcPlayer.isOnline()) return;
            uhcPlayer.getPlayer().teleport(loc);
        }
    }

    public void join(UHCPlayer uhcPlayer) {

        if (isMember(uhcPlayer)) {
            uhcPlayer.sendMessage(Messages.TEAM_ALREADY_IN.toString());
            return;
        }

        if (isFull() && !uhcPlayer.getPlayer().hasPermission("hoc.*")) {
            uhcPlayer.sendMessage(Messages.TEAM_FULL.toString());
            return;
        }
        if (uhcPlayer.hasTeam()) {
            uhcPlayer.getTeam().leave(uhcPlayer);
        }

        uhcPlayer.setTeam(this);
        this.members.add(uhcPlayer);

        uhcPlayer.sendMessage(Messages.TEAM_JOIN.toString()
                .replace("%team%", uhcPlayer.getTeam().getDisplayName()));
    }
    public void leave(UHCPlayer uhcPlayer) {

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
