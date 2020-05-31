package me.arkorwan.srpg.models.equipment;

/**
 * Defensive equipment
 * 
 * @author arkorwan
 *
 */
public interface Armour extends Equipment {

	int defensePower();

	double defenseMultiplier(AttackType against);
}
