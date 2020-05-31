package me.arkorwan.srpg.models.calculation;

import java.util.Optional;
import java.util.Random;

/**
 * A wrapper object for a damage (or healing) that is yet to be evaluated.
 * 
 * @author arkorwan
 *
 */
public class Damage {

	transient Random r;

	private double baseDamage;
	private double evasion;
	private double deviation;

	Damage(double base, double evasion, double deviation, Random r) {
		this.baseDamage = base;
		this.evasion = evasion;
		this.deviation = deviation;
		this.r = r;
	}

	public double getBaseDamage() {
		return baseDamage;
	}

	public double getEvasion() {
		return evasion;
	}

	/**
	 * Evaluate the actual damage. The amount is varied from +/-(deviation)x of
	 * base damage, and evasion is a probability that the attack/heal is missed.
	 * 
	 * @return
	 */
	public Optional<Integer> getActualDamage() {
		if (r.nextDouble() < evasion) {
			return Optional.empty();
		} else {
			return Optional.of((int) Math.round(
					((r.nextDouble() * 2.0 - 1.0) * deviation * baseDamage)
							+ baseDamage));
		}

	}

}
