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
            ps = plugin.mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS uhcrun "
                    + "('uuid' VARCHAR(100) PRIMARY KEY,"
                    + "'name' VARCHAR(100),"
                    + "'level' INT(100),"
                    + "'required_xp' INT(100),"
                    + "'money' DECIMAL(24,9),"
                    + "'wins' INT(100),"
                    + "'kills' INT(100),"
                    + "'deaths' INT(100))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(Player player) {
        try {
            UUID uuid = player.getUniqueId();
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("SELECT * FROM 'uhcrun' WHERE 'uuid'=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            results.next();
            if (!exists(uuid)) {
                PreparedStatement ps2 = plugin.mysql.getConnection().prepareStatement("INSERT IGNORE INTO uhcrun ('uuid','name') VALUES (?,?)");
                ps2.setString(1, uuid.toString());
                ps2.setString(2, player.getName());
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
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("SELECT * FROM 'uhcrun' WHERE 'uuid'=?");
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
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("UPDATE uhcrun SET LEVEL=? WHERE 'uuid'=?");
            ps.setInt(1, (getPlayerLevel(uuid) + 1));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getPlayerLevel(UUID uuid) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("SELECT LEVEL FROM uhcrun WHERE 'uuid'=?");
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
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("UPDATE uhcrun SET REQUIRED_XP=? WHERE 'uuid'=?");
            ps.setDouble(1, (getRequiredXP(uuid) + level_xp));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setRequiredXP(UUID uuid) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("UPDATE uhcrun SET REQUIRED_XP=? WHERE 'uuid'=?");
            ps.setDouble(1, plugin.getLevelManager().setRequiredExp(plugin.getPlayerManager().getUHCPlayer(uuid)));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public double getRequiredXP(UUID uuid) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("SELECT REQUIRED_XP FROM uhcrun WHERE 'uuid'=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            double level = 0;
            if (rs.next()) {
                level = rs.getInt("REQUIRED_XP");
                return level;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addMoney(UUID uuid, double money) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("UPDATE uhcrun SET MONEY=? WHERE 'uuid'=?");
            ps.setDouble(1, (getMoney(uuid) + money));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public double getMoney(UUID uuid) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("SELECT MONEY FROM uhcrun WHERE 'uuid'=?");
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
        return UHCRun.getInstance().getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getInt("string-coins");
    }

    public void addKill(UUID uuid, int kills) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("UPDATE uhcrun SET KILLS=? WHERE 'uuid'=?");
            ps.setInt(1, (getKills(uuid) + kills));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getKills(UUID uuid) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("SELECT KILLS FROM uhcrun WHERE 'uuid'=?");
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
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("UPDATE uhcrun SET WINS=? WHERE UUID=?");
            ps.setInt(1, (getWins(uuid) + wins));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getWins(UUID uuid) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("SELECT WINS FROM uhcrun WHERE UUID=?");
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
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("UPDATE uhcrun SET DEATHS=? WHERE UUID=?");
            ps.setInt(1, (getDeaths(uuid) + deaths));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getDeaths(UUID uuid) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("SELECT DEATHS FROM uhcrun WHERE UUID=?");
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


    public void emptyTable() {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("TRUNCATE uhcrun");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(UUID uuid) {
        try {
            PreparedStatement ps = plugin.mysql.getConnection().prepareStatement("DELETE FROM uhcrun WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}