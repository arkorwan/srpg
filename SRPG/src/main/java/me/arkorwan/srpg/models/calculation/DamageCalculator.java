package me.arkorwan.srpg.models.calculation;

import java.util.Random;

import me.arkorwan.srpg.models.MagicSpell;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.equipment.AttackType;

public class DamageCalculator {

	double elementalMultiplier;
	double damageAtkMultiplier;
	double damageAtkDegree;
	double damageDiscountDegree;

	public DamageCalculator(double elementalMultiplier,
			double damageAtkMultiplier, double damageAtkDegree,
			double damageDiscountDegree) {
		this.elementalMultiplier = elementalMultiplier;
		this.damageAtkMultiplier = damageAtkMultiplier;
		this.damageAtkDegree = damageAtkDegree;
		this.damageDiscountDegree = damageDiscountDegree;
	}

	public Damage physicalDamage(Unit attacker, Unit target, Random r) {
		double atk = attacker.getPhysicalAttack().getValue()
				* attacker.getWeapon().attackPower();

		double def = target.getPhysicalDefense().getValue()
				* target.getArmour().defensePower()
				* target.getArmour().defenseMultiplier(
						attacker.getWeapon().physicalAttackingType());

		// double deviation = attacker.getCourage().getValue() * 1.0
		// / target.getCourage().getMaxValue();

		return new Damage(calculateDamage(atk, def),
				calculateEvasion(attacker, target), 0.5, r);
	}

	double magicalPotent(Unit caster, MagicSpell spell) {
		return caster.getMagicalAttack().getValue()
				* (caster.getWeapon().magicalPower()
						+ (spell.power() - 1) / 2.0);
	}

	public Damage magicalDamage(Unit attacker, Unit target, Random r,
			MagicSpell spell) {

		double atk = magicalPotent(attacker, spell);

		double def = target.getMagicalDefense().getValue()
				* target.getArmour().magicalPower()
				* target.getArmour().defenseMultiplier(AttackType.Magic);

		double dmg = calculateDamage(atk, def);

		// double deviation = attacker.getCourage().getValue() * 1.0
		// / target.getCourage().getMaxValue();

		int elem = spell.getElemental()
				.advantage(target.getArmour().getElemental());
		if (elem > 0) {
			dmg *= elementalMultiplier;
		} else if (elem < 0) {
			dmg /= elementalMultiplier;
		}

		return new Damage(dmg, calculateEvasion(attacker, target), 0.5, r);
	}

	public Damage healingDamage(Unit healer, Random r, MagicSpell spell) {
		double heal = magicalPotent(healer, spell);

		// scaling -- heal amount should be less than general magical attack.
		heal = calculateDamage(heal, heal);

		return new Damage(heal, 0.2, 0.5, r);
	}

	private double calculateDamage(double atk, double def) {
		double damage = damageAtkMultiplier * Math.pow(atk, damageAtkDegree)
				* Math.pow(atk / (atk + def), damageDiscountDegree);
		return damage;
	}

	private static double calculateEvasion(Unit attacker, Unit target) {
		int atk = attacker.getEvasion().getValue();
		int def = target.getEvasion().getValue();

		return def / (atk + def);
	}
}
