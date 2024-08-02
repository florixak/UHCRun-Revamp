package me.florixak.uhcrevamp.game.customDrop;

import me.florixak.uhcrevamp.game.GameManager;
import me.florixak.uhcrevamp.utils.MathUtils;
import me.florixak.uhcrevamp.utils.XSeries.XMaterial;
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
import java.util.Map;

public class CustomDrop {

	private final Material material;
	private final EntityType entityType;
	private final List<String> drops;
	private Map<String, Integer> durabilityMap;
	private final int minAmount;
	private final int maxAmount;
	private final int exp;

	public CustomDrop(Material material,
					  List<String> drops,
					  Map<String, Integer> durabilityMap,
					  int minAmount,
					  int maxAmount,
					  int exp) {
		this.material = material;
		this.entityType = null;
		this.drops = drops;
		this.durabilityMap = durabilityMap;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.exp = exp;
	}

	public CustomDrop(EntityType entityType,
					  List<String> drops,
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

	public List<String> getDrops() {
		return this.drops;
	}

	public void addDrop(String drop) {
		this.drops.add(drop);
	}

	public boolean hasDurability(Material drop) {
		return this.durabilityMap.containsKey(drop.name());
	}

	public int getDurability(Material drop) {
		return durabilityMap.get(drop.name());
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

	public void dropItem(BlockBreakEvent breakEvent) {
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
		try {
			List<String> drops = getDrops();
			Material dropMaterial = XMaterial.matchXMaterial(drops.get(MathUtils.getRandom().nextInt(drops.size()))).get().parseMaterial();
			if (dropMaterial != XMaterial.AIR.parseMaterial()) {
				int amount = MathUtils.randomInteger(getMinAmount(), getMaxAmount());
				ItemStack drop = new ItemStack(dropMaterial, amount);
				if (hasDurability(dropMaterial)) {
					drop.setDurability((short) getDurability(dropMaterial));
//					Bukkit.getLogger().info("Dropped item with durability: " + getDurability(dropMaterial));
				}
				Location location = loc.add(0.5, 0.5, 0.5);
				Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, drop);
			}
		} catch (NullPointerException e) {
			Bukkit.getLogger().info("Failed to drop item from " + getMaterial().name());
		}

	}
}
