package me.arkorwan.srpg.models;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.Test;

import me.arkorwan.srpg.generators.UnitGenerator;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;

public class TestParty {

	UnitGenerator g = new UnitGenerator(new BattleSystem(),
			ThreadLocalRandom.current());

	@Test
	public void testAddRemoveUnit() {

		Party p = new Party();

		assertEquals(p.getMembers().size(), 0);

		Unit u1 = g.generate();
		Unit u2 = g.generate();

		p.addUnit(u1);

		assertEquals(p.getMembers().size(), 1);
		assertSame(p.getMembers().get(0), u1);

		assertFalse(p.removeUnit(u2));
		assertEquals(p.getMembers().size(), 1);
		assertSame(p.getMembers().get(0), u1);

		assertTrue(p.removeUnit(u1));
		assertEquals(p.getMembers().size(), 0);
	}
}
