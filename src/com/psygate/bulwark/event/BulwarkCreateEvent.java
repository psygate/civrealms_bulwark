package com.psygate.bulwark.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.untamedears.citadel.entity.IReinforcement;

/**
 * 
 * @author psygate
 * 
 *         The main bulwark event. If this is cancelled, no bulwark will be
 *         created.
 */
public class BulwarkCreateEvent extends Event implements Cancellable {
	private static final HandlerList hl = new HandlerList();
	private Block block;
	private Player player;
	private IReinforcement reinf;
	private boolean cancelled = false;

	/**
	 * Creates a new BulwarkCreateEvent.
	 * 
	 * @param block
	 *            Block which is the bulwark (center) around which the bounding
	 *            box will span.
	 * @param player
	 *            Player who created this bulwark.
	 * @param iReinforcement
	 *            Citadel-IReinforcement attached to the bulwark creation.
	 */
	public BulwarkCreateEvent(Block block, Player player, IReinforcement iReinforcement) {
		this.block = block;
		this.player = player;
		this.reinf = iReinforcement;
	}

	/**
	 * Gets the block which is the center of the bulwark.
	 * 
	 * @return Block which is the center of the bulwark.
	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * Gets the player who is the creator of the bulwark.
	 * 
	 * @return Player who is the creator of the bulwark.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the reinforcement associated with this bulwark.
	 * @param iReinforcement
	 *            Citadel-IReinforcement attached to the bulwark creation.
	 */
	public IReinforcement getReinforcement() {
		return reinf;
	}

	@Override
	public HandlerList getHandlers() {
		return hl;
	}

	public static HandlerList getHandlerList() {
		return hl;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}
}
