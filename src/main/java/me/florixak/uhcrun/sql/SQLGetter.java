package me.florixak.uhcrun.sql;


import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {

    private UHCRun plugin;

    public SQLGetter(UHCRun plugin){
        this.plugin = plugin;
    }

    public void createTable() {
        PreparedStatement ps;
        try {
            ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS uhcrun "
                    + "(NAME VARCHAR(100),UUID VARCHAR(100),"
                    + "LEVEL INT(100),REQUIRED_XP INT(100),MONEY DECIMAL(24,9),WINS INT(100),KILLS INT(100),DEATHS INT(100),"
                    + "STARTER INT(100),ENCHANTER INT(100),MINER INT(100),HEALER INT(100),HORSE_RIDER INT(100),"
                    + "STRENGTH INT(100),REGEN INT(100),SPEED INT(100),INVIS INT(100),FIRE_RES INT(100),END_PEARL INT(100),"
                    + "RESIST INT(100),"
                    + "PRIMARY KEY (NAME))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(Player player) {
        try {
            UUID uuid = player.getUniqueId();
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            results.next();
            if (!exists(uuid)) {
                PreparedStatement ps2 = plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO uhcrun"
                        + " (NAME,UUID) VALUES (?,?)");
                ps2.setString(1, player.getName());
                ps2.setString(2, uuid.toString());
                ps2.executeUpdate();
                addPlayerLevel(uuid);
                setRequiredXP(uuid);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // STATISTICS
    public void addPlayerLevel(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET LEVEL=? WHERE UUID=?");
            ps.setInt(1, (getPlayerLevel(uuid) + 1));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getPlayerLevel(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT LEVEL FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int level = 1;
            if (rs.next()) {
                level = rs.getInt("LEVEL");
                return level;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void addRequiredXP(UUID uuid, double level_xp) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET REQUIRED_XP=? WHERE UUID=?");
            ps.setDouble(1, (getRequiredXP(uuid) + level_xp));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setRequiredXP(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET REQUIRED_XP=? WHERE UUID=?");
            ps.setDouble(1, plugin.getLevelManager().setRequiredExp(uuid));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public double getRequiredXP(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT REQUIRED_XP FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            double level = plugin.getLevelManager().setRequiredExp(uuid);
            if (rs.next()) {
                level = rs.getInt("REQUIRED_XP");
                return level;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plugin.getLevelManager().setRequiredExp(uuid);
    }

    public void addMoney(UUID uuid, double money) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET MONEY=? WHERE UUID=?");
            ps.setDouble(1, (getMoney(uuid) + money));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public double getMoney(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT MONEY FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            double money = 0.0;
            if (rs.next()) {
                money = rs.getDouble("MONEY");
                return money;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getInt("string-coins");
    }

    public void addKill(UUID uuid, int kills) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET KILLS=? WHERE UUID=?");
            ps.setInt(1, (getKills(uuid) + kills));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getKills(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT KILLS FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int kills = 0;
            if (rs.next()) {
                kills = rs.getInt("KILLS");
                return kills;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addWin(UUID uuid, int wins) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET WINS=? WHERE UUID=?");
            ps.setInt(1, (getWins(uuid) + wins));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getWins(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT WINS FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("WINS");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addDeath(UUID uuid, int deaths) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET DEATHS=? WHERE UUID=?");
            ps.setInt(1, (getDeaths(uuid) + deaths));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getDeaths(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT DEATHS FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("DEATHS");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // KITS
    public void addStarter(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET STARTER=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveStarter(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT STARTER FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("STARTER");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addEnchanter(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET ENCHANTER=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveEnchanter(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT ENCHANTER FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("ENCHANTER");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addMiner(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET MINER=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveMiner(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT MINER FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("MINER");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addHealer(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET HEALER=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveHealer(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT HEALER FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("HEALER");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addHorseRider(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET HORSE_RIDER=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveHorseRider(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT HORSE_RIDER FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("HORSE_RIDER");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // PERKS
    public void addStrength(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET STRENGTH=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveStrength(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT STRENGTH FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("STRENGTH");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addRegen(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET REGEN=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveRegen(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT REGEN FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("REGEN");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addSpeed(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET SPEED=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveSpeed(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT SPEED FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("MINER");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addInvis(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET INVIS=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveInvis(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT INVIS FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("INVIS");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addFireRes(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET FIRE_RES=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveFireREsr(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT FIRE_RES FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("FIRE_RES");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addEndPearl(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET END_PEARL=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveEndPearl(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT END_PEARL FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("END_PEARL");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addResist(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE uhcrun SET RESIST=? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int haveResist(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT RESIST FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int wins = 0;
            if (rs.next()) {
                wins = rs.getInt("RESIST");
                return wins;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void emptyTable() {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("TRUNCATE uhcrun");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("DELETE FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}