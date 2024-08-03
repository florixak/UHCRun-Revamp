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

	public MySQL(final String host, final String port, final String database, final String user, final String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;

		connect();
	}

	private void connect() {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
			UHCRevamp.getInstance().getLogger().info(TextUtils.color("&aMySQL connected!"));
		} catch (final SQLException e) {
			UHCRevamp.getInstance().getLogger().severe(TextUtils.color("&cMySQL can not be connected!"));
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			if (this.hasConnection()) {
				this.conn.close();
			}
		} catch (final SQLException e) {
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