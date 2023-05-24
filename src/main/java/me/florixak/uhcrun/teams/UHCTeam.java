package me.florixak.uhcrun.teams;

import me.florixak.uhcrun.config.Messages;
import me.florixak.uhcrun.player.UHCPlayer;
import me.florixak.uhcrun.utils.TextUtils;
import me.florixak.uhcrun.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UHCTeam {

    private ItemStack display_item;
    private String name;
    private int size;

    private String color;

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
        return this.members.contains(uhcPlayer);
    }

    public int getKills() {
        return members.stream().mapToInt(UHCPlayer::getKills).sum();
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
        return this.members.stream().filter(filter).collect(Collectors.toList());
    }
    public List<UHCPlayer> getAliveMembers() {
        return getMembers(hocPlayer -> hocPlayer.isAlive());
    }

    public boolean isAlive() {
        return getAliveMembers() != null;
    }

    public void teleport(Location loc) {
        for (UHCPlayer uhcPlayer : getAliveMembers()) {
            uhcPlayer.getPlayer().teleport(loc);
        }
    }

    public void join(UHCPlayer uhcPlayer) {

        if (isMember(uhcPlayer)) {
            uhcPlayer.sendMessage(Messages.TEAM_ALREADY_IN_TEAM.toString());
            return;
        }

        if (isFull() && !uhcPlayer.getPlayer().hasPermission("hoc.*")) {
            uhcPlayer.sendMessage(Messages.TEAM_FULL.toString());
            return;
        }

        if (uhcPlayer.hasTeam()) {
            uhcPlayer.getTeam().leave(uhcPlayer);
        }

        getMembers().add(uhcPlayer);
        uhcPlayer.setTeam(this);
        uhcPlayer.sendMessage(Messages.TEAM_JOIN.toString()
                .replace("%player%", uhcPlayer.getName())
                .replace("%team%", TextUtils.color(getDisplayName())));
    }
    public void leave(UHCPlayer uhcPlayer) {

        if (!uhcPlayer.hasTeam()) {
            uhcPlayer.sendMessage(Messages.TEAM_NOT_IN_TEAM.toString());
            return;
        }

        uhcPlayer.sendMessage(Messages.TEAM_LEAVE.toString()
                .replace("%player%", uhcPlayer.getName())
                .replace("%team%", TextUtils.color(getDisplayName())));
        getMembers().remove(uhcPlayer);
        uhcPlayer.setTeam(null);
    }
    public void sendHotBarMessage(String message) {
        if (message.isEmpty() || message == null) return;
        getMembers().stream().filter(hocPlayer -> hocPlayer.isAlive()).forEach(hocPlayer -> Utils.sendHotBarMessage(hocPlayer.getPlayer(), message));
    }
    public void sendMessage(String message) {
        if (message.isEmpty() || message == null) return;
        this.members.forEach(hocPlayer -> hocPlayer.sendMessage(TextUtils.color(message)));
    }

}
