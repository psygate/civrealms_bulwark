package com.psygate.bulwark.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.psygate.bulwark.BulwarkPlugin;
import com.psygate.bulwark.entity.Bulwark;
import com.psygate.bulwark.entity.MitigatingBulwark;
import com.untamedears.citadel.Citadel;
import com.untamedears.citadel.entity.Faction;
import com.untamedears.citadel.entity.IReinforcement;
import com.untamedears.citadel.entity.PlayerReinforcement;

public class MitigationListener implements Listener {
	private boolean enabled;

	public MitigationListener(boolean enabled) {
		this.enabled = enabled;
	}

	@EventHandler(ignoreCancelled = true)
	public void damage(EntityDamageByEntityEvent ev) {
		if (!enabled) {
			return;
		}
		if (ev.getEntity() instanceof Player) {
			Player damaged = (Player) ev.getEntity();
			List<? extends MitigatingBulwark> list = BulwarkPlugin.getDB()
					.getContaining(damaged.getLocation().getBlock());
			for (MitigatingBulwark bw : list) {
				if (groupCheck(bw, damaged.getName())) {
					double dmg = ev.getDamage();
					double mitigation = bw.calcMitigation();
					double newdmg = dmg - dmg * mitigation;
					ev.setDamage(newdmg);
					return;
				}
			}
		}
	}

	private boolean groupCheck(Bulwark bulw, String memname) {
		IReinforcement reinf = Citadel.getReinforcementManager()
				.getReinforcement(bulw.getLocation());
		if (reinf instanceof PlayerReinforcement) {
			Faction owner = ((PlayerReinforcement) reinf).getOwner();
			boolean can = owner.isMember(memname) || owner.isModerator(memname)
					|| owner.isFounder(memname);

			return can;
		}

		return false;
	}

	public boolean toggle() {
		enabled = !enabled;
		return enabled;
	}
}
