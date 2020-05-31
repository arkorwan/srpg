package me.arkorwan.srpg.models;

import static org.testng.Assert.*;

import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.Test;

import me.arkorwan.srpg.generators.UnitGenerator;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.srpg.models.calculation.DamageCalculator;

public class TestBattle {

	UnitGenerator g = new UnitGenerator(new BattleSystem(),
			ThreadLocalRandom.current());

	@Test
	public void testBattleSetup() {
		Battle b = new Battle(3, 4, ThreadLocalRandom.current(),
				new DamageCalculator(2, 1, 1, 1));

		assertEquals(b.getField().getWidth(), 3);
		assertEquals(b.getField().getLength(), 4);

		Unit a1 = g.generate();
		Unit a2 = g.generate();
		Unit b1 = g.generate();
		Unit b2 = g.generate();

		Party p1 = b.getParty1();
		Party p2 = b.getParty2();

		b.placeUnitInParty1(a1, Coordinate.of(0, 0));
		b.placeUnit(a2, Coordinate.of(1, 0), p1);

		b.placeUnitInParty2(b1, Coordinate.of(0, 1));
		b.placeUnit(b2, Coordinate.of(1, 1), p2);

		assertEquals(b.getParty1().getMembers().size(), 2);
		assertEquals(b.getParty2().getMembers().size(), 2);

		assertTrue(b.getParty1().getMembers().contains(a1));
		assertTrue(b.getParty1().getMembers().contains(a2));
		assertTrue(b.getParty2().getMembers().contains(b1));
		assertTrue(b.getParty2().getMembers().contains(b2));

		assertEquals(b.getPartyForUnit(a1), p1);
		assertEquals(b.getPartyForUnit(a2), p1);
		assertEquals(b.getPartyForUnit(b1), p2);
		assertEquals(b.getPartyForUnit(b2), p2);

		assertFalse(b.getWinningParty().isPresent());

		b.setUnitLocation(a1,
				b.getField().cellForCoordinate(Coordinate.of(2, 2)));
		assertEquals(b.getUnitLocation(a1).getLocation(), Coordinate.of(2, 2));

		b.setUnitLocation(b1, b.getField().cellForCoordinate(2, 0));
		assertEquals(b.getUnitLocation(b1).getLocation(), Coordinate.of(2, 0));

		Unit a3 = g.generate();
		b.placeUnitInParty1(a3, b.getUnitLocation(a2).getLocation());
		assertEquals(b.getParty1().getMembers().size(), 2);
		assertFalse(b.getParty1().getMembers().contains(a2));
		assertTrue(b.getParty1().getMembers().contains(a3));
		assertEquals(b.getUnitLocation(a3).getLocation(), Coordinate.of(1, 0));

	}

}
