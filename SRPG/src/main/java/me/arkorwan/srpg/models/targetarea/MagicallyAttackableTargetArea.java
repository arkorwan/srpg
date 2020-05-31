package me.arkorwan.srpg.models.targetarea;

import java.util.HashSet;
import java.util.Set;

import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.BattleInfoReader;
import me.arkorwan.srpg.models.Coordinate;
import me.arkorwan.srpg.models.Unit;

public class MagicallyAttackableTargetArea extends AbstractTargetArea {

	public MagicallyAttackableTargetArea(Unit unit,
			BattleInfoReader battleInfo) {
		super(unit, battleInfo);
	}

	@Override
	protected HashSet<BattleCell> calculateArea(Coordinate coord) {
		Set<BattleCell> result = battleInfo.getField().cellInRange(false, coord,
				6, dh -> true, (c0, c1) -> 1);
		result.removeIf(c -> c.isVoid());
		return new HashSet<>(result);
	}

}
