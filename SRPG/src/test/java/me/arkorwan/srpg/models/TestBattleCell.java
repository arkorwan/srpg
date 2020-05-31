package me.arkorwan.srpg.models;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.Test;

import me.arkorwan.srpg.generators.UnitGenerator;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;

public class TestBattleCell {

	@Test
	public void testSimpleCell() {

		BattleCell cell = new BattleCell(Coordinate.of(0, 0), 0);

		assertEquals(cell.getLocation(), Coordinate.of(0, 0));
		assertEquals(cell.getHeight(), 0);
		assertFalse(cell.isVoid());
		assertFalse(cell.getUnit().isPresent());
	}

	UnitGenerator g = new UnitGenerator(new BattleSystem(),
			ThreadLocalRandom.current());

	@Test
	public void testUnitAssignment() {

		BattleCell cell = new BattleCell(Coordinate.of(0, 0), 0);

		Unit u = g.generate();

		cell.setUnit(u);
		assertSame(cell.getUnit().get(), u);

		cell.clearUnit();
		assertFalse(cell.getUnit().isPresent());
	}

	@Test
	public void testVoid() {

		BattleCell cell = new BattleCell(Coordinate.of(0, 0), 0);

		cell.setToVoid(true);

		assertTrue(cell.isVoid());

		cell.setToVoid(false);

		assertFalse(cell.isVoid());
	}

	@Test
	public void testHeight() {
		BattleCell cell = new BattleCell(Coordinate.of(0, 0), 0);
		cell.setHeight(-1);
		assertEquals(cell.getHeight(), -1);
	}

	@Test
	public void testEquality() {

		BattleCell cell0 = new BattleCell(Coordinate.of(0, 0), 0);
		BattleCell cell1 = new BattleCell(Coordinate.of(0, 1), 0);
		BattleCell cell2 = new BattleCell(Coordinate.of(0, 0), 1);

		assertNotEquals(cell0, cell1);
		assertEquals(cell0, cell2); // ignore height

	}

}
