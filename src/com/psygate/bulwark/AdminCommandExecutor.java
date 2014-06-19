package com.psygate.bulwark;

import java.io.Console;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import static org.bukkit.ChatColor.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.psygate.bulwark.entity.Bulwark;
import com.psygate.bulwark.entity.MitigatingBulwark;

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
	public boolean onCommand(CommandSender sender, Command command,
			String alias, String[] args) {
		// if (!arg0.isOp()) {
		// arg0.sendMessage(ChatColor.RED + "You must be operator to do that.");
		// } else
		if (command.getName().equals("bwtoggle")) {
			boolean state = main.toggleCitadel();
			sender.sendMessage("Bulwark creation now "
					+ ((state) ? GREEN + "on" : RED + "off"));
		} else if (command.getName().equals("bwtoggle_no_reinforce")) {
			boolean state = main.toggleReinforcement();
			sender.sendMessage("No reinforcement now "
					+ ((state) ? GREEN + "on" : RED + "off"));
		} else if (command.getName().equals("bwtoggle_no_lava")) {
			boolean state = main.toggleLava();
			sender.sendMessage("No lava placement now "
					+ ((state) ? GREEN + "on" : RED + "off"));
		} else if (command.getName().equals("bwtoggle_no_water")) {
			boolean state = main.toggleWater();
			sender.sendMessage("No water placement now "
					+ ((state) ? GREEN + "on" : RED + "off"));
		} else if (command.getName().equals("bwtoggle_no_pearl")) {
			boolean state = main.togglePearl();
			sender.sendMessage("No pearl teleportation "
					+ ((state) ? GREEN + "on" : RED + "off"));
		} else if (command.getName().equals("bwtoggle_group_pearl")) {
			boolean state = main.toggleGroup();
			sender.sendMessage("Allow member pearl teleport now "
					+ ((state) ? GREEN + "on" : RED + "off"));
		} else if (command.getName().equals("bwtoggle_damage_mitigation")) {
			boolean state = main.toggleMitigation();
			sender.sendMessage("Member damage mitigation now "
					+ ((state) ? GREEN + "on" : RED + "off"));
		} else if (command.getName().equals("bw_show_mitigation")) {
			listBulwarkMitigation(sender);
		} else if (command.getName().equals("bw_age")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only usable as player.");
			} else if (args.length != 1) {
				sender.sendMessage("Not enough arguments.");
			} else {
				int age;
				try {
					age = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					sender.sendMessage("Argument must be an integer.");
					return true;
				}

				ageBulwark((Player) sender, age);
			}
		}

		return true;
	}

	private void ageBulwark(Player player, int age) {
		List<? extends MitigatingBulwark> list = BulwarkPlugin.getDB()
				.getContaining(player.getLocation().getBlock());
		MitigatingBulwark cand = null;
		for (MitigatingBulwark bw : list) {
			if (cand == null) {
				cand = bw;
			} else {
				if (distSqr(player, cand) > distSqr(player, bw)) {
					cand = bw;
				}
			}
		}
		
		if (cand == null) {
			player.sendMessage("No bulwarks found.");
		} else {
			cand.setCreation(System.currentTimeMillis()
					- TimeUnit.DAYS.toMillis(age));
			player.sendMessage("Bulwark age set.");
			player.sendMessage(cand.toString());
			
			BulwarkPlugin.getDB().update(cand);
		}
	}

	private double distSqr(Player p, Bulwark b) {
		return p.getLocation().distanceSquared(b.getLocation());
	}

	private void listBulwarkMitigation(CommandSender arg0) {
		if (arg0 instanceof ConsoleCommandSender) {
			List<? extends MitigatingBulwark> list = BulwarkPlugin.getDB()
					.getAllBulwarks();
			Iterator<? extends MitigatingBulwark> it = list.iterator();
			while (it.hasNext()) {
				MitigatingBulwark bw = it.next();
				arg0.sendMessage(bw.toString());
				it.remove();
			}
		} else if (arg0 instanceof Player) {
			List<? extends MitigatingBulwark> list = BulwarkPlugin.getDB()
					.getContaining(((Player) arg0).getLocation().getBlock());
			Iterator<? extends MitigatingBulwark> it = list.iterator();
			while (it.hasNext()) {
				MitigatingBulwark bw = it.next();
				arg0.sendMessage(bw.toUserString());
				it.remove();
			}
		}
	}
}
