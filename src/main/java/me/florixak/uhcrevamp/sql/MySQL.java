package me.florixak.uhcrevamp.sql;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.utils.text.TextUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private Connection conn;

    private final String host;
    private final String port;
    private final String database;
    private final String user;
    private final String password;

    public MySQL(String host, String port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public void connect() {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
            UHCRevamp.getInstance().getLogger().info(TextUtils.color("&aMySQL connected!"));
        } catch (SQLException e) {
            UHCRevamp.getInstance().getLogger().severe(TextUtils.color("&cMySQL can not be connected!"));
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (this.hasConnection()) {
                this.conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasConnection() {
        return this.conn != null;
    }

    public Connection getConnection() {
        if (this.conn != null) {
            return this.conn;
        }
        connect();
        return this.conn;
    }
}