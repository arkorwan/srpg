package me.arkorwan.srpg.models;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import me.arkorwan.srpg.models.calculation.NormalizedFunction;

public class TestDerivedAttribute {

	@Test
	public void testAttribute() {

		NormalizedFunction f = new BasicAttribute(BasicAttribute.Type.STR, 0.5,
				100, 10);

		DerivedAttribute da = new DerivedAttribute("xx", f, -100, 100);

		assertEquals(da.getName(), "xx");
		assertEquals(da.getMaxValue(), 100);
		assertEquals(da.getMinValue(), -100);
		assertEquals(da.getValue(), 0);

	}
}
