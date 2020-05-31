package me.arkorwan.utils;

import static me.arkorwan.testng.utils.DataProviders.cross;
import static me.arkorwan.testng.utils.DataProviders.wrap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestCommon {

	static final double e = 0.000001;

	@DataProvider
	public Object[][] doubleValueProvider() {
		return wrap(0.5, 1.0, Math.PI, 100.0);
	}

	@DataProvider
	public Object[][] doublePairProvider() {
		return cross(doubleValueProvider(), doubleValueProvider());
	}

	@Test(dataProvider = "doublePairProvider")
	public void testRandomDoublePartition(double avg, double deviation) {

		int size = 10;
		List<Double> result = Common.randomDoublePartition(size, avg, deviation,
				ThreadLocalRandom.current());
		assertEquals(result.size(), size);

		double sum = 0;
		for (double d : result) {
			assertTrue(d <= avg + deviation + e);
			assertTrue(d >= avg - deviation - e);
			sum += d;
		}

		assertEquals(sum / size, avg, e);

	}

}
