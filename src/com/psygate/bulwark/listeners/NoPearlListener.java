package com.psygate.bulwark.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

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
 *         Listener checking for pearl teleports.
 */
public class NoPearlListener implements Listener {
	private boolean grouppearl;
	private boolean on;

	/**
	 * Creates a new NoPearlListener.
	 * 
	 * @param grouppearl
	 *            Allow members of a citadel group to pearl through a bulwark
	 *            bounding box (true -> allow, false -> prevent).
	 * @param on
	 *            Sets the listener to on or off (true -> on, false -> off)
	 */
	public NoPearlListener(boolean grouppearl, boolean on) {
		this.grouppearl = grouppearl;
		this.on = on;
	}

	/**
	 * Checks for player teleports by pearling.
	 * 
	 * @param ev
	 *            {@link PlayerTeleportEvent}
	 */
	@EventHandler(ignoreCancelled = true)
	public void pearlPort(PlayerTeleportEvent ev) {
		if (!on) {
			return;
		}
		if (ev.getCause().equals(TeleportCause.ENDER_PEARL)) {
			float ax = (float) ev.getFrom().getX();
			float ay = (float) ev.getFrom().getY() - 2; // Compensate for direct
														// trajectories
			float az = (float) ev.getFrom().getZ();

			float bx = (float) ev.getTo().getX();
			float by = (float) ev.getTo().getY() + 2; // Compensate for direct
														// trajectories
			float bz = (float) ev.getTo().getZ();

			World w = ev.getFrom().getWorld();

			float dx = bx - ax;
			float dy = by - ay;
			float dz = bz - az;
			float len = (float) Math.sqrt((dx * dx + dy * dy + dz * dz));

			dx /= len;
			dy /= len;
			dz /= len;

			float a, b;
			float min, max;
			float divx = 1 / dx;
			float divy = 1 / dy;
			float divz = 1 / dz;

			// drawBB((int) ax, (int) ay, (int) az, (int) bx, (int) by, (int)
			// bz, w, ev.getPlayer());

			List<? extends Bulwark> warks = BulwarkPlugin.getDB().getIntersecting(ax, ay, az, bx, by, bz, w);

			// ev.getPlayer().sendMessage("Pearling through " + warks.size() +
			// " Bulwarks.");

			for (Bulwark bulw : warks) {
				// Ray intersection with bounding box
				if (bulw.containsLocation(ev.getFrom()) || bulw.containsLocation(ev.getTo())) {
					if (!groupCheck(bulw, ev.getPlayer().getName())) {
						ev.setCancelled(true);
						ev.getPlayer().sendMessage(ChatColor.RED + "You cannot teleport here.");
						break;
					}
				} else {
					a = (bulw.getLbx() - ax) * divx;
					b = (bulw.getUbx() - ax) * divx;
					if (a < b) {
						min = a;
						max = b;
					} else {
						min = b;
						max = a;
					}

					a = (bulw.getLby() - ay) * divy;
					b = (bulw.getUby() - ay) * divy;

					if (a > b) {
						float t = a;
						a = b;
						b = t;
					}

					if (a > min)
						min = a;
					if (b < max)
						max = b;

					a = (bulw.getLbz() - az) * divz;
					b = (bulw.getUbz() - az) * divz;

					if (a > b) {
						float t = a;
						a = b;
						b = t;
					}

					if (a > min)
						min = a;
					if (b < max)
						max = b;

					if (max >= 0 && max >= min) {
						if (!groupCheck(bulw, ev.getPlayer().getName())) {
							ev.setCancelled(true);
							ev.getPlayer().sendMessage(ChatColor.RED + "You cannot teleport here.");
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Checks if a user throwing a pearl is a member of a bulwark group.
	 * 
	 * @param bulw
	 *            The bulwark to check against.
	 * @param memname
	 *            Member name to check for.
	 * @return True -> memname is member of bulwark citadle group, False ->
	 *         memname is not a member.
	 */
	private boolean groupCheck(Bulwark bulw, String memname) {
		if (grouppearl) {
			IReinforcement reinf = Citadel.getReinforcementManager().getReinforcement(bulw.getLocation());
			if (reinf instanceof PlayerReinforcement) {
				Faction owner = ((PlayerReinforcement) reinf).getOwner();
				boolean can = owner.isMember(memname) || owner.isModerator(memname) || owner.isFounder(memname);

				return can;
			}
		}

		return false;
	}

	/**
	 * Toggles the listener on off.
	 * @return The new state of the listener. True -> listener is acting on event, False -> Listener is dormant.
	 */
	public boolean toggleOn() {
		on = !on;
		return on;
	}

	/**
	 * Toggles the group pearling permisison on off.
	 * 
	 * @return The new state of the group pearling. True -> listener is acting
	 *         on event, False -> Listener is dormant.
	 */
	public boolean toggleGroup() {
		grouppearl = !grouppearl;
		return grouppearl;
	}
	
	@Deprecated
	@SuppressWarnings("deprecation")
	private void drawBoundingBox(int ax, int ay, int az, int bx, int by, int bz, World w, Player player) {

		Location l;
		for (int i = min(ax, bx); i < max(ax, bx); i++) {

			l = new Location(w, i, ay, az);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);

			l = new Location(w, i, ay, bz);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);

			l = new Location(w, i, by, az);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);

			l = new Location(w, i, by, bz);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);
		}

		for (int i = min(ay, by); i < max(ay, by); i++) {
			l = new Location(w, ax, i, az);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);

			l = new Location(w, ax, i, bz);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);

			l = new Location(w, bx, i, az);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);

			l = new Location(w, bx, i, bz);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);
		}

		for (int i = min(az, bz); i < max(az, bz); i++) {
			l = new Location(w, ax, ay, i);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);

			l = new Location(w, ax, by, i);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);

			l = new Location(w, bx, ay, i);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);

			l = new Location(w, bx, by, i);
			player.sendBlockChange(l, Material.GLOWSTONE, (byte) 0);
		}
	}

	private int max(int a, int b) {
		return (a > b) ? a : b;
	}

	private int min(int a, int b) {
		return (a < b) ? a : b;
	}
}
