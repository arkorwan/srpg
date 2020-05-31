package me.arkorwan.srpg.models;

import static me.arkorwan.testng.utils.DataProviders.cross;
import static me.arkorwan.testng.utils.DataProviders.wrap;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestElemental {

	static final double e = 0.000001;

	@DataProvider
	public Object[][] allElementalPairProvider() {
		Object[][] all = wrap((Object[]) Elemental.values());
		return cross(all, all);
	}

	@Test(dataProvider = "allElementalPairProvider")
	public void testAdvantageZero(Elemental a, Elemental b) {
		if (a == b || !a.isBlackMagic() || !b.isBlackMagic()) {
			assertTrue(a.advantage(b) == 0);
		} else {
			assertTrue(a.advantage(b) != 0);
		}
	}

	@Test
	public void testAllAdvantage() {
		assertTrue(Elemental.Fire.advantage(Elemental.Nature) > 0);
		assertTrue(Elemental.Nature.advantage(Elemental.Water) > 0);
		assertTrue(Elemental.Water.advantage(Elemental.Fire) > 0);
		assertTrue(Elemental.Fire.advantage(Elemental.Water) < 0);
		assertTrue(Elemental.Nature.advantage(Elemental.Fire) < 0);
		assertTrue(Elemental.Water.advantage(Elemental.Nature) < 0);

	}

}
