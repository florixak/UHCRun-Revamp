package me.florixak.uhcrun.manager.scoreboard;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private Map<UUID, ScoreHelper> players;

    private final String title;
    private List<String> waiting;
    private List<String> starting;
    private List<String> mining;
    private List<String> pvp;
    private List<String> deathmatch;
    private List<String> ending;

    private final GameManager gameManager;

    public ScoreboardManager(GameManager gameManager){
        this.gameManager = gameManager;
        this.players = new HashMap<>();

        FileConfiguration config = gameManager.getConfigManager().getFile(ConfigType.SCOREBOARD).getConfig();

        this.title = config.getString("scoreboard.title");

        this.waiting = config.getStringList("scoreboard.waiting");
        this.starting = config.getStringList("scoreboard.starting");
        this.mining = config.getStringList("scoreboard.mining");
        this.pvp = config.getStringList("scoreboard.pvp");
        this.deathmatch = config.getStringList("scoreboard.deathmatch");
        this.ending = config.getStringList("scoreboard.ending");
    }

    public void createWaitingSb(Player p){
        players.put(p.getUniqueId(), updateWaitingSb(p.getUniqueId()));
    }
    public ScoreHelper updateWaitingSb(UUID uuid){

        Player p = Bukkit.getPlayer(uuid);

        if (p == null) return null;

        int sb = this.waiting.size();

        ScoreHelper helper = players.get(p.getUniqueId());
        if (helper == null) helper = new ScoreHelper(p);
        helper.setTitle(title);

        for (String text : this.waiting){
            helper.setSlot(sb, text);
            sb--;
        }
        return helper;
    }

    public void createStartingSb(Player p){
        players.put(p.getUniqueId(), updateStartingSb(p.getUniqueId()));
    }
    public ScoreHelper updateStartingSb(UUID uuid) {

        Player p = Bukkit.getPlayer(uuid);

        if (p == null) return null;

        int sb = this.starting.size();

        ScoreHelper helper = players.get(p.getUniqueId());
        if (helper == null) helper = new ScoreHelper(p);
        helper.setTitle(title);

        for (String text : this.starting) {
            helper.setSlot(sb, text);
            sb--;
        }
        return helper;
    }

    public void createMiningSb(Player p){
        players.put(p.getUniqueId(), updateMiningSb(p.getUniqueId()));
    }
    public ScoreHelper updateMiningSb(UUID uuid){

        Player p = Bukkit.getPlayer(uuid);

        if (p == null) return null;

        int sb = this.mining.size();

        ScoreHelper helper = players.get(p.getUniqueId());
        if (helper == null) helper = new ScoreHelper(p);
        helper.setTitle(title);

        for (String text : this.mining){
            helper.setSlot(sb, text);
            sb--;
        }
        return helper;
    }

    public void createPvPSb(Player p){
        players.put(p.getUniqueId(), updatePvPSb(p.getUniqueId()));
    }
    public ScoreHelper updatePvPSb(UUID uuid){

        Player p = Bukkit.getPlayer(uuid);

        if (p == null) return null;

        int sb = this.pvp.size();

        ScoreHelper helper = players.get(p.getUniqueId());
        if (helper == null) helper = new ScoreHelper(p);
        helper.setTitle(title);

        for (String text : this.pvp){
            helper.setSlot(sb, text);
            sb--;
        }
        return helper;
    }

    public void createDeathmatchSb(Player p){
        players.put(p.getUniqueId(), updateDeathmatchSb(p.getUniqueId()));
    }
    public ScoreHelper updateDeathmatchSb(UUID uuid){

        Player p = Bukkit.getPlayer(uuid);

        if (p == null) return null;

        int sb = this.deathmatch.size();

        ScoreHelper helper = players.get(p.getUniqueId());
        if (helper == null) helper = new ScoreHelper(p);
        helper.setTitle(title);

        for (String text : this.deathmatch){
            helper.setSlot(sb, text);
            sb--;
        }
        return helper;
    }

    public void createEndingSb(Player p){
        players.put(p.getUniqueId(), updateEndingSb(p.getUniqueId()));
    }
    public ScoreHelper updateEndingSb(UUID uuid){

        Player p = Bukkit.getPlayer(uuid);

        if (p == null) return null;

        int sb = this.ending.size();

        ScoreHelper helper = players.get(p.getUniqueId());
        if (helper == null) helper = new ScoreHelper(p);
        helper.setTitle(title);

        for (String text : this.ending){
            helper.setSlot(sb, text);
            sb--;
        }
        return helper;
    }


    public void setScoreboard(Player p) {
        removeFromMap(p);
        switch (gameManager.getGameState()) {
            case LOBBY:
                createWaitingSb(p);
                break;
            case STARTING:
                createStartingSb(p);
                break;
            case MINING:
                createMiningSb(p);
                break;
            case PVP:
                createPvPSb(p);
                break;
            case DEATHMATCH:
                createDeathmatchSb(p);
                break;
            case ENDING:
                createEndingSb(p);
                break;
        }
    }

    private void removeFromMap(Player p) {
        if (players.containsKey(p.getUniqueId())) {
            players.remove(p.getUniqueId());
        }
    }

    public void removeScoreboard(Player p) {
        if (players.containsKey(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }
}