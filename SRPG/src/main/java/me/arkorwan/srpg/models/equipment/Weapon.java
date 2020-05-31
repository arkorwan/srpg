package me.arkorwan.srpg.models.equipment;

/**
 * Attacking equipment
 * 
 * @author arkorwan
 *
 */
public interface Weapon extends Equipment {

	double attackPower();
	
	default int attackMinRange() {
		return 1;
	}

	int attackMaxRange();

	int attackLowerRange();

	int attackHigherRange();

	default boolean verticalAttackAllowed(int diffHeight) {
		if (diffHeight >= 0) {
			return attackHigherRange() < 0 || diffHeight <= attackHigherRange();
		} else {
			return attackLowerRange() < 0 || -diffHeight <= attackLowerRange();
		}
	}

	boolean isRangeDirect();
	AttackType naturalAttackingType();
	AttackType physicalAttackingType();
}
