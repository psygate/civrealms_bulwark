package com.psygate.bulwark.db;

import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.Query;
import com.psygate.bulwark.entity.Bulwark;
import com.psygate.bulwark.entity.MitigatingBulwark;

/**
 * 
 * @author psygate
 * 
 *         a simple and easy abstraction to access the database. Calls to the
 *         database should route through here.<br>
 *         <ul>
 *         <li>TODO: Add a cache layer.</li>
 *         </ul>
 */
public class DBLayer {
	private EbeanServer serv;

	/**
	 * Constructs a new DBLayer.
	 * 
	 * @param ebeanserv
	 *            The ebean server which should be used for calls.
	 */
	public DBLayer(EbeanServer ebeanserv) {
		serv = ebeanserv;
	}

	/**
	 * Stores a bulwark in the database.
	 * 
	 * @param bulw
	 *            The bulwark to persist. Must be != null and not already
	 *            stored.
	 */
	public void persist(Bulwark bulw) {
		serv.save(new MitigatingBulwark(bulw));
	}
	
	public void persist(MitigatingBulwark bulw) {
		serv.save(bulw);
	}
	
	public void update(MitigatingBulwark bw) {
		serv.update(bw);
	}

	/**
	 * Removes a bulwark.
	 * 
	 * @param bulw
	 *            Bulwark to remove. Must be != null and already stored.
	 */
	public void delete(Bulwark bulw) {
		serv.delete(bulw);
	}

	public void delete(MitigatingBulwark bulw) {
		serv.delete(bulw);
	}

	/**
	 * Returns a list of bulwarks containing the provided block. One may use
	 * this to retrieve a bulwark block, in this case, the list has to be
	 * iterated through to determine which bulwark is associated with the
	 * provided block. If the block of the bulwark is already known,
	 * {@link DBLayer#get(Block)} is to be used.
	 * 
	 * @param block
	 *            Block to check for bulwarks that contain this block. Must be
	 *            != null.
	 * @return List of bulwarks containing the provided block. If none are
	 *         found, an empty list is returned.
	 */
	public List<MitigatingBulwark> getContaining(Block block) {
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		Query<MitigatingBulwark> query = serv
				.createQuery(MitigatingBulwark.class);

		// Basic query if something is in a cube."In" includes the side planes.
		query.where().le("lbx", x).le("lby", y).le("lbz", z).ge("ubx", x)
				.ge("uby", y).ge("ubz", z)
				.eq("worlduid", block.getWorld().getUID().toString());
		return query.findList();
	}

	/**
	 * Returns the bulwark associated with this block.
	 * 
	 * @param block
	 *            Bulwark block to search for. If this block is not associated
	 *            with a bulwark, null is returned. Must be != null.
	 * @return Bulwark that is associated with the block, or null, if none are
	 *         associated with the block.
	 */
	public MitigatingBulwark get(Block block) {
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		Query<MitigatingBulwark> query = serv
				.createQuery(MitigatingBulwark.class);

		query.where().eq("x", x).eq("y", y).eq("z", z)
				.eq("worlduid", block.getWorld().getUID().toString());
		return query.findUnique();
	}

	/**
	 * Returns a list of intersecting bulwarks. Intersection is defined as the
	 * bounding box of bulwark A intersects OR touches the bounding box of
	 * bulwark B.
	 * 
	 * @param nb
	 *            Bulwark to search for intersection with other bulwarks. Must
	 *            be != null.
	 * @return List of intersecting bulwarks. If none intersect, an empty list
	 *         is returned.
	 */
	public List<MitigatingBulwark> getIntersecting(Bulwark nb) {
		// basic cube intersection query

		Query<MitigatingBulwark> query = serv
				.createQuery(MitigatingBulwark.class);

		int lx = nb.getLbx();
		int ux = nb.getUbx();
		int ly = nb.getLby();
		int uy = nb.getUby();
		int lz = nb.getLbz();
		int uz = nb.getUbz();
		String wid = nb.getWorlduid();

		query.where().ge("ubx", lx).le("lbx", ux).ge("uby", ly).le("lby", uy)
				.ge("ubz", lz).le("lbz", uz).eq("worlduid", wid);
		List<MitigatingBulwark> list = query.findList();
		return list;
	}

	/**
	 * Returns a list of bulwarks of which the bounding box intersect the
	 * provided bounding box. The provided bounding box is defined as:<br>
	 * ax - bx -> lower and upper x coordinate of the provided bounding box.<br>
	 * ay - by -> lower and upper y coordinate of the provided bounding box.<br>
	 * az - bz -> lower and upper z coordinate of the provided bounding box.<br>
	 * Intersection is defined as bounding boxes intersect OR touch. An internal
	 * check if performed, so the point specifications may be provided in any
	 * order. Meaning, ax, ay and az do not have to be smaller or equal to
	 * bx,by,bz.
	 * 
	 * @param ax
	 *            lower or upper x coordinate of the provided bounding box
	 * @param ay
	 *            lower or upper y coordinate of the provided bounding box
	 * @param az
	 *            lower or upper z coordinate of the provided bounding box
	 * @param bx
	 *            lower or upper x coordinate of the provided bounding box
	 * @param by
	 *            lower or upper y coordinate of the provided bounding box
	 * @param bz
	 *            lower or upper z coordinate of the provided bounding box
	 * @param w
	 *            World in which the bounding box resides. Must be != null.
	 * @return A list of bulwarks that intersect OR touch the provided bounding
	 *         box. An empty list is returned if no bulwarks intersect.
	 */
	public List<MitigatingBulwark> getIntersecting(float ax, float ay,
			float az, float bx, float by, float bz, World w) {
		int lx = (int) min(ax, bx);
		int ly = (int) min(ay, by);
		int lz = (int) min(az, bz);

		int ux = (int) max(ax, bx) + 1;
		int uy = (int) max(ay, by) + 1;
		int uz = (int) max(az, bz) + 1;

		Query<MitigatingBulwark> query = serv
				.createQuery(MitigatingBulwark.class);

		query.where().ge("ubx", lx).le("lbx", ux).ge("uby", ly).le("lby", uy)
				.ge("ubz", lz).le("lbz", uz)
				.eq("worlduid", w.getUID().toString());
		return query.findList();
	}

	public List<MitigatingBulwark> getAllBulwarks() {
		Query<MitigatingBulwark> query = serv
				.createQuery(MitigatingBulwark.class);
		return query.findList();
	}

	private float min(float ax, float bx) {
		if (ax < bx) {
			return ax;
		} else {
			return bx;
		}
	}

	private float max(float ax, float bx) {
		if (ax < bx) {
			return bx;
		} else {
			return ax;
		}
	}
}
