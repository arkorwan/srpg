package me.arkorwan.srpg.models.calculation;

import static me.arkorwan.testng.utils.DataProviders.wrap;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestShaper {

	static final double e = 0.000001;

	@DataProvider
	public Object[][] doubleValueProvider() {
		return wrap(0.0, 0.2, 0.5, 0.7, 1.0);
	}

	@Test(dataProvider = "doubleValueProvider")
	public void testLinearFunction(double x) {
		Shaper func = ShaperCollection.getShaper("linear");
		assertEquals(func.apply(x), x, e);
	}

	@Test(dataProvider = "doubleValueProvider")
	public void testQuadraticFunction(double x) {

		Shaper func = ShaperCollection.getShaper("power", 2.0);
		assertEquals(func.apply(x), x * x, e);
	}

	@Test(dataProvider = "doubleValueProvider")
	public void testCubeFunction(double x) {

		Shaper func = ShaperCollection.getShaper("power", 3.0);
		assertEquals(func.apply(x), x * x * x, e);
	}

	@Test(dataProvider = "doubleValueProvider")
	public void testSqrtFunction(double x) {

		Shaper func = ShaperCollection.getShaper("root", 2.0);
		assertEquals(func.apply(x), Math.sqrt(x), e);
	}

	@Test(dataProvider = "doubleValueProvider")
	public void testExpFunction(double x) {

		Shaper func = ShaperCollection.getShaper("exp", 10.0);
		assertEquals(func.apply(x), (Math.pow(10.0, x) - 1) / 9.0, e);
	}

	@Test(dataProvider = "doubleValueProvider")
	public void testLogisticFunction(double x) {

		Shaper func = ShaperCollection.getShaper("logistic", 10.0);
		assertEquals(func.apply(x) + func.apply(1.0 - x), 1.0, e);
	}

}
