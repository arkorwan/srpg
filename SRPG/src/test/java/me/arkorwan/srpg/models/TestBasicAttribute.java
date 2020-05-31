package me.arkorwan.srpg.models;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TestBasicAttribute {

	@Test
	public void testAttribute() {

		BasicAttribute f = new BasicAttribute(BasicAttribute.Type.STR, 0.5, 101,
				10);

		assertEquals(f.getName(), "Strength");
		assertEquals(f.getMaxValue(), 101);
		assertEquals(f.getMinValue(), 1);
		assertEquals(f.getValue(), 51);
		assertEquals(f.unscaledValue(), 0.5);
		f.adjustModifier(5);
		assertEquals(f.getValue(), 56);
		f.adjustModifier(-10);
		assertEquals(f.getValue(), 46);

	}

}
