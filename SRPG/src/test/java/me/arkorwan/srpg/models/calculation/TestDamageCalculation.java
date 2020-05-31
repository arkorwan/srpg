package me.arkorwan.srpg.models.calculation;

import static org.testng.Assert.assertTrue;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static me.arkorwan.testng.utils.DataProviders.wrap;
import static me.arkorwan.testng.utils.DataProviders.cross;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import me.arkorwan.srpg.models.Elemental;
import me.arkorwan.srpg.models.Job;
import me.arkorwan.srpg.models.Race;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.srpg.models.equipment.ConcreteArmour;
import me.arkorwan.srpg.models.equipment.ConcreteArmour.ArmourType;
import me.arkorwan.srpg.models.equipment.ConcreteWeapon;
import me.arkorwan.srpg.models.equipment.ConcreteWeapon.WeaponType;

public class TestDamageCalculation {

	@DataProvider
	public Object[][] elementalProviders() {
		Object[] blackMagic = { Elemental.Fire, Elemental.Water,
				Elemental.Nature };
		Object[][] b = wrap(blackMagic);
		return cross(b, b);
	}

	@Test(dataProvider = "elementalProviders")
	public void testElementalAdvantage(Elemental e1, Elemental e2) {

		Unit u1 = createAverageMage(e1, e2);
		Unit u2 = createAverageMage(e1, e1);

		DamageCalculator dmgCalc = new DamageCalculator(2.0, 1.0, 1.0, 1.0);

		Damage dmg1 = dmgCalc.magicalDamage(u1, u2, null,
				u1.getCurrentBlackMagicSpells().get(0));
		Damage dmg2 = dmgCalc.magicalDamage(u2, u1, null,
				u2.getCurrentBlackMagicSpells().get(0));

		if (e1.advantage(e2) > 0) {
			assertTrue(dmg2.getBaseDamage() > dmg1.getBaseDamage());
		}
		if (e2.advantage(e1) > 0) {
			assertTrue(dmg1.getBaseDamage() > dmg2.getBaseDamage());
		}

		for (int i = 0; i < 50; i++) {
			dmg1.r = ThreadLocalRandom.current();
			Optional<Integer> od = dmg1.getActualDamage();
			if (od.isPresent()) {
				double ratio = od.get() / dmg1.getBaseDamage();
				assertTrue(ratio >= 0.5 && ratio <= 1.5);
			}

		}
	}

	private Unit createAverageMage(Elemental ew, Elemental ea) {
		BattleSystem sys = new BattleSystem();
		Unit u = new Unit(sys, 0.5, 0.5, 0.5, 0.5, 0.5);
		u.setRace(Race.Human);
		u.setJob(Job.Mage);

		ConcreteWeapon w = new ConcreteWeapon(sys, WeaponType.Staff, "x", 0.5,
				0.5, 0.5, 0.5, 0.5);
		w.setElemental(ew);
		u.equip(w);

		ConcreteArmour a = new ConcreteArmour(sys, ArmourType.Robe, "y", 0.5,
				0.5, 0.5, 0.5, 0.5);
		a.setElemental(ea);
		u.equip(a);
		return u;
	}

}
