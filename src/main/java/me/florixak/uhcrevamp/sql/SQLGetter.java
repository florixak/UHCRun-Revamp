package me.florixak.uhcrevamp.sql;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SQLGetter {

	private final Connection conn;
	private final String table;

	public SQLGetter(GameManager gameManager) {
		this.conn = gameManager.getSQL().getConnection();

		this.table = "uhcrevamp";
		createTable();
	}

	public void createTable() {
		PreparedStatement ps;

		int firstUHCLevel = GameValues.STATISTICS.FIRST_UHC_LEVEL;
		double firstRequiredUHCExp = GameValues.STATISTICS.FIRST_REQUIRED_EXP;

		try {
			ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " "
					+ "(uuid VARCHAR(100) PRIMARY KEY,"
					+ "name VARCHAR(100),"
					+ "money DECIMAL(24,2) DEFAULT 0,"
					+ "uhc_level INT(100) DEFAULT " + firstUHCLevel + ","
					+ "uhc_exp DECIMAL(24,2) DEFAULT 0,"
					+ "required_uhc_exp DECIMAL(24,2) DEFAULT " + firstRequiredUHCExp + ","
					+ "wins INT(100) DEFAULT 0,"
					+ "losses INT(100) DEFAULT 0,"
					+ "kills INT(100) DEFAULT 0,"
					+ "killstreak INT(100) DEFAULT 0,"
					+ "assists INT(100) DEFAULT 0,"
					+ "deaths INT(100) DEFAULT 0,"
					+ "displayed_top VARCHAR(100) DEFAULT 'Wins',"
					+ "kits VARCHAR(100) DEFAULT '',"
					+ "perks VARCHAR(100) DEFAULT '')"
			);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createPlayer(Player p) {
		try {
			UUID uuid = p.getUniqueId();

			if (!exists(uuid)) {
				PreparedStatement ps2 = conn.prepareStatement("INSERT IGNORE INTO " + table + " (uuid,name) VALUES (?,?)");
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

	public String getName(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT name FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setMoney(UUID uuid, double money) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET money=? WHERE uuid=?");
			ps.setDouble(1, money);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public double getMoney(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT money FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble("money");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.00;
	}

	public void addUHCLevel(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET uhc_level=? WHERE uuid=?");
			ps.setInt(1, getUHCLevel(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getUHCLevel(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT uhc_level FROM " + table + " WHERE uuid=?");
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

	public void addUHCExp(UUID uuid, double exp) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET uhc_exp=? WHERE uuid=?");
			ps.setDouble(1, getUHCExp(uuid) + exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public double getUHCExp(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT uhc_exp FROM " + table + " WHERE uuid=?");
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
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET uhc_exp=? WHERE uuid=?");
			ps.setDouble(1, exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public double getRequiredUHCExp(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT required_uhc_exp FROM " + table + " WHERE uuid=?");
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
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET required_uhc_exp=? WHERE uuid=?");
			ps.setDouble(1, exp);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addWin(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET wins=? WHERE uuid=?");
			ps.setInt(1, getWins(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getWins(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT wins FROM " + table + " WHERE uuid=?");
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

	public void addLose(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET losses=? WHERE uuid=?");
			ps.setInt(1, getLosses(uuid) + 1);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getLosses(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT losses FROM " + table + " WHERE uuid=?");
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

	public void addKill(UUID uuid, int kills) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET kills=? WHERE uuid=?");
			ps.setInt(1, getKills(uuid) + kills);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getKills(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT kills FROM " + table + " WHERE uuid=?");
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

	public void setKillstreak(UUID uuid, int killstreak) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET killstreak=? WHERE uuid=?");
			ps.setInt(1, killstreak);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getKillstreak(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT killstreak FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("killstreak");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addAssist(UUID uuid, int assists) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET assists=? WHERE uuid=?");
			ps.setInt(1, getAssists(uuid) + assists);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getAssists(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT assists FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("assists");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setAssists(UUID uuid, int assists) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET assists=? WHERE uuid=?");
			ps.setInt(1, assists);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addDeath(UUID uuid, int deaths) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET deaths=? WHERE uuid=?");
			ps.setInt(1, getDeaths(uuid) + deaths);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getDeaths(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT deaths FROM " + table + " WHERE uuid=?");
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
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET deaths=? WHERE uuid=?");
			ps.setInt(1, deaths);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getDisplayedTop(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT displayed_top FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("displayed_top");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "wins";
	}

	public void setDisplayedTop(UUID uuid, String topMode) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET displayed_top=? WHERE uuid=?");
			ps.setString(1, topMode);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> getBoughtKits(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT kits FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String kits = rs.getString("kits");
				List<String> kitsList = Arrays.asList(kits.split(", "));
//                Bukkit.getLogger().info(kitsList.toString());
				return kitsList;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void addBoughtKit(UUID uuid, String kit) {
		List<String> kits = getBoughtKits(uuid);
		if (!kits.contains(kit)) {
			kits.add(kit);
			setBoughtKits(uuid, String.join(",", kits));
		}
	}

	public void setBoughtKits(UUID uuid, String kits) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET kits=? WHERE uuid=?");
			ps.setString(1, kits);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> getBoughtPerks(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT perks FROM " + table + " WHERE uuid=?");
			ps.setString(1, uuid.toString());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String perks = rs.getString("perks");
				List<String> perksList = Arrays.asList(perks.split(", "));
//                Bukkit.getLogger().info(perksList.toString());
				return perksList;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void addBoughtPerk(UUID uuid, String perk) {
		List<String> perks = getBoughtPerks(uuid);
		if (!perks.contains(perk)) {
			perks.add(perk);
			setBoughtPerks(uuid, String.join(",", perks));
		}
	}

	public void setBoughtPerks(UUID uuid, String perks) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + table + " SET perks=? WHERE uuid=?");
			ps.setString(1, perks);
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void emptyTable() {
		try {
			PreparedStatement ps = conn.prepareStatement("TRUNCATE " + table);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeUUID(UUID uuid) {
		try {
			PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE 'uuid'=?");
			ps.setString(1, uuid.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}