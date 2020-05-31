package me.arkorwan.srpg.models;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.testng.annotations.Test;

public class TestBattleField {

	@Test
	public void testDimension() {

		BattleField field = new BattleField(8, 12);

		assertEquals(field.getLength(), 12);
		assertEquals(field.getWidth(), 8);

	}

	@Test
	public void testAdjacency() {

		BattleField field = new BattleField(3, 3);

		for (int i = 0; i < field.getWidth(); i++) {
			for (int j = 0; j < field.getLength(); j++) {

				List<BattleCell> adj = new ArrayList<>();

				for (Direction d : Direction.values()) {
					Optional<BattleCell> adjCell = field
							.adjacent(Coordinate.of(i, j), d);

					int nx = i + d.dx;
					int ny = j + d.dy;
					if (nx < 0 || ny < 0 || nx >= field.getWidth()
							|| ny >= field.getLength()) {
						assertFalse(adjCell.isPresent());
					} else {
						BattleCell cell = adjCell.get();
						assertEquals(cell.location, Coordinate.of(nx, ny));
						adj.add(cell);
					}
				}

				List<BattleCell> actualAdj = field
						.adjacent(Coordinate.of(i, j));

				assertEquals(actualAdj.size(), adj.size());

			}
		}

	}

	@Test
	public void testManhattanDistance() {

		int d = Coordinate.of(100, -100)
				.manhattanDistance(Coordinate.of(-80, 70));
		assertEquals(d, 350);

	}

	@Test
	public void testManhattanRange() {

		BattleField field = new BattleField(5, 5);

		Set<BattleCell> cells;
		cells = field.cellInManhattanRange(Coordinate.of(2, 2), 0, x -> true,
				(x, y) -> 1);
		assertEquals(cells.size(), 1);

		cells = field.cellInManhattanRange(Coordinate.of(2, 2), 1, x -> true,
				(x, y) -> 1);
		assertEquals(cells.size(), 5);

		cells = field.cellInManhattanRange(Coordinate.of(2, 2), 2, x -> true,
				(x, y) -> 1);
		assertEquals(cells.size(), 13);

		cells = field.cellInManhattanRange(Coordinate.of(2, 2), 3, x -> true,
				(x, y) -> 1);
		assertEquals(cells.size(), 21);

		cells = field.cellInManhattanRange(Coordinate.of(2, 2), 4, x -> true,
				(x, y) -> 1);
		assertEquals(cells.size(), 25);

		cells = field.cellInManhattanRange(Coordinate.of(2, 2), 4, x -> false,
				(x, y) -> 1);
		assertEquals(cells.size(), 1);

	}

	@Test
	public void testDirectRange() {

		BattleField field = new BattleField(5, 5);

		Set<BattleCell> cells;
		cells = field.cellInDirectRange(Coordinate.of(2, 2), 0, x -> true,
				(x, y) -> 1);
		assertEquals(cells.size(), 0);

		cells = field.cellInDirectRange(Coordinate.of(2, 2), 1, x -> true,
				(x, y) -> 1);
		assertEquals(cells.size(), 4);

		cells = field.cellInDirectRange(Coordinate.of(2, 2), 2, x -> true,
				(x, y) -> 1);
		assertEquals(cells.size(), 8);

		cells = field.cellInDirectRange(Coordinate.of(2, 2), 4, x -> false,
				(x, y) -> 1);
		assertEquals(cells.size(), 0);

	}
}
