package me.arkorwan.srpg.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.arkorwan.srpg.generators.constraints.QuotaConstraint;
import me.arkorwan.srpg.generators.constraints.QuotaConstraint.Quota;
import me.arkorwan.srpg.models.Elemental;
import me.arkorwan.srpg.models.Job;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.srpg.models.equipment.ConcreteWeapon;
import me.arkorwan.srpg.models.equipment.ConcreteWeapon.WeaponType;
import me.arkorwan.srpg.models.equipment.MaterialAttribute.Type;
import me.arkorwan.srpg.models.equipment.Weapon;

public class WeaponGenerator extends EquipmentGenerator<Weapon> {

	QuotaConstraint<WeaponType> weaponTypeConstraint;
	transient Quota<WeaponType> qw;
	Job job;

	public WeaponGenerator(BattleSystem sys, Random r) {
		super(sys, r);
	}

	public void setWeaponTypeConstraint(QuotaConstraint<WeaponType> type) {
		this.weaponTypeConstraint = type;
	}

	@Override
	public void refreshQuotas() {
		super.refreshQuotas();
		qw = QuotaConstraint.createFreshQuota(weaponTypeConstraint);
	}

	@Override
	protected Weapon createEquipment(String name, Map<Type, Double> attributes,
			Elemental element) {

		List<WeaponType> types = new ArrayList<>();
		for (WeaponType wt : WeaponType.values()) {
			if (wt.naturalAttackingType() == job.getWeaponAttackType()) {
				types.add(wt);
			}
		}

		WeaponType type = qw.getRandomItem(rand,
				types.toArray(new WeaponType[0]));
		ConcreteWeapon w = new ConcreteWeapon(sys, type, name, attributes);
		if (job == Job.Mage) {
			w.setElemental(element);
		} else if (job == Job.Cleric) {
			w.setElemental(Elemental.Holy);
		}
		return w;
	}

}
