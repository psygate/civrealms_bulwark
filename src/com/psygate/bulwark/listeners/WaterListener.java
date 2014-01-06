package com.psygate.bulwark.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import com.psygate.bulwark.BulwarkPlugin;
import com.psygate.bulwark.entity.Bulwark;
import com.untamedears.citadel.Citadel;
import com.untamedears.citadel.entity.Faction;
import com.untamedears.citadel.entity.IReinforcement;
import com.untamedears.citadel.entity.PlayerReinforcement;

/**
 * 
 * @author psygate
 * 
 *         Water listener, if on, prevents lava placing and spreading inside
 *         bulwarks.
 * 
 */
public class WaterListener implements Listener {
	private Material replace;
	private boolean on = true;

	/**
	 * Constructs a WaterListener that prevents water from spreading into
	 * bulwarks.
	 * 
	 * @param material
	 *            Is used as a replacement to prevent water from spreading into
	 *            a bulwark.
	 * @param on
	 *            Determines if the listener will act on water placement /
	 *            spreading.
	 */
	public WaterListener(Material valueOf, boolean on) {
		replace = valueOf;
		this.on = on;
	}

	/**
	 * Prevents player from emptying water buckets inside a bulwark.
	 * 
	 * @param ev
	 *            {@link PlayerBucketEmptyEvent}
	 */
	@EventHandler(ignoreCancelled = true)
	public void waterBucket(PlayerBucketEmptyEvent ev) {
		if (!on) {
			return;
		}
		if (ev.getBucket().equals(Material.WATER_BUCKET)) {
			List<Bulwark> bastlist = BulwarkPlugin.getDB().getContaining(ev.getBlockClicked());
			for (Bulwark bulwark : bastlist) {
				IReinforcement reinf = Citadel.getReinforcementManager().getReinforcement(bulwark.getLocation());
				if (reinf instanceof PlayerReinforcement) {
					PlayerReinforcement re = (PlayerReinforcement) reinf;
					Faction owner = re.getOwner();
					String mem = ev.getPlayer().getName();

					// User must be member, moderator or owner of bulwark
					// reinforced
					// block.
					boolean can = owner.isMember(mem) || owner.isModerator(mem) || owner.isFounder(mem);

					if (!can) {
						ev.setCancelled(true);
						ev.getPlayer().sendMessage(ChatColor.RED + "You cannot place that here.");
						break;
					}
				}
			}
		}
	}

	/**
	 * Prevents water from spreading into a bulwark by setting water to
	 * stationary water.
	 * 
	 * @param ev
	 *            {@link BlockFromToEvent}
	 */
	@EventHandler(ignoreCancelled = true)
	public void waterSpread(BlockFromToEvent ev) {
		if (!on) {
			return;
		}
		if (ev.getBlock().getType().equals(Material.WATER) || ev.getBlock().getType().equals(Material.STATIONARY_WATER)) {
			List<Bulwark> bastlist = BulwarkPlugin.getDB().getContaining(ev.getToBlock());
			if (!bastlist.isEmpty()) {
				ev.setCancelled(true);
				ev.getBlock().setType(Material.STATIONARY_WATER);
				// ev.getBlock().setType(replace);
			}
		}
	}

	/**
	 * Calling this toggles the enabled boolean on or off. True -> on, false ->
	 * off
	 * 
	 * On -> water is stopped Off -> water is allowed to spread / being placed.
	 * 
	 * @return New state of the listener on off boolean.
	 */
	public boolean toggleOn() {
		on = !on;
		return on;
	}
}
