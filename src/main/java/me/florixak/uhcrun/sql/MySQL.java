package me.florixak.uhcrun.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private Connection con;

    private String host;
    private String port;
    private String database;
    private String user;
    private String password;

    public MySQL(String host, String port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;

        connect();
    }

    public void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL disconnected!");
        }
    }

    public void disconnect() {
        try {
            if (this.hasConnection()) {
                this.con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasConnection() {
        return this.con != null ? true : false;
    }

    public Connection getConnection() {
        return this.con;
    }
}
