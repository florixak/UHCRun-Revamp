package me.florixak.uhcrun.utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TimeConvertor {

    public static String convert(int secs) {
        int h = secs / 3600, i = secs - h * 3600, m = i / 60, s = i - m * 60;
        String timeF = "";

        if (h < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + h + ":";
        if (m < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + m + ":";
        if (s < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + s;

        return timeF;
    }

    public static String convertDay(int seconds) {
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;
        String timeF = days + "d:" + hours + "h:"  + minutes + "m:" + sec +"s";

        return timeF;
    }

    public static String convertHour(int seconds) {
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        String timeF = hours + "h:" + minutes + "m:" + sec +"s";

        return timeF;
    }

    public static String convertMinute(int seconds) {
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        String timeF = minutes + "m:" + sec +"s";

        return timeF;
    }

    public static String convertCountdown(int seconds) {
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        String timeF;
        if (seconds < 60) {
            timeF = sec +"s";
        } else {
            timeF = minutes+"m " + sec+"s";
        }
        return timeF;
    }

    public static String convertSeconds(int seconds) {
        long sec = seconds % 60;
        String timeF = sec +"s";

        return timeF;
    }

    public static String convertTime(Player p){
        long gameTime = Bukkit.getServer().getWorld(p.getWorld().getName()).getTime(), hours = gameTime / 1000 + 6, minutes = (gameTime % 1000) * 60 / 1000;

        String ampm = "AM";
        if (hours >= 12) {
            hours -= 12;
            ampm = "PM";
        }
        if (hours >= 12) {
            hours -= 12;
            ampm = "AM";
        }
        if (hours == 0) hours = 12;
        String mm = "0" + minutes;
        mm = mm.substring(mm.length() - 2, mm.length());
        return hours + ":" + mm + " " + ampm;
    }
}