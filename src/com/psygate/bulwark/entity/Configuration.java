package com.psygate.bulwark.entity;

import org.bukkit.Material;

import com.psygate.bulwark.BulwarkPlugin;

/**
 * 
 * @author psygate
 * 
 *         This represents the global configuration of Bulwark.
 */
public class Configuration {
	private Material bulmat;
	private int radius;

	/**
	 * Constructs a new Configuration.
	 * 
	 * @param plug
	 *            The main plugin class. The constructor will automatically save
	 *            & load the default configuration and parse the settings.
	 */
	public Configuration(BulwarkPlugin plug) {
		plug.getConfig().options().copyDefaults(true);
		plug.saveConfig();

		bulmat = Material.valueOf(plug.getConfig().getString("block_type"));
		radius = plug.getConfig().getInt("bulwark_size");
	}

	/**
	 * Gets the material which determines the bulwark block.
	 * 
	 * @return material of the bulwark block.
	 */
	public Material getBulwarkMaterial() {
		return bulmat;
	}

	/**
	 * Gets the size of the bulwark bounding box.
	 * 
	 * @return Size of the bulwark bounding box. (The box size is 2*size)
	 */
	public int getSize() {
		return radius;
	}
}
