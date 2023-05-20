package me.florixak.uhcrun.sql;

import me.florixak.uhcrun.game.GameManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {

    private GameManager gameManager;

    public SQLGetter(GameManager gameManager){
        this.gameManager = gameManager;
    }

    public void createTable() {
        PreparedStatement ps;
        try {
            ps = gameManager.getSQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS uhcrun "
                    + "('uuid' VARCHAR(100) PRIMARY KEY,"
                    + "'name' VARCHAR(100),"
                    + "'level' INT(100) DEFAULT '0',"
                    + "'required_xp' INT(100) DEFAULT '0',"
                    + "'money' DECIMAL(24,9) DEFAULT '0',"
                    + "'wins' INT(100) DEFAULT '0',"
                    + "'losses' INT(100) DEFAULT '0',"
                    + "'kills' INT(100) DEFAULT '0',"
                    + "'deaths' INT(100) DEFAULT '0')");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void emptyTable() {
        try {
            PreparedStatement ps = gameManager.getSQL().getConnection().prepareStatement("TRUNCATE uhcrun");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeUUID(UUID uuid) {
        try {
            PreparedStatement ps = gameManager.getSQL().getConnection().prepareStatement("DELETE FROM uhcrun WHERE 'uuid'=?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}