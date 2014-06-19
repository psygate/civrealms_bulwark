package com.psygate.bulwark.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockSpreadEvent;
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
 *         Lava listener, if on, prevents lava placing and spreading inside
 *         bulwarks.
 * 
 */
public class LavaListener implements Listener {
	private Material replace;
	private boolean on;

	/**
	 * Constructs a LavaListener that prevents lava from spreading into
	 * bulwarks.
	 * 
	 * @param material
	 *            Is used as a replacement to prevent lava from spreading into a
	 *            bulwark.
	 * @param on
	 *            Determines if the listener will act on lava placement /
	 *            spreading.
	 */
	public LavaListener(Material material, boolean on) {
		replace = material;
		this.on = on;
	}

	/**
	 * Prevents player from emptying lava buckets inside a bulwark.
	 * 
	 * @param ev
	 *            {@link PlayerBucketEmptyEvent}
	 */
	@EventHandler(ignoreCancelled = true)
	public void lavaBucket(PlayerBucketEmptyEvent ev) {
		if (!on) {
			return;
		}
		if (ev.getBucket().equals(Material.LAVA_BUCKET)) {
			List<? extends Bulwark> bastlist = BulwarkPlugin.getDB().getContaining(ev.getBlockClicked());
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
	 * Prevents lava from spreading into a bulwark by setting lava to stationary
	 * lava.
	 * 
	 * @param ev
	 *            {@link BlockFromToEvent}
	 */
	@EventHandler(ignoreCancelled = true)
	public void lavaSpread(BlockFromToEvent ev) {
		if (!on) {
			return;
		}
		if (ev.getBlock().getType().equals(Material.LAVA) || ev.getBlock().getType().equals(Material.STATIONARY_LAVA)) {
			List<? extends Bulwark> bastlist = BulwarkPlugin.getDB().getContaining(ev.getToBlock());
			if (!bastlist.isEmpty()) {
				ev.setCancelled(true);
				ev.getBlock().setType(Material.STATIONARY_LAVA);
				// ev.getBlock().setType(replace);
			}
		}
	}

	/**
	 * Calling this toggles the enabled boolean on or off. True -> on, false ->
	 * off
	 * 
	 * On -> Lava is stopped Off -> Lava is allowed to spread / being placed.
	 * 
	 * @return New state of the listener on off boolean.
	 */
	public boolean toggleOn() {
		on = !on;
		return on;
	}
}
