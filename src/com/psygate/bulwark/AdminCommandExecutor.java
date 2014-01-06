package com.psygate.bulwark;

import org.bukkit.ChatColor;
import static org.bukkit.ChatColor.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * 
 * @author psygate
 * 
 *         Handles the admin commands.
 * 
 */
public class AdminCommandExecutor implements CommandExecutor {
	private BulwarkPlugin main;

	/**
	 * Constructs a new admin command.
	 * 
	 * @param pl
	 *            Bulwark plugin to manipulate the listener classes.
	 */
	public AdminCommandExecutor(BulwarkPlugin pl) {
		this.main = pl;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (!arg0.isOp()) {
			arg0.sendMessage(ChatColor.RED + "You must be operator to do that.");
		} else if (arg1.getName().equals("bwtoggle")) {
			boolean state = main.toggleCitadel();
			arg0.sendMessage("Bulwark creation now " + ((state) ? GREEN + "on" : RED + "off"));
		} else if (arg1.getName().equals("bwtoggle_no_reinforce")) {
			boolean state = main.toggleReinforcement();
			arg0.sendMessage("No reinforcement now " + ((state) ? GREEN + "on" : RED + "off"));
		} else if (arg1.getName().equals("bwtoggle_no_lava")) {
			boolean state = main.toggleLava();
			arg0.sendMessage("No lava placement now " + ((state) ? GREEN + "on" : RED + "off"));
		} else if (arg1.getName().equals("bwtoggle_no_water")) {
			boolean state = main.toggleWater();
			arg0.sendMessage("No water placement now " + ((state) ? GREEN + "on" : RED + "off"));
		} else if (arg1.getName().equals("bwtoggle_no_pearl")) {
			boolean state = main.togglePearl();
			arg0.sendMessage("No pearl teleportation " + ((state) ? GREEN + "on" : RED + "off"));
		} else if (arg1.getName().equals("bwtoggle_group_pearl")) {
			boolean state = main.toggleGroup();
			arg0.sendMessage("Allow member pearl teleport now " + ((state) ? GREEN + "on" : RED + "off"));
		}

		return true;
	}

}
