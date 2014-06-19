package com.psygate.bulwark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.CallableSql;
import com.avaje.ebean.Transaction;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.psygate.bulwark.db.DBLayer;
import com.psygate.bulwark.entity.Bulwark;
import com.psygate.bulwark.entity.Configuration;
import com.psygate.bulwark.entity.MitigatingBulwark;
import com.psygate.bulwark.listeners.BulwarkListener;
import com.psygate.bulwark.listeners.LavaListener;
import com.psygate.bulwark.listeners.MitigationListener;
import com.psygate.bulwark.listeners.NoPearlListener;
import com.psygate.bulwark.listeners.ReinforceListener;
import com.psygate.bulwark.listeners.WaterListener;
import com.untamedears.citadel.Citadel;
import com.untamedears.citadel.entity.Faction;
import com.untamedears.citadel.entity.IReinforcement;
import com.untamedears.citadel.entity.PlayerReinforcement;

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
	private MitigationListener ml;
	private CommandExecutor acom;
	public static float minimumMitigation, maximumMitigation;
	public static long maturationTime, maturationStartAfter;

	@Override
	public void onEnable() {
		plugin = this;
		ArrayList<String> singleCreateStmts = getSingleStatements();
		for (String crt : singleCreateStmts) {
			CallableSql stmt = getDatabase().createCallableSql(crt);
			try {
				getDatabase().execute(stmt);
			} catch (Exception e) {
				System.out.println("Non critical table creation error.");
				System.out.println("Table probably already existed.");
			}
		}

		db = new DBLayer(getDatabase());

		if (getDatabase().find(Bulwark.class).findRowCount() > 0) {
			migrate();
		}

		minimumMitigation = getFloat("mitigation.starting_at") / 100f;
		maximumMitigation = getFloat("mitigation.capping_at") / 100f;
		maturationTime = TimeUnit.DAYS
				.toMillis((int) getFloat("mitigation.maturation_time"));
		maturationStartAfter = TimeUnit.DAYS
				.toMillis((int) getFloat("mitigation.mature_after"));

		plugin = this;
		// This doesn't do much atm, need to fix the mechanic first.
		Material repmat = Material.AIR; // Material.valueOf(getConfig().getString("replace_material").toUpperCase());

		ct = new BulwarkListener(new Configuration(this), getConfig()
				.getBoolean("bulwark_creation"));
		rl = new ReinforceListener(getConfig().getBoolean("no_reinforce"));
		ll = new LavaListener(repmat, getConfig().getBoolean("no_lava"));
		wl = new WaterListener(repmat, getConfig().getBoolean("no_water"));
		np = new NoPearlListener(getConfig().getBoolean("allow_group_pearl"),
				getConfig().getBoolean("no_pearl"));
		ml = new MitigationListener(getConfig()
				.getBoolean("mitigation_enabled"));

		regListener(ct);
		regListener(rl);
		regListener(ll);
		regListener(wl);
		regListener(np);
		regListener(ml);

		acom = new AdminCommandExecutor(this);
		setExecutor("bwtoggle", acom);
		setExecutor("bwtoggle_no_reinforce", acom);
		setExecutor("bwtoggle_no_lava", acom);
		setExecutor("bwtoggle_no_water", acom);
		setExecutor("bwtoggle_no_pearl", acom);
		setExecutor("bwtoggle_group_pearl", acom);
		setExecutor("bw_show_mitigation", acom);
		setExecutor("bwtoggle_damage_mitigation", acom);
		setExecutor("bw_age", acom);
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

	public boolean toggleMitigation() {
		boolean state = ml.toggle();
		getConfig().set("mitigation_enabled", state);
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
		classes.add(MitigatingBulwark.class);

		return classes;
	}

	private void migrate() {
		System.out.println("Migrating old bulwarks.");
		List<Bulwark> warks = getDatabase().find(Bulwark.class).findList();
		Iterator<Bulwark> migit = warks.iterator();

		while (migit.hasNext()) {
			Bulwark wark = migit.next();
			MitigatingBulwark mitigatingBulwark = new MitigatingBulwark(wark);
			getDB().persist(mitigatingBulwark);
			migit.remove();
			getDatabase().delete(wark);
		}
	}

	private float getFloat(String path) {
		return (float) getConfig().getDouble(path);
	}

	private ArrayList<String> getSingleStatements() {
		SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
		String create = serv.getDdlGenerator().generateCreateDdl();
		ArrayList<String> stats = new ArrayList<>();
		ArrayList<Character> chars = new ArrayList<>();
		for (char c : create.toCharArray()) {
			chars.add(c);
			if (c == ';') {
				char[] str = new char[chars.size()];
				for (int i = 0; i < str.length; i++) {
					str[i] = chars.get(i);
				}

				stats.add(new String(str));
				chars = new ArrayList<>();
			}
		}

		char[] str = new char[chars.size()];
		for (int i = 0; i < str.length; i++) {
			str[i] = chars.get(i);
		}

		stats.add(new String(str));
		return stats;
	}
}
