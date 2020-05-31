package me.arkorwan.srpg.models.equipment;

import java.util.Optional;

import me.arkorwan.srpg.models.Elemental;
import me.arkorwan.srpg.models.StatusEffector;

/**
 * An equipment is something a unit can wear.
 * 
 * @author arkorwan
 *
 */
public interface Equipment extends StatusEffector {

	public static enum EqType {
		Unclassifiable, Weapon, Armour;
	}

	String getName();

	EqType getEquipmentType();

	Optional<MaterialAttribute> getAttribute(MaterialAttribute.Type type);

	double magicalPower();

	Elemental getElemental();

	double getWeight();

	String getInformation();

}