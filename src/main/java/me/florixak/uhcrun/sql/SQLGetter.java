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

    public SQLGetter(GameManager gameManager){
        this.conn = gameManager.getSQL().getConnection();

        createTable();
    }

    public void createTable() {
        PreparedStatement ps;
        FileConfiguration config = GameManager.getGameManager().getConfigManager().getFile(ConfigType.SETTINGS).getConfig();
        try {
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS uhcrun "
                    + "(uuid VARCHAR(100) PRIMARY KEY,"
                    + "name VARCHAR(100),"
                    + "uhc_level INT(100) DEFAULT " + config.getInt("settings.statistics.player-level.first-level", 0) + ","
                    + "uhc_exp INT(100) DECIMAL(24,9) 0,"
                    + "required_uhc_exp DECIMAL(24,9) DEFAULT " + config.getDouble("settings.statistics.player-level.first-required-exp", 100) + ","
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
                PreparedStatement ps2 = conn.prepareStatement("INSERT INGORE INTO uhcrun (uuid,name) VALUES (?,?)");
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
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM uhcrun WHERE uuid=?");
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