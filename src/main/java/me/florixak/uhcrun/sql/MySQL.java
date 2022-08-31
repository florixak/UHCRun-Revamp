package me.florixak.uhcrun.sql;

import me.florixak.uhcrun.UHCRun;
import me.florixak.uhcrun.config.ConfigType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private String host = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getString("MySQL.host");
    private String port = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getString("MySQL.port");
    private String database = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getString("MySQL.database");
    private String username = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getString("MySQL.username");
    private String password = UHCRun.plugin.getConfigManager().getFile(ConfigType.SETTINGS).getConfig().getString("MySQL.password");

    private Connection connection;

    public boolean isConnected() {
        return (connection == null ? false : true);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
        }
    }

    public void disconnect(){
        if (isConnected()){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
