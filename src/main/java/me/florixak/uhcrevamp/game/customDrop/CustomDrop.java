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

	public CustomDrop(final Material material,
					  final List<String> drops,
					  final Map<String, Integer> durabilityMap,
					  final int minAmount,
					  final int maxAmount,
					  final int exp) {
		this.material = material;
		this.entityType = null;
		this.drops = drops;
		this.durabilityMap = durabilityMap;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.exp = exp;
	}

	public CustomDrop(final EntityType entityType,
					  final List<String> drops,
					  final int minAmount,
					  final int maxAmount,
					  final int exp) {
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

	public void addDrop(final String drop) {
		this.drops.add(drop);
	}

	public Map<String, Integer> getDurabilityMap() {
		return durabilityMap;
	}

	public boolean hasDurability(final Material drop) {
		return this.durabilityMap.containsKey(drop.name());
	}

	public int getDurability(final Material drop) {
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

	public void dropItem(final BlockBreakEvent breakEvent) {
		if (breakEvent != null) {
			final Player p = breakEvent.getPlayer();
			final Block block = breakEvent.getBlock();
			final Location loc = block.getLocation();

			final int exp = getExp();
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

	public void dropMobItem(final EntityDeathEvent deathEvent) {
		if (deathEvent != null) {
			final Player p = deathEvent.getEntity().getKiller();
			final Entity entity = deathEvent.getEntity();
			final Location loc = entity.getLocation();

			if (p == null) return;

			if (hasDrops()) {
				deathEvent.getDrops().clear();
				deathEvent.setDroppedExp(0);
				p.giveExp(getExp());

				final List<String> drops = getDrops();
				final Material dropMaterial = XMaterial.matchXMaterial(drops.get(MathUtils.getRandom().nextInt(drops.size()))).get().parseMaterial();
				if (dropMaterial != XMaterial.AIR.parseMaterial()) {
					final int amount = MathUtils.randomInteger(getMinAmount(), getMaxAmount());
					final ItemStack drop = new ItemStack(dropMaterial, amount);
					final Location location = loc.add(0.5, 0.5, 0.5);
					Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, drop);
				}
			}
		}
	}

	private void dropItem(final Location loc) {
		try {
			final List<String> drops = getDrops();
			final Material dropMaterial = XMaterial.matchXMaterial(drops.get(MathUtils.getRandom().nextInt(drops.size()))).get().parseMaterial();
			if (dropMaterial != XMaterial.AIR.parseMaterial()) {
				final int amount = MathUtils.randomInteger(getMinAmount(), getMaxAmount());
				final ItemStack drop = new ItemStack(dropMaterial, amount);
				if (hasDurability(dropMaterial)) {
					drop.setDurability((short) getDurability(dropMaterial));
//					Bukkit.getLogger().info("Dropped item with durability: " + getDurability(dropMaterial));
				}
				final Location location = loc.add(0.5, 0.5, 0.5);
				Bukkit.getWorld(loc.getWorld().getName()).dropItem(location, drop);
			}
		} catch (final NullPointerException e) {
			Bukkit.getLogger().info("Failed to drop item from.");
		}

	}
}
