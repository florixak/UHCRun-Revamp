package me.florixak.uhcrevamp.game.customDrop;

import me.florixak.uhcrevamp.UHCRevamp;
import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.utils.MathUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
import me.florixak.uhcrevamp.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomDrop {

	private final Material material;
	private final EntityType entityType;
	private final List<Material> drops;
	private final int minAmount;
	private final int maxAmount;
	private final int exp;

	public CustomDrop(Material material,
					  List<Material> drops,
					  int minAmount,
					  int maxAmount,
					  int exp) {
		this.material = material;
		this.entityType = null;
		this.drops = drops;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.exp = exp;
	}

	public CustomDrop(EntityType entityType,
					  List<Material> drops,
					  int minAmount,
					  int maxAmount,
					  int exp) {
		this.entityType = entityType;
		this.material = null;
		this.drops = drops;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.exp = exp;
	}

	public Material getMaterial() {
		if (material == null) return null;
		return XMaterial.matchXMaterial(material).parseMaterial();
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public boolean hasDrops() {
		return !getDrops().isEmpty() && getDrops() != null;
	}

	public List<Material> getDrops() {
		return this.drops;
	}

	public int getExp() {
		return this.exp;
	}

	public int getMinAmount() {
		return this.minAmount;
	}

	public int getMaxAmount() {
		return this.maxAmount;
	}

	public void dropBlockItem(BlockBreakEvent breakEvent) {
		if (breakEvent != null) {
			Player p = breakEvent.getPlayer();
			Block block = breakEvent.getBlock();
			Location loc = block.getLocation();

			int exp = getExp();
			if (exp > 0) {
				p.giveExp(exp);
				GameManager.getGameManager().getSoundManager().playOreDestroySound(p);
			}

			if (hasDrops()) {
				block.setType(XMaterial.AIR.parseMaterial());
				block.getDrops().clear();
				breakEvent.setExpToDrop(0);

				dropItem(loc);
			}
		}
	}

	public void dropMobItem(EntityDeathEvent deathEvent) {
		if (deathEvent != null) {
			Player p = deathEvent.getEntity().getKiller();
			Entity entity = deathEvent.getEntity();
			Location loc = entity.getLocation();

			if (p == null) return;

			if (hasDrops()) {
				deathEvent.getDrops().clear();
				deathEvent.setDroppedExp(0);
				p.giveExp(getExp());
				dropItem(loc);
			}
		}
	}

	private void dropItem(Location loc) {
		Material drop = XMaterial.matchXMaterial(getDrops().get(MathUtils.getRandom().nextInt(getDrops().size()))).parseMaterial();
		if (drop != XMaterial.AIR.parseMaterial()) {
			int amount = getMinAmount() == getMaxAmount() ? getMinAmount() : MathUtils.randomInteger(getMinAmount(), getMaxAmount());
			ItemStack dropItem = new ItemStack(drop, amount);
			if (drop == XMaterial.LAPIS_LAZULI.parseMaterial()) {
				VersionUtils versionUtils = UHCRevamp.getInstance().getVersionUtils();
				dropItem = versionUtils.getLapis();
			}
			if (material == XMaterial.REDSTONE_ORE.parseMaterial()) {
				Bukkit.getLogger().info("Redstone ore drop: " + dropItem.toString());
			}
			// int amount = getMinAmount() == getMaxAmount() ? getMinAmount() : getMinAmount() + (int)(Math.random() * ((getMaxAmount()-getMinAmount())+1));

			Location location = loc.add(0.5, 0.5, 0.5);
			Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, dropItem);
		}
	}
}
