package me.florixak.uhcrun.sql;

import me.florixak.uhcrun.config.ConfigType;
import me.florixak.uhcrun.game.GameManager;
import me.florixak.uhcrun.player.UHCPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {

    private final Connection conn;
    private final String table;

    public SQLGetter(GameManager gameManager) {
        this.conn = gameManager.getSQL().getConnection();

        this.table = "uhcrun";
        createTable();
    }

    public void createTable() {
        PreparedStatement ps;
        FileConfiguration config = GameManager.getGameManager().getConfigManager().getFile(ConfigType.SETTINGS).getConfig();

        int startUHCLevel = config.getInt("settings.statistics.player-level.first-level", 0);
        double startRequiredUHCExp = config.getDouble("settings.statistics.player-level.first-required-exp", 100);

        try {
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " "
                    + "(uuid VARCHAR(100) PRIMARY KEY,"
                    + "name VARCHAR(100),"
                    + "uhc_level INT(100) DEFAULT " + startUHCLevel + ","
                    + "uhc_exp INT(100) DECIMAL(24,9) 0,"
                    + "required_uhc_exp DECIMAL(24,9) DEFAULT " + startRequiredUHCExp + ","
                    + "wins INT(100) DEFAULT 0,"
                    + "losses INT(100) DEFAULT 0,"
                    + "kills INT(100) DEFAULT 0,"
                    + "deaths INT(100) DEFAULT 0)");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(Player p) {
        try {
            UUID uuid = p.getUniqueId();

            if (!exists(uuid)) {
                PreparedStatement ps2 = conn.prepareStatement("INSERT INGORE INTO " + table + " (uuid,name) VALUES (?,?)");
                ps2.setString(1, uuid.toString());
                ps2.setString(2, p.getName());
                ps2.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean exists(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUHCLevel(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET uhc_level=? WHERE uuid=?");
            ps.setInt(1, getUHCLevel(uuid)+1);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getUHCLevel(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT uhc_level FROM uhcrun WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("uhc_level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setUHCLevel(UUID uuid, int level) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET uhc_level=? WHERE uuid=?");
            ps.setInt(1, level);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUHCExp(UUID uuid, double exp) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET uhc_exp=? WHERE uuid=?");
            ps.setDouble(1, getUHCExp(uuid)+exp);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public double getUHCExp(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT uhc_exp FROM uhcrun WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("uhc_exp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setUHCExp(UUID uuid, double exp) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET uhc_exp=? WHERE uuid=?");
            ps.setDouble(1, exp);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRequiredUHCExp(UUID uuid, double exp) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET required_uhc_exp=? WHERE uuid=?");
            ps.setDouble(1, getRequiredUHCExp(uuid)+exp);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public double getRequiredUHCExp(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT required_uhc_exp FROM uhcrun WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("required_uhc_exp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setRequiredUHCExp(UUID uuid, double exp) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET required_uhc_exp=? WHERE uuid=?");
            ps.setDouble(1, exp);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addWin(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET wins=? WHERE uuid=?");
            ps.setDouble(1, getWins(uuid)+1);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getWins(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT wins FROM uhcrun WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("wins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setWins(UUID uuid, int wins) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET wins=? WHERE uuid=?");
            ps.setDouble(1, wins);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLose(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET losses=? WHERE uuid=?");
            ps.setDouble(1, getLosses(uuid)+1);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getLosses(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT losses FROM uhcrun WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("losses");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setLosses(UUID uuid, int losses) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET losses=? WHERE uuid=?");
            ps.setDouble(1, losses);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addKill(UUID uuid, int kills) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET kills=? WHERE uuid=?");
            ps.setDouble(1, getKills(uuid)+kills);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getKills(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT kills FROM uhcrun WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("kills");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setKills(UUID uuid, int kills) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET kills=? WHERE uuid=?");
            ps.setDouble(1, kills);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDeath(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET deaths=? WHERE uuid=?");
            ps.setDouble(1, getDeaths(uuid)+1);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getDeaths(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT deaths FROM uhcrun WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("deaths");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setDeaths(UUID uuid, int deaths) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE uhcrun SET deaths=? WHERE uuid=?");
            ps.setDouble(1, deaths);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void emptyTable() {
        try {
            PreparedStatement ps = conn.prepareStatement("TRUNCATE uhcrun");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeUUID(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM uhcrun WHERE 'uuid'=?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}