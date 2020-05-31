package me.arkorwan.srpg.models.equipment;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import me.arkorwan.srpg.models.BasicAttribute.Type;
import me.arkorwan.srpg.models.Elemental;

/**
 * The default 'equipment' when a unit does not wear anything. Fists fighting!
 * 
 * @author arkorwan
 *
 */
public enum EmptyEquipment implements Weapon, Armour {
	Instance;

	@Override
	public double attackPower() {
		return 1.0;
	}

	@Override
	public int defensePower() {
		return 1;
	}

	@Override
	public int attackMaxRange() {
		return 1;
	}

	@Override
	public int attackLowerRange() {
		return 1;
	}

	@Override
	public int attackHigherRange() {
		return 1;
	}

	@Override
	public boolean isRangeDirect() {
		return true;
	}

	@Override
	public String getName() {
		return "None";
	}

	@Override
	public EqType getEquipmentType() {
		return EqType.Unclassifiable;
	}

	@Override
	public Map<Type, Integer> effects() {
		return Collections.emptyMap();
	}

	@Override
	public Optional<MaterialAttribute> getAttribute(
			MaterialAttribute.Type type) {
		return Optional.empty();
	}

	@Override
	public Elemental getElemental() {
		return Elemental.None;
	}

	@Override
	public double getWeight() {
		return 0;
	}

	@Override
	public String getInformation() {
		return "Nothing";
	}

	@Override
	public double magicalPower() {
		return 0;
	}

	@Override
	public AttackType naturalAttackingType() {
		return AttackType.Melee;
	}

	@Override
	public double defenseMultiplier(AttackType against) {
		return 0.01;
	}

	@Override
	public AttackType physicalAttackingType() {
		return AttackType.Melee;
	}

}
