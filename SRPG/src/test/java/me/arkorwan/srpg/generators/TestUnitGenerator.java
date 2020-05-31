package me.arkorwan.srpg.generators;

import static me.arkorwan.testng.utils.DataProviders.cross;
import static me.arkorwan.testng.utils.DataProviders.wrap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import me.arkorwan.srpg.generators.constraints.AttributeConstraint;
import me.arkorwan.srpg.models.BasicAttribute;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;

public class TestUnitGenerator {

	static final double e = 0.000001;

	@DataProvider
	public Object[][] constraintProvider() {
		Object[] types = BasicAttribute.Type.values();
		Double[][] minmax = { { 0.5, 0.8 }, { 0.2, 0.5 }, { 0.75, 1.0 },
				{ 0.0, 0.25 } };

		return cross(wrap(types), minmax);
	}

	@Test
	public void testNoConstraintUnit() {
		BattleSystem sys = new BattleSystem();
		UnitGenerator g = new UnitGenerator(sys, ThreadLocalRandom.current());
		for (int i = 0; i < 10; i++) {
			g.refreshQuotas();
			Unit u = g.generate();

			for (BasicAttribute.Type t : BasicAttribute.Type.values()) {
				verifyAttribute(u.getBasicAttribute(t));
			}

			verifyAttributesSum(u);
		}

	}

	@Test(dataProvider = "constraintProvider")
	public void testSingleConstraintUnit(BasicAttribute.Type type, double min,
			double max) {
		BattleSystem sys = new BattleSystem();
		UnitGenerator g = new UnitGenerator(sys, ThreadLocalRandom.current());
		AttributeConstraint<BasicAttribute.Type> c = new AttributeConstraint<>(
				type, min, max);
		g.setAttributeConstraints(Collections.singleton(c));
		for (int i = 0; i < 10; i++) {
			g.refreshQuotas();
			Unit u = g.generate();

			for (BasicAttribute.Type t : BasicAttribute.Type.values()) {
				verifyAttribute(u.getBasicAttribute(t));
			}

			BasicAttribute attr = u.getBasicAttribute(type);
			double v = attr.unscaledValue();
			assertTrue(v >= min && v <= max,
					"attribute not in valid range, found " + v);

			verifyAttributesSum(u);
		}
	}

	private void verifyAttribute(BasicAttribute attr) {
		double v = attr.unscaledValue();
		assertTrue(v >= 0 && v <= 1.0,
				"attribute not in valid range, found " + v);
	}

	private void verifyAttributesSum(Unit u) {
		double sum = u.strength().unscaledValue()
				+ u.intelligence().unscaledValue() + u.wisdom().unscaledValue()
				+ u.dexterity().unscaledValue()
				+ u.constitution().unscaledValue();
		assertEquals(sum / 5, 0.5, e);
	}

}
