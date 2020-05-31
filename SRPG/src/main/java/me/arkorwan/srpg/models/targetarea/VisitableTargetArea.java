package me.arkorwan.srpg.models.targetarea;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;

import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.BattleInfoReader;
import me.arkorwan.srpg.models.Coordinate;
import me.arkorwan.srpg.models.Unit;

public class VisitableTargetArea extends AbstractTargetArea {

	public VisitableTargetArea(Unit unit, BattleInfoReader battleInfo) {
		super(unit, battleInfo);
	}

	public static final IntPredicate stepPredicate = (dh -> dh <= 1
			&& dh >= -3);
	public static final BiFunction<BattleCell, BattleCell, Integer> costFunction = ((
			c0, c1) -> Math.abs(c1.getHeight() - c0.getHeight()) <= 1 ? 1 : 2);

	@Override
	protected HashSet<BattleCell> calculateArea(Coordinate coord) {
		Set<BattleCell> result = battleInfo.getField().cellInManhattanRange(
				coord, unit.getMoveRange().getValue(), stepPredicate,
				costFunction);
		return new HashSet<>(result);
	}

}
