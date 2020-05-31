package me.arkorwan.srpg.models;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

/**
 * A rectangular grid of BattleCells where units can move around.
 * 
 * @author arkorwan
 *
 */
public class BattleField {

	private BattleCell[][] cells;
	private int width;
	private int length;

	// for serializer
	@SuppressWarnings("unused")
	private BattleField() {

	}

	public BattleField(int w, int l) {

		this.width = w;
		this.length = l;
		this.cells = new BattleCell[w][l];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < l; y++) {
				cells[x][y] = new BattleCell(Coordinate.of(x, y), 0);
			}
		}

	}

	public int getWidth() {
		return width;
	}

	public int getLength() {
		return length;
	}

	public int idForCoordinate(Coordinate c) {
		return idForCoordinate(c.x, c.y);
	}

	public int idForCoordinate(int x, int y) {
		return y * width + x;
	}

	public Coordinate coordinateForId(int id) {
		return Coordinate.of(id % width, id / width);
	}

	public BattleCell cellForCoordinate(Coordinate c) {
		return cellForCoordinate(c.x, c.y);
	}

	public BattleCell cellForCoordinate(int x, int y) {
		return cells[x][y];
	}

	public BattleCell cellForId(int id) {
		return cellForCoordinate(coordinateForId(id));
	}

	public void forEachCell(Consumer<BattleCell> consumer) {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < length; y++) {
				consumer.accept(cells[x][y]);
			}
	}

	public void forEachCell(BiConsumer<Coordinate, BattleCell> consumer) {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < length; y++) {
				consumer.accept(Coordinate.of(x, y), cells[x][y]);
			}
	}

	public Optional<BattleCell> adjacent(Coordinate origin,
			Direction direction) {

		int nx = origin.x + direction.dx;
		int ny = origin.y + direction.dy;
		if (nx >= 0 && nx < width && ny >= 0 && ny < length) {
			return Optional.of(cells[nx][ny]);
		} else {
			return Optional.empty();
		}

	}

	public List<BattleCell> adjacent(Coordinate origin) {
		List<BattleCell> result = new ArrayList<>();
		for (Direction d : Direction.values()) {
			Optional<BattleCell> b = adjacent(origin, d);
			if (b.isPresent()) {
				result.add(b.get());
			}
		}
		return result;
	}

	public Map<BattleCell, Integer> cellsDistance(Coordinate origin,
			OptionalInt range, IntPredicate stepPredicate,
			BiFunction<BattleCell, BattleCell, Integer> costFunction) {

		Map<BattleCell, Integer> moveCounter = new HashMap<>();
		Deque<Coordinate> queue = new LinkedList<>();

		queue.addLast(origin);
		moveCounter.put(cellForCoordinate(origin), 0);
		while (!queue.isEmpty()) {
			Coordinate c = queue.removeFirst();
			BattleCell b = cellForCoordinate(c);
			int m = moveCounter.get(cellForCoordinate(c));
			for (BattleCell cell : adjacent(c)) {
				int currentDiff = cell.height - b.height;
				if (stepPredicate.test(currentDiff)) {
					int cost = costFunction.apply(cell, b);
					int nextRange = m + cost;
					if ((!range.isPresent() || nextRange <= range.getAsInt())
							&& (!moveCounter.containsKey(cell)
									|| moveCounter.get(cell) > nextRange)) {
						moveCounter.put(cell, nextRange);
						if (!range.isPresent()
								|| nextRange < range.getAsInt()) {
							queue.addLast(cell.location);
						}
					}
				}
			}

		}

		return moveCounter;

	}

	public Set<BattleCell> cellInManhattanRange(Coordinate origin, int range,
			IntPredicate stepPredicate,
			BiFunction<BattleCell, BattleCell, Integer> costFunction) {
		return cellsDistance(origin, OptionalInt.of(range), stepPredicate,
				costFunction).keySet();
	}

	public Set<BattleCell> cellInDirectRange(Coordinate origin, int range,
			IntPredicate stepPredicate,
			BiFunction<BattleCell, BattleCell, Integer> costFunction) {

		Set<BattleCell> result = new HashSet<>();
		BattleCell originCell = cellForCoordinate(origin);
		for (Direction d : Direction.values()) {
			BattleCell previousCell = originCell;
			int cost = 0;
			while (true) {
				Optional<BattleCell> ob = adjacent(previousCell.location, d);
				if (!ob.isPresent()) {
					break;
				}
				cost += costFunction.apply(previousCell, ob.get());
				if (cost > range) {
					break;
				}
				if (stepPredicate.test(ob.get().height - previousCell.height)) {
					previousCell = ob.get();
					result.add(previousCell);
				} else {
					break;
				}
			}

		}
		return result;
	}

	public Set<BattleCell> cellInRange(boolean isDirect, Coordinate origin,
			int range, IntPredicate stepPredicate,
			BiFunction<BattleCell, BattleCell, Integer> costFunction) {
		return isDirect
				? cellInDirectRange(origin, range, stepPredicate, costFunction)
				: cellInManhattanRange(origin, range, stepPredicate,
						costFunction);
	}

}
