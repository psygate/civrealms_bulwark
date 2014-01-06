package com.psygate.bulwark;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.psygate.bulwark.db.DBLayer;
import com.psygate.bulwark.entity.Bulwark;
import com.psygate.bulwark.entity.Configuration;
import com.psygate.bulwark.listeners.BulwarkListener;
import com.psygate.bulwark.listeners.LavaListener;
import com.psygate.bulwark.listeners.NoPearlListener;
import com.psygate.bulwark.listeners.ReinforceListener;
import com.psygate.bulwark.listeners.WaterListener;
import org.bukkit.command.CommandExecutor;

/**
 * 
 * @author psygate
 * 
 *         Main bulwark plugin class. Handles everything.
 * 
 */
public class BulwarkPlugin extends JavaPlugin {
	private static BulwarkPlugin plugin;
	private DBLayer db;
	private BulwarkListener ct;
	private ReinforceListener rl;
	private LavaListener ll;
	private WaterListener wl;
	private NoPearlListener np;
	private CommandExecutor acom;

	@Override
	public void onEnable() {
		try {
			getDatabase().find(Bulwark.class).findRowCount();
		} catch (Exception e) {
			installDDL();
		}
		plugin = this;
		db = new DBLayer(getDatabase());
		// This doesn't do much atm, need to fix the mechanic first.
		Material repmat = Material.AIR; // Material.valueOf(getConfig().getString("replace_material").toUpperCase());

		ct = new BulwarkListener(new Configuration(this), getConfig().getBoolean("bulwark_creation"));
		rl = new ReinforceListener(getConfig().getBoolean("no_reinforce"));
		ll = new LavaListener(repmat, getConfig().getBoolean("no_lava"));
		wl = new WaterListener(repmat, getConfig().getBoolean("no_water"));
		np = new NoPearlListener(getConfig().getBoolean("allow_group_pearl"), getConfig().getBoolean("no_pearl"));

		regListener(ct);
		regListener(rl);
		regListener(ll);
		regListener(wl);
		regListener(np);

		acom = new AdminCommandExecutor(this);
		setExecutor("bwtoggle", acom);
		setExecutor("bwtoggle_no_reinforce", acom);
		setExecutor("bwtoggle_no_lava", acom);
		setExecutor("bwtoggle_no_water", acom);
		setExecutor("bwtoggle_no_pearl", acom);
		setExecutor("bwtoggle_group_pearl", acom);

	}

	private void setExecutor(String commandString, CommandExecutor executor) {
		getCommand(commandString).setExecutor(executor);
	}

	/**
	 * Toggles the citadel listener on or off.
	 * 
	 * @return New state of the citadel listener.
	 */
	public boolean toggleCitadel() {
		boolean state = ct.toggleOn();
		getConfig().set("bulwark_creation", state);
		saveConfig();
		return state;
	}

	/**
	 * Toggles the reinforcement listener on or off.
	 * 
	 * @return New state of the reinforcement listener.
	 */
	public boolean toggleReinforcement() {
		boolean state = rl.toggleOn();
		getConfig().set("no_reinforce", state);
		saveConfig();
		return state;
	}

	/**
	 * Toggles the lava listener on or off.
	 * 
	 * @return New state of the lava listener.
	 */
	public boolean toggleLava() {
		boolean state = ll.toggleOn();
		getConfig().set("no_lava", state);
		saveConfig();
		return state;
	}

	/**
	 * Toggles the water listener on or off.
	 * 
	 * @return New state of the water listener.
	 */
	public boolean toggleWater() {
		boolean state = wl.toggleOn();
		getConfig().set("no_water", state);
		saveConfig();
		return state;
	}

	/**
	 * Toggles the pearl listener on or off.
	 * 
	 * @return New state of the pearl listener.
	 */
	public boolean togglePearl() {
		boolean state = np.toggleOn();
		getConfig().set("no_pearl", state);
		saveConfig();
		return state;
	}

	/**
	 * Toggles the group pearl listener on or off.
	 * 
	 * @return New state of the group pearl listener.
	 */
	public boolean toggleGroup() {
		boolean state = np.toggleGroup();
		getConfig().set("allow_group_pearl", state);
		saveConfig();
		return state;
	}

	private void regListener(Listener list) {
		getServer().getPluginManager().registerEvents(list, this);
	}

	/**
	 * Returns the database layer. If the database isnt available, this may be
	 * null.
	 * 
	 * @return Database layer
	 */
	public static DBLayer getDB() {
		return plugin.db;
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Bulwark.class);

		return classes;
	}

}
