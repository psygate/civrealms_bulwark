package com.psygate.bulwark.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * 
 * @author psygate
 * 
 *         Main bulwark class. A bulwark is a block with an associated bounding
 *         box that provides protection / benefits in that bounding box.<br>
 *         A bulwark is defined as:
 *         <ul>
 *         <li>Center Block: A block defining the center of the bulwark bounding
 *         box.</li>
 *         <li>A Bounding Box: Defined by the lower point (lbx,lby,lbz) and the
 *         upper point (ubx,uby,ubz). A bounding box is ALWAYS axis aligned.</li>
 *         </ul>
 */
@MappedSuperclass
@Entity
@UniqueConstraint(columnNames = { "x", "y", "z", "worlduid" })
public class Bulwark {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private int size;
	private int x;
	private int y;
	private int z;
	private String worlduid;
	// These variables enclose the bulwark inside a cube so the lookup is
	// relatively fast.

	// Lower bounds
	private int lbx;
	private int lby;
	private int lbz;

	// Upper bounds
	private int ubx;
	private int uby;
	private int ubz;

	@Transient
	private Location loc = null;

	/**
	 * Creates an empty bulwark object, this should not be used from code, but
	 * only by the beans layer.
	 */
	public Bulwark() {

	}

	/**
	 * Creates a new bulwark.
	 * 
	 * @param size
	 *            Size of the bulwark bounds. The size is determining the extend
	 *            of the bounding box by:<br>
	 *            lower bounds are center-block coordinates - size<br>
	 *            upper bounds are center-block coordinates + size
	 * @param center
	 *            Center block, which is the bulwark block.
	 */
	public Bulwark(int size, Block center) {
		this.size = size;
		x = center.getX();
		y = center.getY();
		z = center.getZ();
		worlduid = center.getWorld().getUID().toString();
		lbx = x - size;
		lby = y - size;
		lbz = z - size;
		ubx = x + size;
		uby = y + size;
		ubz = z + size;
		loc = center.getLocation();
	}

	public Bulwark(Bulwark old) {
		this.size = old.size;
		this.x = old.x;
		this.y = old.y;
		z = old.z;
		worlduid = old.worlduid;
		lbx = old.lbx;
		lby = old.lby;
		ubx = old.ubx;
		uby = old.uby;
		ubz = old.ubz;
	}

	/**
	 * Gets the id of the bulwark. Only available if it has been persisted by
	 * the entity bean layer. Should not be used from plugin land code, but only
	 * by the entity beans layer.
	 * 
	 * @return Id in the database table.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id of the bulwark. Should not be used from plugin land code, but
	 * only by the entity beans layer.
	 * 
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the size of the bulwark. May or may not invoke a call to the
	 * database. If this is a persisted object from the entity beans layer, this
	 * will invoke a database call.
	 * 
	 * @return Size of the bulwark bounding box. Bounding box size is always
	 *         2*size in each direction, from one plane of the bounding box,
	 *         with the bulwark coordinates / block residing at size from each
	 *         plane.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size of the bulwark. May or may not invoke a call to the
	 * database. If this is a persisted object from the entity beans layer, this
	 * will invoke a database call.
	 * 
	 * @param size
	 *            Size of the bulwark, must not be negative or 0, this will make
	 *            the bounding box algorithms break.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Gets the center x coordinate of the center block of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @return x coordinate of the center block of the bulwark.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x coordinate of the bulwark block. If this is a persisted object
	 * from the entity beans layer, this will invoke a database call.
	 * 
	 * @param x
	 *            New x coordinate of the bulwark block. This cannot make the
	 *            bulwark block the same block as another bulwark, or the entity
	 *            bean layer will throw an exception.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the center y coordinate of the center block of the bulwark.If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @return y coordinate of the center block of the bulwark.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y coordinate of the bulwark block. If this is a persisted object
	 * from the entity beans layer, this will invoke a database call.
	 * 
	 * @param y
	 *            New y coordinate of the bulwark block. This cannot make the
	 *            bulwark block the same block as another bulwark, or the entity
	 *            bean layer will throw an exception.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets the center z coordinate of the center block of the bulwark.
	 * 
	 * @return z coordinate of the center block of the bulwark.
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Sets the y coordinate of the bulwark block. If this is a persisted object
	 * from the entity beans layer, this will invoke a database call.
	 * 
	 * @param y
	 *            New y coordinate of the bulwark block. This cannot make the
	 *            bulwark block the same block as another bulwark, or the entity
	 *            bean layer will throw an exception.
	 */
	public void setZ(int z) {
		this.z = z;
	}

	/**
	 * Gets the world uid as a string for this bulwark. If this is a persisted
	 * object from the entity beans layer, this will invoke a database call.
	 * 
	 * @return WorldUID as a string.
	 */
	public String getWorlduid() {
		return worlduid;
	}

	/**
	 * Sets the world uid as a string for this bulwark. If this is a persisted
	 * object from the entity beans layer, this will invoke a database call.
	 * 
	 * @param worlduid
	 *            The new world uid to set for the bulwark. Must be != null.
	 */
	public void setWorlduid(String worlduid) {
		this.worlduid = worlduid;
	}

	/**
	 * Gets the lower x coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @return x coordinate of the lower point of the bounding box.
	 */
	public int getLbx() {
		return lbx;
	}

	/**
	 * Sets the lower x coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @param lbx
	 *            This must be strictly smaller than {@link Bulwark#getUbx()},
	 *            or strange bugs may occur. There is no internal check in this
	 *            method to assert this.
	 */
	public void setLbx(int lbx) {
		this.lbx = lbx;
	}

	/**
	 * Gets the lower y coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @return y coordinate of the lower point of the bounding box.
	 */
	public int getLby() {
		return lby;
	}

	/**
	 * Sets the lower y coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @param lby
	 *            This must be strictly smaller than {@link Bulwark#getUby()},
	 *            or strange bugs may occur. There is no internal check in this
	 *            method to assert this.
	 */
	public void setLby(int lby) {
		this.lby = lby;
	}

	/**
	 * Gets the lower z coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @return z coordinate of the lower point of the bounding box.
	 */
	public int getLbz() {
		return lbz;
	}

	/**
	 * Sets the lower z coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @param lbz
	 *            This must be strictly smaller than {@link Bulwark#getUbz()},
	 *            or strange bugs may occur. There is no internal check in this
	 *            method to assert this.
	 */
	public void setLbz(int lbz) {
		this.lbz = lbz;
	}

	/**
	 * Gets the upper x coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @return x coordinate of the upper point of the bounding box.
	 */
	public int getUbx() {
		return ubx;
	}

	/**
	 * Sets the upper x coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @param ubx
	 *            This must be strictly larger than {@link Bulwark#getLbx()}, or
	 *            strange bugs may occur. There is no internal check in this
	 *            method to assert this.
	 */
	public void setUbx(int ubx) {
		this.ubx = ubx;
	}

	/**
	 * Gets the upper y coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @return y coordinate of the upper point of the bounding box.
	 */
	public int getUby() {
		return uby;
	}

	/**
	 * Sets the upper y coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @param uby
	 *            This must be strictly larger than {@link Bulwark#getLby()}, or
	 *            strange bugs may occur. There is no internal check in this
	 *            method to assert this.
	 */
	public void setUby(int uby) {
		this.uby = uby;
	}

	/**
	 * Gets the upper z coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @return z coordinate of the upper point of the bounding box.
	 */
	public int getUbz() {
		return ubz;
	}

	/**
	 * Sets the upper z coordinate for the bounding box of the bulwark. If this
	 * is a persisted object from the entity beans layer, this will invoke a
	 * database call.
	 * 
	 * @param ubz
	 *            This must be strictly larger than {@link Bulwark#getLbz()}, or
	 *            strange bugs may occur. There is no internal check in this
	 *            method to assert this.
	 */
	public void setUbz(int ubz) {
		this.ubz = ubz;
	}

	/**
	 * Converts the internal format of the bulwark block in the center of the
	 * bounding box to a {@link Location}.
	 * 
	 * @return {@Link Location} representation of the center point of te
	 *         bulwark.
	 */
	public Location getLocation() {
		if (loc == null) {
			loc = new Location(Bukkit.getWorld(UUID.fromString(getWorlduid())),
					getX(), getY(), getZ());
		}

		return loc;
	}

	/**
	 * Checks if two bulwarks overlap each other (Bounding boxes intersect OR
	 * touch)
	 * 
	 * @param bulw
	 *            Bulwark to check against. Must be != null.
	 * @return True if the bulwarks intersect, intersection is defined as
	 *         intersection of the bounding boxes OR touching of the bounding
	 *         boxes.
	 */
	public boolean overlaps(Bulwark bulw) {
		return (getUbx() >= bulw.getUbx() && getLbx() <= bulw.getLbx())
				&& (getUby() >= bulw.getLby() && getLby() <= bulw.getUby())
				&& (getUbz() >= bulw.getLbz() && getLbz() <= bulw.getUbz());
	}

	/**
	 * Checks if the bulwark bounding box contains a location.
	 * 
	 * @param from
	 *            Location to check against. Must be != null.
	 * @return true, if the location is within the bounding box, else, false.
	 */
	public boolean containsLocation(Location from) {
		if (!from.getWorld().getUID().toString().equals(getWorlduid())) {
			return false;
		}

		return from.getX() <= getUbx() && from.getX() >= getLbx()
				&& from.getY() <= getUby() && from.getY() >= getLby()
				&& from.getZ() <= getUbz() && from.getZ() >= getLbz();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + lbx;
		result = prime * result + lby;
		result = prime * result + lbz;
		result = prime * result + ((loc == null) ? 0 : loc.hashCode());
		result = prime * result + size;
		result = prime * result + ubx;
		result = prime * result + uby;
		result = prime * result + ubz;
		result = prime * result
				+ ((worlduid == null) ? 0 : worlduid.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!getClass().equals(obj.getClass()))
			return false;
		Bulwark other = (Bulwark) obj;
		// if (id != other.id)
		// return false;
		if (lbx != other.lbx)
			return false;
		if (lby != other.lby)
			return false;
		if (lbz != other.lbz)
			return false;
		if (size != other.size)
			return false;
		if (ubx != other.ubx)
			return false;
		if (uby != other.uby)
			return false;
		if (ubz != other.ubz)
			return false;
		if (worlduid == null) {
			if (other.worlduid != null)
				return false;
		} else if (!worlduid.equals(other.worlduid))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
}
