package com.psygate.bulwark.listeners;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.psygate.bulwark.BulwarkPlugin;
import com.psygate.bulwark.entity.Bulwark;
import com.untamedears.citadel.Citadel;
import com.untamedears.citadel.entity.Faction;
import com.untamedears.citadel.entity.IReinforcement;
import com.untamedears.citadel.entity.PlayerReinforcement;
import com.untamedears.citadel.events.CreateReinforcementEvent;

/**
 * 
 * @author psygate
 * 
 *         Listenes for unauthorized citadel reinforcements inside a bulwark.
 */
public class ReinforceListener implements Listener {
	private boolean on;

	/**
	 * Constructs the listener.
	 * 
	 * @param on
	 *            Determines if the listener is on or off.
	 */
	public ReinforceListener(boolean on) {
		this.on = on;
	}

	/**
	 * Prevents the reinforcement of blocks inside a bulwark, if the placer is
	 * not a member of the bulwark group.
	 * 
	 * @param ev {@link CreateReinforcementEvent}
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void reinforce(CreateReinforcementEvent ev) {
		if (!on) {
			return;
		}
		List<? extends Bulwark> basts = BulwarkPlugin.getDB().getContaining(ev.getBlock());
		for (Bulwark bast : basts) {
			IReinforcement reinf = Citadel.getReinforcementManager().getReinforcement(bast.getLocation());
			if (reinf instanceof PlayerReinforcement) {
				PlayerReinforcement re = (PlayerReinforcement) reinf;
				Faction owner = re.getOwner();
				String mem = ev.getPlayer().getName();

				// User must be member, moderator or owner of bulwark reinforced
				// block.
				boolean can = owner.isMember(mem) || owner.isModerator(mem) || owner.isFounder(mem);

				if (!can) {
					ev.setCancelled(true);
					ev.getPlayer().sendMessage(ChatColor.RED + "You cannot reinforce here.");
					break;
				}
			}
		}
	}

	/**
	 * Toggles the listener on off.
	 * 
	 * @return The new state of the listener. True -> listener is acting on
	 *         event, False -> Listener is dormant.
	 */
	public boolean toggleOn() {
		on = !on;
		return on;
	}
}
