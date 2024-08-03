package me.florixak.uhcrevamp.sql;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLGetter {

	private final Connection conn;
	private final String table;

	public SQLGetter(final GameManager gameManager) {
		this.conn = gameManager.getSQL().getConnection();

		this.table = "uhcrevamp";
		createTable();
	}

	public void createTable() {
		final PreparedStatement ps;

		final int firstUHCLevel = GameValues.STATISTICS.FIRST_UHC_LEVEL;
		final double firstRequiredUHCExp = GameValues.STATISTICS.FIRST_REQUIRED_EXP;
		final double startingMoney = GameValues.STATISTICS.STARTING_MONEY;

		try {
			ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " "
					+ "(uuid VARCHAR(100) PRIMARY KEY,"
					+ "name VARCHAR(100),"
					+ "money DECIMAL(24,2) DEFAULT " + startingMoney + ","
					+ "uhc_level INT(100) DEFAULT " + firstUHCLevel + ","
					+ "uhc_exp DECIMAL(24,2) DEFAULT 0,"
					+ "required_uhc_exp DECIMAL(24,2) DEFAULT " + firstRequiredUHCExp + ","
					+ "games_played INT(100) DEFAULT 0,"
					+ "wins INT(100) DEFAULT 0,"
					+ "losses INT(100) DEFAULT 0,"
					+ "kills INT(100) DEFAULT 0,"
					+ "killstreak INT(100) DEFAULT 0,"
					+ "assists INT(100) DEFAULT 0,"
					+ "deaths INT(100) DEFAULT 0,"
					+ "kits VARCHAR(100) DEFAULT '',"
					+ "perks VARCHAR(100) DEFAULT '')"
			);
			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void createPlayer(final Player p) {
		try {
			final UUID uuid = p.getUniqueId();

			if (!exists(uuid)) {
				final PreparedStatement ps2 = conn.prepareStatement("INSERT IGNORE INTO " + table + " (uuid,name) VALUES (?,?)");
				ps2.setString(1, uuid.toString());
				ps2.setString(2, p.getName());
				ps2.executeUpdate();
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean exists(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
			return false;

		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getName(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT name FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setMoney(final UUID uuid, final double money) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET money=? WHERE uuid=?");
			ps.setDouble(1, money);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public double getMoney(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT money FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble("money");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0.00;
	}

	public void addUHCLevel(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET uhc_level=? WHERE uuid=?");
			ps.setInt(1, getUHCLevel(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getUHCLevel(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT uhc_level FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("uhc_level");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addUHCExp(final UUID uuid, final double exp) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET uhc_exp=? WHERE uuid=?");
			ps.setDouble(1, getUHCExp(uuid) + exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public double getUHCExp(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT uhc_exp FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("uhc_exp");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setUHCExp(final UUID uuid, final double exp) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET uhc_exp=? WHERE uuid=?");
			ps.setDouble(1, exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public double getRequiredUHCExp(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT required_uhc_exp FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("required_uhc_exp");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setRequiredUHCExp(final UUID uuid, final double exp) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET required_uhc_exp=? WHERE uuid=?");
			ps.setDouble(1, exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void setGamesPlayed(final UUID uuid, final int gamesPlayed) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET games_played=? WHERE uuid=?");
			ps.setInt(1, gamesPlayed);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getGamesPlayed(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT games_played FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("games_played");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addWin(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET wins=? WHERE uuid=?");
			ps.setInt(1, getWins(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getWins(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT wins FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("wins");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addLose(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET losses=? WHERE uuid=?");
			ps.setInt(1, getLosses(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getLosses(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT losses FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("losses");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addKill(final UUID uuid, final int kills) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET kills=? WHERE uuid=?");
			ps.setInt(1, getKills(uuid) + kills);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getKills(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT kills FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("kills");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setKillstreak(final UUID uuid, final int killstreak) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET killstreak=? WHERE uuid=?");
			ps.setInt(1, killstreak);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getKillstreak(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT killstreak FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("killstreak");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addAssist(final UUID uuid, final int assists) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET assists=? WHERE uuid=?");
			ps.setInt(1, getAssists(uuid) + assists);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getAssists(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT assists FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("assists");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setAssists(final UUID uuid, final int assists) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET assists=? WHERE uuid=?");
			ps.setInt(1, assists);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void addDeath(final UUID uuid, final int deaths) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET deaths=? WHERE uuid=?");
			ps.setInt(1, getDeaths(uuid) + deaths);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int getDeaths(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT deaths FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("deaths");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setDeaths(final UUID uuid, final int deaths) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET deaths=? WHERE uuid=?");
			ps.setInt(1, deaths);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> getBoughtKits(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT kits FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				final String kits = rs.getString("kits");
				final List<String> kitsList = Arrays.asList(kits.split(", "));
//                Bukkit.getLogger().info(kitsList.toString());
				return kitsList;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void addBoughtKit(final UUID uuid, final String kit) {
		final List<String> kits = getBoughtKits(uuid);
		if (!kits.contains(kit)) {
			kits.add(kit);
			setBoughtKits(uuid, String.join(",", kits));
		}
	}

	public void setBoughtKits(final UUID uuid, final String kits) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET kits=? WHERE uuid=?");
			ps.setString(1, kits);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> getBoughtPerks(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("SELECT perks FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				final String perks = rs.getString("perks");
				final List<String> perksList = Arrays.asList(perks.split(", "));
//                Bukkit.getLogger().info(perksList.toString());
				return perksList;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void addBoughtPerk(final UUID uuid, final String perk) {
		final List<String> perks = getBoughtPerks(uuid);
		if (!perks.contains(perk)) {
			perks.add(perk);
			setBoughtPerks(uuid, String.join(",", perks));
		}
	}

	public void setBoughtPerks(final UUID uuid, final String perks) {
		try {
			final PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET perks=? WHERE uuid=?");
			ps.setString(1, perks);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Integer> getTopStatistics(final String... types) {
		final Map<String, Integer> topStatistics = new HashMap<>();
		for (final String type : types) {
			try {
				final PreparedStatement ps = conn.prepareStatement("SELECT name FROM " + table + " ORDER BY " + type + " DESC LIMIT 10");

				final ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					topStatistics.put(rs.getString("name"), rs.getInt(type));
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return topStatistics;
	}


	public void emptyTable() {
		try {
			final PreparedStatement ps = conn.prepareStatement("TRUNCATE " + table);
			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeUUID(final UUID uuid) {
		try {
			final PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE 'uuid'=?");
			ps.setString(1, uuid.toString());
			ps.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}
}