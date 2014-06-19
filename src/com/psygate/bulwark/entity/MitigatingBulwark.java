package com.psygate.bulwark.entity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;

import com.psygate.bulwark.BulwarkPlugin;

@Entity
public class MitigatingBulwark extends Bulwark {
	private long creation;

	public MitigatingBulwark() {

	}

	public MitigatingBulwark(Bulwark wark) {
		super(wark);
		creation = System.currentTimeMillis();
	}

	public long getCreation() {
		return creation;
	}

	public void setCreation(long creation) {
		this.creation = creation;
	}

	public String toString() {
		long age = System.currentTimeMillis() - getCreation();
		return "MitigatingBulwark[" + getX() + "," + getY() + "," + getZ()
				+ "](age: " + age + "[" + TimeUnit.MILLISECONDS.toDays(age)
				+ "])";
	}

	public double calcMitigation() {
		double duration = System.currentTimeMillis() - getCreation()
				- BulwarkPlugin.maturationStartAfter;
		if (duration <= 0) {
			return 0;
		}

		double comp = duration / BulwarkPlugin.maturationTime;
		double fac = BulwarkPlugin.maximumMitigation
				- BulwarkPlugin.minimumMitigation;
		return BulwarkPlugin.minimumMitigation + fac
				* ((comp < 0) ? 0 : (comp > 1) ? 1 : comp);
	}

	public String toUserString() {
		long age = System.currentTimeMillis() - getCreation();
		int fullmit = (int) (calcMitigation() * 100);
		float perc = ((float) fullmit) / 100f;
		return "MitigatingBulwark(age: " + TimeUnit.MILLISECONDS.toDays(age)
				+ ", mitigation: " + perc + ")";
	}
}
