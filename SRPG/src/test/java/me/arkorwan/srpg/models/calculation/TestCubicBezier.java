package me.arkorwan.srpg.models.calculation;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TestCubicBezier {

	static final double e = 0.000001;
	
	@Test
	public void testLimit(){
		CubicBezierShaper func = new CubicBezierShaper(0, 0, 1, 1);
		assertEquals(func.apply(0.5), 0.5, e);
	}
	
}
