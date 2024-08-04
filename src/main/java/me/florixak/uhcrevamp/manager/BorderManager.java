package me.florixak.uhcrevamp.manager;

import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;

public class BorderManager {

	private WorldBorder wb;

	public BorderManager() {
	}

	public void setBorder() {
		wb = Bukkit.getWorld(GameValues.WORLD_NAME).getWorldBorder();
		wb.setCenter(0, 0);
		wb.setSize(GameValues.BORDER.INIT_SIZE);
		wb.setDamageAmount(GameValues.BORDER.BORDER_DAMAGE);
	}

	public void removeBorder() {
		wb.setSize(0);
	}

	public void setSize(final double size) {
		wb.setSize(size);
	}

	public void setSize(final double size, final int countdown) {
		wb.setSize(size, countdown);
	}

	public double getSize() {
		return wb.getSize();
	}

	public double getMaxSize() {
		return GameValues.BORDER.INIT_SIZE;
	}

//	public double getSpeed() {
//		return GameValues.BORDER.BORDER_SPEED;
//	}

	public boolean exists() {
		return wb != null && wb.getSize() == getMaxSize();
	}

	public boolean isInBorder(final Location loc) {
		if (loc.getX() > getSize() && loc.getZ() > getSize()) return true;
		return false;
	}

	public void checkBorder() {
		if (!exists()) setBorder();
	}

	public void startShrinking(final double size, final int countdown) {
		setSize(size, countdown);
	}
}