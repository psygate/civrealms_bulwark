package com.psygate.bulwark.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.psygate.bulwark.BulwarkPlugin;
import com.psygate.bulwark.entity.Bulwark;
import com.psygate.bulwark.entity.Configuration;
import com.psygate.bulwark.event.BulwarkCreateEvent;
import com.untamedears.citadel.Citadel;
import com.untamedears.citadel.entity.IReinforcement;
import com.untamedears.citadel.entity.PlayerReinforcement;
import com.untamedears.citadel.events.CreateReinforcementEvent;

/**
 * 
 * @author psygate
 * 
 *         This is the listener which creates the bulwarks if a block is citadel
 *         reinforced.
 */
public class BulwarkListener implements Listener {
	private Configuration conf;
	private boolean on;

	/**
	 * Creates a new Bulwark listener.
	 * 
	 * @param conf
	 *            Configuration the sets the material and size of new bulwarks.
	 * @param on
	 *            Determines if new bulwarks can be created or not.
	 */
	public BulwarkListener(Configuration conf, boolean on) {
		this.conf = conf;
		this.on = on;
	}

	/**
	 * Catches a citadel reinforcement event, and if the material determined by
	 * {@link Configuration} is reinforced, a new {@link BulwarkCreateEvent} is
	 * fired.
	 * 
	 * @param ev
	 *            {@link CreateReinforcementEvent}
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void reinforcementEvent(CreateReinforcementEvent ev) {
		if (!on) {
			return;
		}
		if (ev.getBlock().getType().equals(conf.getBulwarkMaterial())) {
			Bukkit.getServer().getPluginManager()
					.callEvent(new BulwarkCreateEvent(ev.getBlock(), ev.getPlayer(), ev.getReinforcement()));
		}
	}

	/**
	 * Creates a new bulwark if a {@link BulwarkCreateEvent} is received.
	 * 
	 * @param ev
	 *            {@link BulwarkCreateEvent}
	 */
	@EventHandler(ignoreCancelled = true)
	public void bulwarkCreate(BulwarkCreateEvent ev) {
		if (!on) {
			return;
		}
		Bulwark bast = new Bulwark(conf.getSize(), ev.getBlock());
		List<Bulwark> interl = BulwarkPlugin.getDB().getIntersecting(bast);

		for (Bulwark inters : interl) {
			IReinforcement reinf = Citadel.getReinforcementManager().getReinforcement(inters.getLocation());
			IReinforcement cur = ev.getReinforcement();

			if (reinf instanceof PlayerReinforcement && cur instanceof PlayerReinforcement) {
				PlayerReinforcement pr = (PlayerReinforcement) reinf;
				PlayerReinforcement cr = (PlayerReinforcement) cur;

				if (!pr.getOwner().equals(cr.getOwner())) {
					ev.getPlayer().sendMessage(
							ChatColor.RED
									+ "Bulwark cannot intersect with other bulwarks who do not share the same owner.");
					ev.setCancelled(true);
					return;
				}
			}
		}

		BulwarkPlugin.getDB().persist(bast);
		ev.getPlayer().sendMessage(ChatColor.GREEN + "Bulwark created, radius of " + conf.getSize() + " set.");
	}

	/**
	 * Listenes if a block is broken, if that block is a bulwark block, that
	 * bulwark is destroyed.
	 * 
	 * @param ev
	 */
	@EventHandler(ignoreCancelled = true)
	public void bulwarkBreak(BlockBreakEvent ev) {
		Bulwark bw = BulwarkPlugin.getDB().get(ev.getBlock());
		if (bw != null) {
			BulwarkPlugin.getDB().delete(bw);
		}
	}

	/**
	 * Toggles creation of new bulwarks on or off.
	 * 
	 * @return New state of the creation boolean.
	 *         <ul>
	 *         <li>True -> new bulwarks can be</li>
	 *         created.
	 *         <li>False -> no new bulwarks can be created.</li>
	 *         </ul>
	 */
	public boolean toggleOn() {
		on = !on;
		return on;
	}
}
