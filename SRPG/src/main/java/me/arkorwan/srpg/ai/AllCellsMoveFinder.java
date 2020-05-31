package me.arkorwan.srpg.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.BattleField;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.targetarea.VisitableTargetArea;

/**
 * A utility to find the number of turns a unit needs in order to go from one
 * cell to another.
 * 
 * @author arkorwan
 *
 */
public class AllCellsMoveFinder {

	Map<BattleCell, Map<BattleCell, Integer>> maps = new HashMap<>();

	Unit owner;
	BattleField field;

	public AllCellsMoveFinder(Unit u, BattleField field) {
		this.owner = u;
		this.field = field;
	}

	private void ensure(BattleCell from) {
		if (!maps.containsKey(from)) {

			Map<BattleCell, Integer> distances = field.cellsDistance(
					from.getLocation(), OptionalInt.empty(),
					VisitableTargetArea.stepPredicate,
					VisitableTargetArea.costFunction);

			int moveRange = owner.getMoveRange().getValue();

			// convert distances into steps
			Map<BattleCell, Integer> steps = new HashMap<>();
			for (Map.Entry<BattleCell, Integer> kv : distances.entrySet()) {
				steps.put(kv.getKey(),
						(kv.getValue() + moveRange - 1) / moveRange);
			}
			maps.put(from, steps);
		}
	}

	/**
	 * Number of turns needed to travel between two given cells
	 * 
	 * @param from
	 *            starting cell
	 * @param to
	 *            target cell
	 * @return number of turns
	 */
	public int getTurns(BattleCell from, BattleCell to) {
		ensure(from);
		Map<BattleCell, Integer> ds = maps.get(from);
		if (ds.containsKey(to)) {
			return ds.get(to);
		} else {
			return -1;
		}
	}

	/**
	 * All cells that required exactly n turns to travel to
	 * 
	 * @param from
	 *            starting cell
	 * @param n
	 *            number of turns
	 * @return a set of cells reachable in n turns
	 */
	public Set<BattleCell> cellsWithTurns(BattleCell from, int n) {
		ensure(from);
		return maps.get(from).entrySet().stream()
				.filter(kv -> kv.getValue() == n).map(kv -> kv.getKey())
				.collect(Collectors.toSet());
	}

}
